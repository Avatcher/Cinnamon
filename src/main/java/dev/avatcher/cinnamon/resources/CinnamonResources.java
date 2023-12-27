package dev.avatcher.cinnamon.resources;

import org.bukkit.plugin.Plugin;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public interface CinnamonResources extends Closeable {
    String CONFIG_FILE = "cinnamon-resources.yml";
    String ITEMS_FOLDER_PATH = "items/";
    String BLOCKS_FOLDER = "blocks/";
    String ASSETS_FOLDER = "assets/";

    Path getFolder();

    InputStream read(Path path) throws IOException;

    Plugin getPlugin();

    default Path getConfig() {
        return this.getFolder().resolve(CONFIG_FILE);
    }

    default Path getItemsFolder() {
        return this.getFolder().resolve(ITEMS_FOLDER_PATH);
    }

    default Path getBlocksFolder() {
        return this.getFolder().resolve(BLOCKS_FOLDER);
    }

    default Path getAssetsFolder() {
        return this.getFolder().resolve(ASSETS_FOLDER);
    }
}
