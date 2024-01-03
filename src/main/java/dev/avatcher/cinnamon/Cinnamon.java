package dev.avatcher.cinnamon;

import dev.avatcher.cinnamon.commands.CGiveCommand;
import dev.avatcher.cinnamon.exceptions.CinnamonRuntimeException;
import dev.avatcher.cinnamon.item.listeners.ItemEventListener;
import dev.avatcher.cinnamon.resources.CinnamonResources;
import dev.avatcher.cinnamon.resources.CinnamonResourcesManager;
import dev.avatcher.cinnamon.resources.exceptions.CinnamonResourcesInitializationException;
import dev.avatcher.cinnamon.resources.source.JarCinnamonResources;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import dev.jorel.commandapi.CommandAPICommand;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Cinnamon plugin class
 */
public final class Cinnamon extends JavaPlugin {
    @Getter
    private static Cinnamon instance;

    @Getter
    private CinnamonResourcesManager resourcesManager;

    private Logger log;

    public static void load(Plugin plugin) {
        try {
            Cinnamon.load(new JarCinnamonResources(plugin));
        } catch (CinnamonResourcesInitializationException e) {
            throw new CinnamonRuntimeException(e);
        }
    }

    public static void load(CinnamonResources resources) {
        instance.getResourcesManager().load(resources);
    }

    @Override
    public void onLoad() {
        CommandAPI.onLoad(new CommandAPIBukkitConfig(this));
    }

    @Override
    public void onEnable() {
        CommandAPI.onEnable();
        instance = this;
        log = this.getLogger();
        try {
            this.resourcesManager = new CinnamonResourcesManager();
        } catch (IOException e) {
            throw new CinnamonRuntimeException(e);
        }
        this.registerEvents(
                new ItemEventListener()
        );
        this.registerCommands(
                new CGiveCommand().getCommandAPICommand()
        );
    }

    @Override
    public void onDisable() {
        CommandAPI.onDisable();
    }

    /**
     * Registers event listeners
     *
     * @param listeners Event listeners
     * @return this
     */
    @Contract("_ -> this")
    private void registerEvents(Listener @NotNull ... listeners) {
        var manager = Bukkit.getPluginManager();
        for (var listener : listeners) manager.registerEvents(listener, this);
    }

    private void registerCommands(CommandAPICommand... commands) {
        for (var command : commands) {
            command.register();
        }
    }
}
