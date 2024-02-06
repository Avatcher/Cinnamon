package dev.avatcher.cinnamon.core;

import com.google.common.base.Preconditions;
import dev.avatcher.cinnamon.api.Cinnamon;
import dev.avatcher.cinnamon.api.CinnamonAPI;
import dev.avatcher.cinnamon.api.blocks.CustomBlocksRegistry;
import dev.avatcher.cinnamon.api.items.CustomItemsRegistry;
import dev.avatcher.cinnamon.core.block.listeners.NoteblockListener;
import dev.avatcher.cinnamon.core.commands.CGiveCommand;
import dev.avatcher.cinnamon.core.commands.CommandBase;
import dev.avatcher.cinnamon.core.commands.InspectCommand;
import dev.avatcher.cinnamon.core.exceptions.CinnamonRuntimeException;
import dev.avatcher.cinnamon.core.item.listeners.ItemEventListener;
import dev.avatcher.cinnamon.core.resources.CinnamonResources;
import dev.avatcher.cinnamon.core.resources.CinnamonResourcesManager;
import dev.avatcher.cinnamon.core.resources.ResourcepackServerImpl;
import dev.avatcher.cinnamon.core.resources.source.JarCinnamonResources;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.codehaus.plexus.util.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Cinnamon plugin
 */
public final class CinnamonPlugin extends JavaPlugin implements CinnamonAPI {
    /**
     * Instance of the Cinnamon plugin
     */
    @Getter
    private static CinnamonPlugin instance;

    private Logger log;
    /**
     * Default resources manager of Cinnamon
     */
    @Getter
    private CinnamonResourcesManager resourcesManager;
    /**
     * Http server for sending resourcepack
     */
    @Getter
    private ResourcepackServerImpl resourcepackServer;

    /**
     * Loads Cinnamon resources from plugin's jar
     * folder {@value JarCinnamonResources#CINNAMON_FOLDER}.
     *
     * @param plugin Plugin containing Cinnamon resources
     */
    public void load(Plugin plugin) {
        try {
            this.load(new JarCinnamonResources(plugin));
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Loads certain Cinnamon resources.
     *
     * @param resources Resources to be loaded
     */
    public void load(CinnamonResources resources) {
        instance.getResourcesManager().load(resources);
    }

    @Override
    public CustomItemsRegistry getCustomItems() {
        return this.resourcesManager.getCustomItems();
    }

    @Override
    public CustomBlocksRegistry getCustomBlocks() {
        return this.resourcesManager.getCustomBlocks();
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
        this.saveDefaultConfig();
        try {
            FileUtils.deleteDirectory(this.getDataFolder().toPath()
                    .resolve(CinnamonResourcesManager.RESOURCE_PACK_FOLDER)
                    .resolve("assets/")
                    .toFile());
            this.resourcesManager = new CinnamonResourcesManager();
        } catch (IOException e) {
            throw new CinnamonRuntimeException(e);
        }
        this.registerEvents(
                new ItemEventListener(),
                new NoteblockListener()
        );
        this.registerCommands(
                new CGiveCommand(),
                new InspectCommand()
        );
        // Register API instance
        if (Cinnamon.getInstance() == null) {
            Cinnamon.setInstance(this);
        }

        this.getServer().getScheduler().scheduleSyncDelayedTask(this, this::afterAllPluginsEnabled);
    }

    @Override
    public void onDisable() {
        CommandAPI.onDisable();
        if (this.resourcepackServer != null) {
            this.resourcepackServer.stop();
        }
        if (this.resourcesManager.getResourcePackBuilder() != null) {
            try {
                this.resourcesManager.getResourcePackBuilder().clear();
            } catch (IOException e) {
                log.log(Level.WARNING, "An exception occurred while clearing resourcepack.", e);
            }
        }
    }

    public void afterAllPluginsEnabled() {
        try {
            this.resourcesManager.getResourcePackBuilder().build();
        } catch (IOException e) {
            log.log(Level.SEVERE, "An exception occurred while building the resource pack", e);
        }
        this.initializeResourcepackServer();
    }

    /**
     * Registers event listeners.
     *
     * @param listeners Event listeners
     */
    private void registerEvents(Listener @NotNull ... listeners) {
        var manager = Bukkit.getPluginManager();
        for (var listener : listeners) manager.registerEvents(listener, this);
    }

    /**
     * Registers commands.
     *
     * @param commands CommandAPI commands
     */
    private void registerCommands(CommandBase... commands) {
        for (var command : commands) {
            command.getCommandApiCommand().register();
        }
    }

    /**
     * Initializes and starts resourcepack transmitting server
     *
     * @see ResourcepackServerImpl
     */
    private void initializeResourcepackServer() {
        boolean active;
        try {
            var config = CinnamonPlugin.getInstance().getConfig().getConfigurationSection("resourcepack");
            Preconditions.checkNotNull(config);

            active = config.getBoolean("do-transmit");
            int port = config.contains("port")
                    ? config.getInt("port")
                    : 9300;
            URL url = config.contains("url")
                    ? new URL(config.getString("url"))
                    : null;
            String messageJson = config.getString("message");
            Component message = messageJson == null
                    ? Component.text("Please install our resourcepack. It is required for a better server experience.")
                    .color(NamedTextColor.YELLOW)
                    : JSONComponentSerializer.json().deserialize(messageJson);
            this.resourcepackServer = new ResourcepackServerImpl(port, url, message);
            this.registerEvents(this.resourcepackServer);
        } catch (IOException e) {
            log.log(Level.SEVERE, "An exception occurred while initializing the resourcepack transmitting server.", e);
            return;
        }
        try {
            byte[] resourcePack = this.resourcesManager.getResourcePackBuilder().buildZip();
            Path zipPath = CinnamonPlugin.getInstance().getDataFolder().toPath().resolve("resourcepack.zip");
            Files.write(zipPath, resourcePack, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            this.resourcepackServer.setResourcepackBytes(resourcePack);
        } catch (IOException e) {
            log.log(Level.SEVERE, "An exception occurred while building resource pack's Zip archive.", e);
        }
        if (active) this.resourcepackServer.start();
    }
}
