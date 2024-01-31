package dev.avatcher.cinnamon.core.resources;

import dev.avatcher.cinnamon.core.item.CItem;
import org.bukkit.plugin.Plugin;

import java.io.Closeable;
import java.nio.file.Path;

/**
 * A source of Cinnamon resources
 */
public interface CinnamonResources extends Closeable {
    /**
     * Default path to configuration file
     */
    String CONFIG_FILE = "cinnamon-resources.yml";
    /**
     * Default path to folder with custom items
     *
     * @see CItem
     */
    String ITEMS_FOLDER = "items/";
    /**
     * Default path to folder with custom blocks
     */
    String BLOCKS_FOLDER = "blocks/";
    /**
     * Default path to folder with resource pack assets
     */
    String ASSETS_FOLDER = "assets/";
    /**
     * Default path to folder with item models inside {@value #ASSETS_FOLDER}
     */
    String MODELS_FOLDER = "models/";
    /**
     * Default path to folder with recipes
     */
    String RECIPES_FOLDER = "recipes/";

    /**
     * Gets {@link Path} to folder with resources.
     *
     * @return {@link Path} to folder with resources
     */
    Path getFolder();

    /**
     * Gets plugin resources belong to
     *
     * @return Resources owner plugin
     */
    Plugin getPlugin();

    /**
     * Gets path to configuration file.
     *
     * @return Path to configuration file
     */
    default Path getConfig() {
        return this.getFolder().resolve(CONFIG_FILE);
    }

    /**
     * Gets path to items folder.
     *
     * @return Path to items folder
     */
    default Path getItemsFolder() {
        return this.getFolder().resolve(ITEMS_FOLDER);
    }

    /**
     * Gets path to item models folder.
     *
     * @return Path to item models folder
     */
    default Path getCustomModelsFolder() {
        return this.getAssetsFolder()
                .resolve(this.getPlugin().getName().toLowerCase())
                .resolve(MODELS_FOLDER);
    }

    /**
     * Gets path to blocks folder.
     *
     * @return Path to blocks folder
     */
    default Path getBlocksFolder() {
        return this.getFolder().resolve(BLOCKS_FOLDER);
    }

    /**
     * Gets path to assets folder.
     *
     * @return Path to assets folder
     */
    default Path getAssetsFolder() {
        return this.getFolder().resolve(ASSETS_FOLDER);
    }

    /**
     * Gets path to recipes folder.
     *
     * @return Path to recipes folder
     */
    default Path getRecipesFolder() {
        return this.getFolder().resolve(RECIPES_FOLDER);
    }
}
