package dev.avatcher.cinnamon.api;

import com.google.common.base.Preconditions;
import org.bukkit.plugin.Plugin;

/**
 * Cinnamon API
 */
public final class Cinnamon {
    private static CinnamonAPI instance;

    /**
     * Gets instance of the Cinnamon API
     *
     * @return {@code null}, if Cinnamon API is not
     *         yet assigned
     */
    public static CinnamonAPI getInstance() {
        return instance;
    }

    /**
     * Sets the instance of the Cinnamon API
     *
     * @param cinnamon Cinnamon instance
     * @throws IllegalStateException If Cinnamon API is already assigned
     */
    public static void setInstance(CinnamonAPI cinnamon) {
        Preconditions.checkArgument(instance == null, "Trying to redefine Cinnamon API instance");
        instance = cinnamon;
    }

    public static void load(Plugin plugin) {
        instance.load(plugin);
    }

    private Cinnamon() {
        throw new UnsupportedOperationException();
    }
}
