package dev.avatcher.cinnamon.resources.source;

import dev.avatcher.cinnamon.resources.CinnamonResources;
import dev.avatcher.cinnamon.resources.exceptions.CinnamonResourcesInitializationException;
import lombok.Getter;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
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
    public static String CINNAMON_FOLDER = "cinnamon/";

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

    public JarCinnamonResources(Plugin plugin, Class<?> resource) throws CinnamonResourcesInitializationException {
        this.plugin = plugin;
        this.clazz = resource;
        try {
            String jarPath = resource
                    .getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI()
                    .getPath();
            URI uri = URI.create("jar:file:" + jarPath);
            this.fileSystem = FileSystems.newFileSystem(uri, Map.of());
            this.folder = fileSystem.getPath(CINNAMON_FOLDER);
        } catch (Throwable e) {
            throw new CinnamonResourcesInitializationException(this, e);
        }
    }

    public JarCinnamonResources(Plugin plugin) throws CinnamonResourcesInitializationException {
        this(plugin, plugin.getClass());
    }

    @Override
    public InputStream read(Path path) {
        return this.clazz.getClassLoader().getResourceAsStream(path.toString());
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
