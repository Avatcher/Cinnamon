package dev.avatcher.cinnamon.resources.source;

import dev.avatcher.cinnamon.resources.CinnamonResources;
import lombok.Getter;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * {@link CinnamonResources} implementation for
 * resources inside a regular folder
 */
public class FolderCinnamonResources implements CinnamonResources {
    /**
     * Owner plugin of this resources
     */
    @Getter
    private final Plugin plugin;
    /**
     * Folder with the resources inside
     */
    private final Path folder;

    public FolderCinnamonResources(Plugin plugin, Path folder) {
        this.plugin = plugin;
        this.folder = folder;
    }

    @Override
    public Path getFolder() {
        return this.folder;
    }

    @Override
    public InputStream read(Path path) throws IOException {
        return Files.newInputStream(path);
    }

    @Override
    public String toString() {
        return "FolderCResources[plugin=" + this.getPlugin().getName() + ", folder=" + this.getFolder() + "]";
    }

    @Override
    public void close() {}
}
