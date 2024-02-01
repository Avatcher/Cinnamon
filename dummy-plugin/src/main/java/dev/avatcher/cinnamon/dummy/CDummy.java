package dev.avatcher.cinnamon.dummy;

import dev.avatcher.cinnamon.api.Cinnamon;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The main class of the plugin
 */
public final class CDummy extends JavaPlugin {
    private static CDummy instance;

    /**
     * Gets instance of the plugin.
     *
     * @return {@code null}, if called before the plugin
     *         has been initialized
     */
    public static CDummy getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        Cinnamon.load(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
