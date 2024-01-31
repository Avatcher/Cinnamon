package dev.avatcher.cinnamon.core.resources;

import dev.avatcher.cinnamon.core.Cinnamon;
import dev.avatcher.cinnamon.core.block.CBlock;
import dev.avatcher.cinnamon.core.block.NoteblockTune;
import dev.avatcher.cinnamon.core.item.CItem;
import dev.avatcher.cinnamon.core.resources.modules.*;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Manager of Cinnamon resources such as custom items,
 * item models or blocks.
 *
 * @see CinnamonResources
 */
public class CinnamonResourcesManager implements Closeable {
    /**
     * Path to Cinnamon's data folder with preload data
     */
    public static final String PRELOAD_FOLDER = "preload/";
    /**
     * Path to Cinnamon's data folder with resource pack
     */
    public static final String RESOURCE_PACK_FOLDER = "resourcepack/";
    /**
     * Default pack.mcmeta file to be inserted in resourcepack
     */
    private static final byte[] DEFAULT_PACK_MCMETA;
    /**
     * Template of Minecraft model for item containing model
     * overrides depending on item's CustomModelData
     *
     * @see CustomModelData
     */
    private static final String ITEM_MODEL_OVERRIDE_TEMPLATE;

    private static final String BLOCK_MODEL_OVERRIDE_TEMPLATE;

