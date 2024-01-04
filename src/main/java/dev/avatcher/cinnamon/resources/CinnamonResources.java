package dev.avatcher.cinnamon.resources;

import org.bukkit.plugin.Plugin;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

/**
 * A source of Cinnamon resources
 */
public interface CinnamonResources extends Closeable {
    /**
     * Default path to configuration file
     *
     * @see dev.avatcher.cinnamon.config.CinnamonResourcesConfig
     */
    String CONFIG_FILE = "cinnamon-resources.yml";
    /**
     * Default path to folder with custom items
     *
     * @see dev.avatcher.cinnamon.item.CItem
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
     * Returns {@link InputStream} of certain file in resources.
     *
     * @param path Path to the file
     * @return File's {@link InputStream}
     */
    InputStream read(Path path) throws IOException;

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
}
