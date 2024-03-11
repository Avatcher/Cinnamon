package dev.avatcher.cinnamon.core.resources;

import java.io.IOException;
import java.nio.file.Path;

/**
 * A class that can preload its data, that has been
 * obtained previously, from some additional source.
 *
 * @see CinnamonRegistry
 */
public interface Preloadable {
    /**
     * Preloads data.
     *
     * @param folder Path to the folder where
     *               the data is stored
     * @throws IOException If an exception is thrown
     *                     when preloading resources
     */
    void preload(Path folder) throws IOException;

    /**
     * Saves data for later preload.
     *
     * @param folder Path to the folder where
     *               the data is stored
     * @throws IOException If an exception is thrown
     *                     when saving resources
     */
    void savePreload(Path folder) throws IOException;
}
