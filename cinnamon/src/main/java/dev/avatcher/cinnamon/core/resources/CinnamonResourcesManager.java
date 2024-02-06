package dev.avatcher.cinnamon.core.resources;

import dev.avatcher.cinnamon.api.items.CustomItem;
import dev.avatcher.cinnamon.core.CinnamonPlugin;
import dev.avatcher.cinnamon.core.resources.registries.*;
import dev.avatcher.cinnamon.core.resources.resourcepack.ResourcePackBuilder;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

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
     * Module of registered CustomModelData
     */
    @Getter
    private final CustomModelDataRegistry customModelData;
    /**
     * Module of registered custom items
     */
    @Getter
    private final CustomItemsRegistryImpl customItems;
    /**
     * Module of registered custom blocks
     */
    @Getter
    private final CustomBlocksRegistryImpl customBlocks;
    /**
     * Module of registered noteblock tunes
     */
    @Getter
    private final NoteblockTuneRegistry noteblockTunes;
    /**
     * Map of registered custom recipes
     */
    @Getter
    private final RecipeRegistry customRecipes;

    private final List<CinnamonRegistry<?>> modules;

    @Getter
    private final ResourcePackBuilder resourcePackBuilder;

    private final Logger log;

    /**
     * Creates a default Resources manager
     * Cinnamon utilizes.
     */
    public CinnamonResourcesManager() {
        this.log = CinnamonPlugin.getInstance().getLogger();
        this.customModelData = new CustomModelDataRegistry();
        this.customItems = new CustomItemsRegistryImpl(this.customModelData);
        this.noteblockTunes = new NoteblockTuneRegistry();
        this.customBlocks = new CustomBlocksRegistryImpl(this.noteblockTunes, this.customItems);
        this.customRecipes = new RecipeRegistry();
        this.modules = List.of(
                this.customModelData,
                this.customItems,
                this.noteblockTunes,
                this.customBlocks,
                this.customRecipes
        );
        Path resourcePackFolder = CinnamonPlugin.getInstance().getDataFolder().toPath().resolve(RESOURCE_PACK_FOLDER);
        try {
            this.resourcePackBuilder = new ResourcePackBuilder(resourcePackFolder);
        } catch (IOException e) {
            log.log(Level.SEVERE, "An exception occurred while initializing Resourcepack builder.");
            throw new RuntimeException(e);
        }
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
        return Optional.ofNullable(this.customModelData.get(identifier));
    }

    /**
     * Returns {@link CustomItem} with a certain key.
     * Empty optional will be returned, if item was not found.
     *
     * @param identifier Item's identifier
     * @return Optional {@link CustomItem} (Empty, if item was not found)
     */
    public Optional<CustomItem> getCItem(NamespacedKey identifier) {
        return Optional.ofNullable(this.customItems.get(identifier));
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
            this.resourcePackBuilder.registerAssets(resources);
        } catch (IOException e) {
            log.severe("An error occurred while loading assets %s from plugin '%s'"
                    .formatted(resources, resources.getPlugin().getName()));
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        this.savePreload();
    }

    /**
     * Preloads some data from Cinnamon's {@value #PRELOAD_FOLDER} data folder.
     * This is a very important method, as it loads some previously registered
     * resources in Cinnamon.
     */
    private void preloadModules() {
        Path folder = CinnamonPlugin.getInstance().getDataFolder().toPath().resolve(PRELOAD_FOLDER);
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
        Path folder = CinnamonPlugin.getInstance().getDataFolder().toPath().resolve(PRELOAD_FOLDER);
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
