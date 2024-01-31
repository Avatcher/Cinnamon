package dev.avatcher.cdummy;

import dev.avatcher.cinnamon.Cinnamon;
import org.bukkit.plugin.java.JavaPlugin;

public final class CDummy extends JavaPlugin {
    private static CDummy instance;

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
