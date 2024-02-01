package dev.avatcher.cinnamon.api;

import com.google.common.base.Preconditions;
import lombok.Getter;
import org.bukkit.plugin.Plugin;

/**
 * Cinnamon API
 */
public final class Cinnamon {
    /**
     * Instance of the Cinnamon API.
     */
    @Getter
    private static CinnamonAPI instance;

    /**
     * Sets the instance of the Cinnamon API.
     *
     * @param cinnamon Cinnamon instance
     * @throws IllegalStateException If Cinnamon API is already assigned
     */
    public static void setInstance(CinnamonAPI cinnamon) {
        Preconditions.checkArgument(instance == null, "Trying to redefine Cinnamon API instance");
        instance = cinnamon;
    }

    /**
     * Loads Cinnamon resources of a certain plugin.
     *
     * @param plugin Plugin containing Cinnamon resources
     */
    public static void load(Plugin plugin) {
        instance.load(plugin);
    }

    private Cinnamon() {
        throw new UnsupportedOperationException();
    }
}
