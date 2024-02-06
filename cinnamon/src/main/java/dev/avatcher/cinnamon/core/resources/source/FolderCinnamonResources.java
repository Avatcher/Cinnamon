package dev.avatcher.cinnamon.core.resources.source;

import dev.avatcher.cinnamon.core.resources.CinnamonResources;
import lombok.Getter;
import org.bukkit.plugin.Plugin;

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

    /**
     * Creates a new cinnamon resources of a certain folder.
     *
     * @param plugin Plugin owning the resources
     * @param folder Path to the folder containing resources
     */
    public FolderCinnamonResources(Plugin plugin, Path folder) {
        this.plugin = plugin;
        this.folder = folder;
    }

    @Override
    public Path getFolder() {
        return this.folder;
    }

    @Override
    public String toString() {
        return "FolderCResources[plugin=" + this.getPlugin().getName() + ", folder=" + this.getFolder() + "]";
    }

    @Override
    public void close() {}
}
