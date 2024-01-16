package dev.avatcher.cinnamon.resources;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dev.avatcher.cinnamon.Cinnamon;
import dev.avatcher.cinnamon.block.CBlock;
import dev.avatcher.cinnamon.block.NoteblockTune;
import dev.avatcher.cinnamon.exceptions.CinnamonRuntimeException;
import dev.avatcher.cinnamon.item.CItem;
import dev.avatcher.cinnamon.json.NamespacedKeyAdapter;
import dev.avatcher.cinnamon.resources.config.CinnamonResourcesConfig;
import dev.avatcher.cinnamon.resources.exceptions.CinnamonResourcesLoadException;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
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
                RESOURCE_PACK_FOLDER + "item_model_override.json")) {
            byte[] bytes = in.readAllBytes();
            ITEM_MODEL_OVERRIDE_TEMPLATE = new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (var in = Cinnamon.class.getClassLoader().getResourceAsStream(
                RESOURCE_PACK_FOLDER + "block_model_override.json")) {
            byte[] bytes = in.readAllBytes();
            BLOCK_MODEL_OVERRIDE_TEMPLATE = new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Map of registered CustomModelData
     */
    @Getter
    private final Map<NamespacedKey, CustomModelData> customModelMap;
    /**
     * Last {@link CustomModelData#numeric} assigned in CustomModelData registration
     */
    private int lastCustomModelNumeric = CustomModelData.START_NUMERIC;
    /**
     * Map of registered custom items
     */
    @Getter
    private final Map<NamespacedKey, CItem> customItemMap;
    /**
     * Map of registered custom blocks
     */
    @Getter
    private final Map<NamespacedKey, CBlock> customBlockMap;
    /**
     * Map of registered custom recipes
     */
    @Getter
    private final Map<NamespacedKey, Recipe> customRecipeMap;

    private final Logger log;

    public CinnamonResourcesManager() throws IOException {
        this.log = Cinnamon.getInstance().getLogger();
        this.customModelMap = new HashMap<>();
        this.customItemMap = new HashMap<>();
        this.customBlockMap = new HashMap<>();
        this.customBlockMap.put(CBlock.NOTEBLOCK.getIdentifier(), CBlock.NOTEBLOCK);
        this.customRecipeMap = new HashMap<>();
        this.preload();
    }

    @Override
    public void close() throws IOException {
        this.savePreload();
        customRecipeMap.keySet().forEach(Bukkit::removeRecipe);
    }

    /**
     * Returns {@link CustomModelData} with the certain {@link CustomModelData#identifier}.
     * Empty optional will be returned, if CustomModelData was not found.
     *
     * @param identifier Item's identifier
     * @return Optional {@link CustomModelData} (Empty, if CustomModelData was not found)
     */
    public Optional<CustomModelData> getCustomModelData(NamespacedKey identifier) {
        return Optional.ofNullable(this.customModelMap.get(identifier));
    }

    /**
     * Returns {@link CItem} with the certain {@link CItem#identifier}.
     * Empty optional will be returned, if item was not found.
     *
     * @param identifier Item's identifier
     * @return Optional {@link CItem} (Empty, if item was not found)
     */
    public Optional<CItem> getCItem(NamespacedKey identifier) {
        return Optional.ofNullable(this.customItemMap.get(identifier));
    }

    /**
     * Registers {@link CustomModelData}
     *
     * @param customModelData CustomModelData to be registered
     */
    public void registerCustomModel(CustomModelData customModelData) {
        this.customModelMap.put(customModelData.identifier(), customModelData);
        log.info("Registered item model " + customModelData.identifier());
    }

    /**
     * Registers {@link CItem}
     *
     * @param cItem CItem to be registered
     */
    public void registerCItem(CItem cItem) {
        this.customItemMap.put(cItem.getIdentifier(), cItem);
        log.info("Registered item " + cItem.getIdentifier());
    }

    public void registerCBlock(CBlock cBlock) {
        this.customBlockMap.put(cBlock.getIdentifier(), cBlock);
        log.info("Registered block " + cBlock.getIdentifier());
    }

    /**
     * Registers {@link Recipe}
     *
     * @param identifier Identifier of the recipe
     * @param recipe Recipe
     */
    public void registerRecipe(NamespacedKey identifier, Recipe recipe) {
        this.customRecipeMap.put(identifier, recipe);
        Bukkit.addRecipe(recipe, true);
        log.info("Registered recipe " + identifier);
    }

    /**
     * Loads certain Cinnamon resources.
     *
     * @param resources Resources to be loaded
     */
    public void load(CinnamonResources resources) {
        try (var loader = new CinnamonResourcesLoader(resources)) {
            CinnamonResourcesConfig config = loader.loadConfig();
            loader.setConfig(config);

            this.loadItemModels(loader);
            this.loadItems(loader);
            this.loadRecipes(loader);
            this.loadBlocks(loader);
            this.loadAssets(loader);

            this.savePreload();
        } catch (CinnamonResourcesLoadException e) {
            log.severe(e.getMessage());
            log.log(Level.SEVERE, e.getCause().getMessage(), e.getCause());
            log.severe("Failed to load resources '" + resources + "' for plugin '"
                    + resources.getPlugin().getName() + "'");
        } catch (IOException e) {
            throw new CinnamonRuntimeException(e);
        }
    }

    /**
     * Loads assets and adds them into Cinnamon's resource pack.
     * Resource pack is created under Cinnamon's plugin folder {@value RESOURCE_PACK_FOLDER}.
     *
     * @param loader Loader of Cinnamon resources
     */
    private void loadAssets(@NotNull CinnamonResourcesLoader loader) throws CinnamonResourcesLoadException {
        if (!Files.exists(loader.getResources().getAssetsFolder())) return;
        Path resourcePack = Cinnamon.getInstance().getDataFolder().toPath().resolve(RESOURCE_PACK_FOLDER);
        Path resourcePackAssets = resourcePack.resolve(CinnamonResources.ASSETS_FOLDER);
        try {
            Files.createDirectories(resourcePackAssets);
            Path assets = loader.getResources().getAssetsFolder();
            this.copyAssets(assets, resourcePackAssets);
            this.generateItemModelOverrides(resourcePackAssets);
            this.generateBlockModelOverrides(resourcePackAssets);
            this.addPackMeta(resourcePack);
        } catch (IOException e) {
            throw new CinnamonResourcesLoadException(loader.getResources(), "Failed to load assets from resource "
                    + loader.getResources(), e);
        }
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
     * Generates .json model for {@link CItem#MATERIAL} with all
     * the registered {@link CustomModelData}.
     *
     * @param resourcePackAssets Resource pack assets folder
     */
    private void generateItemModelOverrides(Path resourcePackAssets) throws IOException {
        Path modelOverridesPath = resourcePackAssets
                .resolve("minecraft/models/item/")
                .resolve(String.valueOf(CItem.MATERIAL).toLowerCase() + ".json");
        Files.createDirectories(modelOverridesPath.resolve(".."));

        String modelOverridesValues = this.customModelMap.values().stream()
                .sorted(Comparator.comparingInt(CustomModelData::numeric))
                .map(model -> "\t\t{ \"predicate\": { \"custom_model_data\": %d }, \"model\": \"%s\" }"
                        .formatted(model.numeric(), model.identifier()))
                .collect(Collectors.joining(",\n"));
        NamespacedKey materialKey = CItem.MATERIAL.getKey();
        String modelOverrides = ITEM_MODEL_OVERRIDE_TEMPLATE
                .formatted(materialKey.getNamespace() + ":item/" + materialKey.getKey(), modelOverridesValues);
        Files.write(modelOverridesPath, modelOverrides.getBytes());
    }

    private void generateBlockModelOverrides(Path resourcePackAssets) throws IOException {
        Path modelOverridesPath = resourcePackAssets.resolve("minecraft/blockstates/note_block.json");
        Files.createDirectories(modelOverridesPath.resolve(".."));

        String modelOverridesValues = this.customBlockMap.values()
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
     * Loads CustomModelData from {@link CinnamonResourcesLoader}
     *
     * @param loader Loader of Cinnamon resources
     */
    private void loadItemModels(@NotNull CinnamonResourcesLoader loader) throws CinnamonResourcesLoadException {
        List<CustomModelData> models = loader.loadCustomModelIdentifiers().stream()
                .filter(modelName -> !this.customModelMap.containsKey(modelName))
                .map(modelName -> new CustomModelData(modelName, ++this.lastCustomModelNumeric))
                .toList();
        models.forEach(this::registerCustomModel);
        if (!models.isEmpty()) {
            log.info("Loaded a total of " + models.size() + " new item models for plugin '"
                    + loader.getResources().getPlugin().getName() + "'");
        }
    }

    /**
     * Loads custom items from {@link CinnamonResourcesLoader}
     *
     * @param loader Loader of Cinnamon resources
     */
    private void loadItems(@NotNull CinnamonResourcesLoader loader) throws CinnamonResourcesLoadException {
        List<CItem> items = loader.loadItems();
        items.forEach(this::registerCItem);
        log.info("Loaded a total of " + items.size() + " items for plugin '"
                + loader.getResources().getPlugin().getName() + "'");
    }

    private void loadBlocks(@NotNull CinnamonResourcesLoader loader) throws CinnamonResourcesLoadException {
        List<CBlock.RegistrationRequest> registrationRequests = loader.loadBlocks();
        for (var request : registrationRequests) {
            if (this.customBlockMap.containsKey(request.getIdentifier())) continue;
            NoteblockTune noteblockTune = this.getFreeNoteblockTune(request.getIdentifier());
            CBlock cBlock = new CBlock(request.getIdentifier(), request.getModel(), noteblockTune);
            this.registerCBlock(cBlock);
        }
        log.info("Loaded a total of " + registrationRequests.size() + " blocks for plugin '"
                + loader.getResources().getPlugin().getName() + "'");
    }

    private NoteblockTune getFreeNoteblockTune(NamespacedKey identifier) {
        byte lastNote = this.customBlockMap.values().stream()
                .map(CBlock::getTune)
                .map(NoteblockTune::note)
                .max(Byte::compareTo)
                .orElse((byte) 0);
        byte lastInstrument = this.customBlockMap.values().stream()
                .map(CBlock::getTune)
                .map(NoteblockTune::instrument)
                .max(Byte::compareTo)
                .orElse((byte) 0);
        boolean nextInstrument = lastNote + 1 > 24;
        byte note = nextInstrument
                ? (byte) 0
                : ++lastNote;
        byte instrument = nextInstrument
                ? ++lastInstrument
                : lastInstrument;

        NoteblockTune tune = new NoteblockTune(note, instrument);
        return tune;
    }

    /**
     * Loads recipes from {@link CinnamonResourcesLoader}
     *
     * @param loader Loader of Cinnamon resources
     */
    private void loadRecipes(@NotNull CinnamonResourcesLoader loader) throws CinnamonResourcesLoadException {
        List<Map.Entry<NamespacedKey, Recipe>> recipes = loader.loadRecipes();
        recipes.forEach(recipe -> this.registerRecipe(recipe.getKey(), recipe.getValue()));
        log.info("Loaded a total of " + recipes.size() + " recipes for plugin '"
                + loader.getResources().getPlugin().getName() + "'");
    }

    /**
     * Preloads some data from Cinnamon's {@value #PRELOAD_FOLDER} data folder.
     * This is a very important method, as it loads some previously registered
     * resources in Cinnamon.
     */
    private void preload() throws IOException {
        Path folder = Cinnamon.getInstance().getDataFolder().toPath().resolve(PRELOAD_FOLDER);
        if (!Files.exists(folder)) return;

        Path modelsPath = folder.resolve("CustomModelData.json");
        if (Files.exists(modelsPath)) {
            try (var modelsReader = new InputStreamReader(Files.newInputStream(modelsPath))) {
                Map<String, Integer> itemModels = new Gson().fromJson(modelsReader, new TypeToken<>() {
                });
                for (var entry : itemModels.entrySet()) {
                    var model = new CustomModelData(NamespacedKey.fromString(entry.getKey()), entry.getValue());
                    this.customModelMap.put(model.identifier(), model);
                    log.info("Preloaded item model " + model.identifier());
                }
                log.info("Preloaded a total of " + itemModels.size() + " item models");
                this.lastCustomModelNumeric = itemModels.values().stream()
                        .max(Integer::compareTo)
                        .orElse(CustomModelData.START_NUMERIC);
            }
        }
        Path tunePath = folder.resolve("Blocks.json");
        if (Files.exists(tunePath)) {
            try (var reader = new InputStreamReader(Files.newInputStream(tunePath))) {
                Gson gson = new GsonBuilder()
                        .registerTypeAdapter(NamespacedKey.class, new NamespacedKeyAdapter())
                        .create();
                List<CBlock> blocks = gson.fromJson(reader, new TypeToken<>(){});
                for (var block : blocks) {
                    this.customBlockMap.put(block.getIdentifier(), block);
                    log.info("Preloaded block " + block.getIdentifier());
                }
                log.info("Preloaded a total of " + blocks.size() + " blocks");
            }
        }
    }

    /**
     * Saves some important registered resources into
     * Cinnamon's {@value #PRELOAD_FOLDER} data folder.
     */
    private void savePreload() throws IOException {
        Path folder = Cinnamon.getInstance().getDataFolder().toPath().resolve(PRELOAD_FOLDER);
        Files.createDirectories(folder);

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(NamespacedKey.class, new NamespacedKeyAdapter())
                .setPrettyPrinting()
                .create();
        {
            Path modelsPath = folder.resolve("CustomModelData.json");
            Map<String, Integer> models = this.customModelMap.values().stream()
                    .map(model -> Map.entry(model.identifier().asString(), model.numeric()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            Files.writeString(modelsPath, gson.toJson(models));
        }
        {
            Path blocksPath = folder.resolve("Blocks.json");
            Files.writeString(blocksPath, gson.toJson(this.customBlockMap.values().stream()
                    .filter(b -> b != CBlock.NOTEBLOCK)));
        }
    }
}