    static {
        try (var in = Cinnamon.class.getClassLoader().getResourceAsStream(RESOURCE_PACK_FOLDER + "pack.mcmeta")) {
            assert in != null;
            DEFAULT_PACK_MCMETA = in.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (var in = Cinnamon.class.getClassLoader().getResourceAsStream(
                RESOURCE_PACK_FOLDER + "item_model_override.tjson")) {
            byte[] bytes = in.readAllBytes();
            ITEM_MODEL_OVERRIDE_TEMPLATE = new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (var in = Cinnamon.class.getClassLoader().getResourceAsStream(
                RESOURCE_PACK_FOLDER + "block_model_override.tjson")) {
            byte[] bytes = in.readAllBytes();
            BLOCK_MODEL_OVERRIDE_TEMPLATE = new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Module of registered CustomModelData
     */
    @Getter
    private final CustomModelDataModule customModelData;
    /**
     * Module of registered custom items
     */
    @Getter
    private final CItemModule customItems;
    /**
     * Module of registered custom blocks
     */
    @Getter
    private final CBlockModule customBlocks;
    /**
     * Module of registered noteblock tunes
     */
    @Getter
    private final NoteblockTuneModule noteblockTunes;
    /**
     * Map of registered custom recipes
     */
    @Getter
    private final RecipeModule customRecipes;

    private final List<CinnamonModule<?>> modules;

    private final Logger log;

    /**
     * Creates a default Resources manager
     * Cinnamon utilizes.
     */
    public CinnamonResourcesManager() {
        this.log = Cinnamon.getInstance().getLogger();
        this.customModelData = new CustomModelDataModule();
        this.customItems = new CItemModule(this.customModelData);
        this.noteblockTunes = new NoteblockTuneModule();
        this.customBlocks = new CBlockModule(this.noteblockTunes, this.customItems);
        this.customRecipes = new RecipeModule();
        this.modules = List.of(
                this.customModelData,
                this.customItems,
                this.noteblockTunes,
                this.customBlocks,
                this.customRecipes
        );
        this.preloadModules();
    }

    @Override
    public void close() {
        this.savePreload();
        customRecipes.getKeys().forEach(Bukkit::removeRecipe);
    }

    /**
     * Returns {@link CustomModelData} with the certain {@link CustomModelData#identifier}.
     * Empty optional will be returned, if CustomModelData was not found.
     *
     * @param identifier Item's identifier
     * @return Optional {@link CustomModelData} (Empty, if CustomModelData was not found)
     */
    public Optional<CustomModelData> getCustomModelData(NamespacedKey identifier) {
        return this.customModelData.get(identifier);
    }

    /**
     * Returns {@link CItem} with the certain {@link CItem#identifier}.
     * Empty optional will be returned, if item was not found.
     *
     * @param identifier Item's identifier
     * @return Optional {@link CItem} (Empty, if item was not found)
     */
    public Optional<CItem> getCItem(NamespacedKey identifier) {
        return this.customItems.get(identifier);
    }


    /**
     * Loads certain Cinnamon resources.
     *
     * @param resources Resources to be loaded
     */
    public void load(CinnamonResources resources) {
        this.modules.forEach(module -> {
            try {
                module.load(resources);
            } catch (IOException e) {
                log.severe("[%s] An error occurred while loading %s from plugin '%s'"
                        .formatted(module.getClass().getSimpleName(), resources,
                                resources.getPlugin().getName()));
                log.log(Level.SEVERE, e.getMessage(), e);
            }
        });
        try {
            this.loadAssets(resources);
        } catch (IOException e) {
            log.severe("An error occurred while loading assets %s from plugin '%s'"
                    .formatted(resources, resources.getPlugin().getName()));
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        this.savePreload();
    }

    /**
     * Loads assets and adds them into Cinnamon's resource pack.
     * Resource pack is created under Cinnamon's plugin folder {@value RESOURCE_PACK_FOLDER}.
     *
     * @param resources Cinnamon resources
     */
    private void loadAssets(@NotNull CinnamonResources resources) throws IOException {
        if (!Files.exists(resources.getAssetsFolder())) return;
        Path resourcePack = Cinnamon.getInstance().getDataFolder().toPath().resolve(RESOURCE_PACK_FOLDER);
        Path resourcePackAssets = resourcePack.resolve(CinnamonResources.ASSETS_FOLDER);
        Files.createDirectories(resourcePackAssets);
        Path assets = resources.getAssetsFolder();
        this.copyAssets(assets, resourcePackAssets);
        Set<Material> uniqueMaterials = this.customItems.getValues().stream()
                .map(CItem::getMaterial)
                .collect(Collectors.toSet());
        for (var material : uniqueMaterials) {
            this.generateItemModelOverrides(resourcePackAssets, material);
        }
        this.generateBlockModelOverrides(resourcePackAssets);
        this.addPackMeta(resourcePack);
    }

    /**
     * Copies assets from one folder into another
     *
     * @param assets             Source folder
     * @param resourcePackAssets Target folder
     */
    private void copyAssets(Path assets, Path resourcePackAssets) throws IOException {
        try (var walker = Files.walk(assets)) {
            walker.filter(Files::isRegularFile).forEach(file -> {
                String relative = assets.relativize(file).toString();
                Path fileInResourcePack = resourcePackAssets.resolve(relative);
                try {
                    Files.createDirectories(fileInResourcePack.resolve(".."));
                    Files.copy(file, resourcePackAssets.resolve(relative));
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
        }
    }

    /**
     * Generates .json model for {@link CItem#DEFAULT_MATERIAL} with all
     * the registered {@link CustomModelData}.
     *
     * @param resourcePackAssets Resource pack assets folder
     */
    private void generateItemModelOverrides(Path resourcePackAssets, Material material) throws IOException {
        NamespacedKey materialKey = material.getKey();
        Path modelOverridesPath = resourcePackAssets
                .resolve("minecraft/models/item/")
                .resolve(materialKey.getKey() + ".json");
        Files.createDirectories(modelOverridesPath.resolve(".."));

        String modelOverridesValues = this.customModelData.getValues().stream()
                .sorted(Comparator.comparingInt(CustomModelData::numeric))
                .map(model -> "    { \"predicate\": { \"custom_model_data\": %d }, \"model\": \"%s\" }"
                        .formatted(model.numeric(), model.identifier()))
                .collect(Collectors.joining(",\n"));
        String modelOverrides = ITEM_MODEL_OVERRIDE_TEMPLATE
                .formatted(
                        CustomModelData.HANDHELD_ITEMS.contains(material)
                                ? "minecraft:item/handheld"
                                : "minecraft:item/generated",
                        materialKey.getNamespace() + ":item/" + materialKey.getKey(),
                        modelOverridesValues);
        Files.write(modelOverridesPath, modelOverrides.getBytes());
    }

    /**
     * Generates .json noteblock model file, that defines different
     * block models depending on its {@link NoteblockTune}.
     *
     * @param resourcePackAssets Resource pack assets folder
     */
    private void generateBlockModelOverrides(Path resourcePackAssets) throws IOException {
        Path modelOverridesPath = resourcePackAssets.resolve("minecraft/blockstates/note_block.json");
        Files.createDirectories(modelOverridesPath.resolve(".."));

        String modelOverridesValues = this.customBlocks.getValues()
                .stream()
                .filter(b -> b != CBlock.NOTEBLOCK)
                .map(block -> "\t\t\"note=%d,instrument=%s\": { \"model\": \"%s\" }"
                        .formatted(block.getTune().note(),
                                block.getTune().getInstrumentMcString(),
                                block.getModel().asString())
                )
                .collect(Collectors.joining(",\n"));
        String modelOverrides = BLOCK_MODEL_OVERRIDE_TEMPLATE.formatted(modelOverridesValues);
        Files.writeString(modelOverridesPath, modelOverrides);
    }

    /**
     * Adds pack.mcmeta in resource pack, if not present.
     *
     * @param resourcePack Resource pack folder
     */
    private void addPackMeta(Path resourcePack) throws IOException {
        Path packMeta = resourcePack.resolve("pack.mcmeta");
        if (Files.exists(packMeta)) return;
        Files.createDirectories(packMeta.resolve(".."));
        Files.write(packMeta, DEFAULT_PACK_MCMETA);
    }

    /**
     * Preloads some data from Cinnamon's {@value #PRELOAD_FOLDER} data folder.
     * This is a very important method, as it loads some previously registered
     * resources in Cinnamon.
     */
    private void preloadModules() {
        Path folder = Cinnamon.getInstance().getDataFolder().toPath().resolve(PRELOAD_FOLDER);
        if (!Files.exists(folder)) return;
        this.modules.forEach(module -> {
            if (module instanceof Preloadable preloadable) {
                try {
                    preloadable.preload(folder);
                } catch (IOException e) {
                    log.severe("[%s] An error occurred while preloading"
                            .formatted(module.getClass().getSimpleName()));
                    log.log(Level.SEVERE, e.getMessage(), e);
                }
            }
        });
    }

    /**
     * Saves some important registered resources into
     * Cinnamon's {@value #PRELOAD_FOLDER} data folder.
     */
    private void savePreload() {
        log.info("Saving preload...");
        Path folder = Cinnamon.getInstance().getDataFolder().toPath().resolve(PRELOAD_FOLDER);
        try {
            Files.createDirectories(folder);
        } catch (IOException e) {
            log.severe("[%s] Failed to create preload folder".formatted(this.getClass().getSimpleName()));
            log.log(Level.SEVERE, e.getMessage(), e);
            return;
        }
        this.modules.forEach(module -> {
            if (module instanceof Preloadable preloadable) {
                try {
                    preloadable.savePreload(folder);
                } catch (IOException e) {
                    log.severe("[%s] An error occurred while saving preload"
                            .formatted(module.getClass().getSimpleName()));
                    log.log(Level.SEVERE, e.getMessage(), e);
                }
            }
        });
        log.info("Preload saved");
    }
}
