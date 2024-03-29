package dev.avatcher.cinnamon.core.resources.source;

import dev.avatcher.cinnamon.core.resources.CinnamonResources;
import lombok.Getter;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Map;

/**
 * {@link CinnamonResources} implementation for
 * resources inside a .jar file
 */
public class JarCinnamonResources implements CinnamonResources {
    /**
     * Path to resources folder inside jar
     */
    public static final String CINNAMON_FOLDER = "cinnamon/";

    /**
     * Owner plugin of this resources
     */
    @Getter
    private final Plugin plugin;
    /**
     * Cinnamon resources folder inside jar
     */
    @Getter
    private final Path folder;
    /**
     * Class from the .jar file
     */
    private final Class<?> clazz;
    /**
     * File system of the .jar as an archive
     */
    private final FileSystem fileSystem;

    /**
     * Creates new Cinnamon Jar resources
     *
     * @param plugin Plugin owning the resources
     * @param resource Class from the jar containing the resources
     */
    public JarCinnamonResources(Plugin plugin, Class<?> resource) throws IOException, URISyntaxException {
        this.plugin = plugin;
        this.clazz = resource;
        String jarPath = resource
                .getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .toURI()
                .getPath();
        URI uri = URI.create("jar:file:" + jarPath);
        this.fileSystem = FileSystems.newFileSystem(uri, Map.of());
        this.folder = fileSystem.getPath(CINNAMON_FOLDER);

    }

    /**
     * Creates new default Cinnamon Jar resources
     * of a certain plugin.
     *
     * @param plugin Plugin owning the resources
     */
    public JarCinnamonResources(Plugin plugin) throws IOException, URISyntaxException {
        this(plugin, plugin.getClass());
    }

    @Override
    public String toString() {
        return "JarCResources[plugin=" + this.getPlugin().getName() + ", class=" + this.clazz.getName() + "]";
    }

    @Override
    public void close() throws IOException {
        this.fileSystem.close();
    }
}
