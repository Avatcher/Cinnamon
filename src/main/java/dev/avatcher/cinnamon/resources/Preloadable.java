package dev.avatcher.cinnamon.resources;

import java.io.IOException;
import java.nio.file.Path;

/**
 * A class that can preload its data, that has been
 * obtained previously, from some additional source.
 *
 * @see CinnamonModule
 */
public interface Preloadable {
    /**
     * Preloads data.
     *
     * @param folder Path to the folder where
     *               the data is stored
     */
    void preload(Path folder) throws IOException;

    /**
     * Saves data for later preload.
     *
     * @param folder Path to the folder where
     *               the data is stored
     */
    void savePreload(Path folder) throws IOException;
}
