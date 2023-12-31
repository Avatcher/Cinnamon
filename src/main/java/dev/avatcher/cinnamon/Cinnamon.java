package dev.avatcher.cinnamon;

import dev.avatcher.cinnamon.commands.CGiveCommand;
import dev.avatcher.cinnamon.exceptions.CinnamonRuntimeException;
import dev.avatcher.cinnamon.item.listeners.ItemEventListener;
import dev.avatcher.cinnamon.resources.CinnamonResources;
import dev.avatcher.cinnamon.resources.CinnamonResourcesManager;
import dev.avatcher.cinnamon.resources.ResourcepackServer;
import dev.avatcher.cinnamon.resources.exceptions.CinnamonResourcesInitializationException;
import dev.avatcher.cinnamon.resources.source.JarCinnamonResources;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import dev.jorel.commandapi.CommandAPICommand;
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
import java.io.UncheckedIOException;
import java.nio.file.*;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Cinnamon plugin
 */
public final class Cinnamon extends JavaPlugin {
    /**
     * Instance of the Cinnamon plugin
     */
    @Getter
    private static Cinnamon instance;

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
    private ResourcepackServer resourcepackServer;

    /**
     * Loads Cinnamon resources from plugin's jar
     * folder {@value dev.avatcher.cinnamon.resources.source.JarCinnamonResources#CINNAMON_FOLDER}.
     *
     * @param plugin Plugin containing Cinnamon resources
     */
    public static void load(Plugin plugin) {
        try {
            Cinnamon.load(new JarCinnamonResources(plugin));
        } catch (CinnamonResourcesInitializationException e) {
            throw new CinnamonRuntimeException(e);
        }
    }

    /**
     * Loads certain Cinnamon resources.
     *
     * @param resources Resources to be loaded
     */
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
                new ItemEventListener()
        );
        this.registerCommands(
                new CGiveCommand().getCommandAPICommand()
        );
        // Runs code AFTER all the plugins are loaded
        this.getServer().getScheduler().scheduleSyncDelayedTask(this, this::initializeResourcepackServer);
    }

    @Override
    public void onDisable() {
        CommandAPI.onDisable();
        this.resourcepackServer.stop();
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
    private void registerCommands(CommandAPICommand... commands) {
        for (var command : commands) {
            command.register();
        }
    }

    /**
     * Initializes and starts resourcepack transmitting server
     *
     * @see ResourcepackServer
     */
    private void initializeResourcepackServer() {
        boolean active = true;
        try {
            var config = Cinnamon.getInstance().getConfig().getConfigurationSection("resourcepack");
            Objects.requireNonNull(config);
            active = config.getBoolean("do-transmit");
            int port = config.getInt("port");
            String context = config.getString("context");
            if (context == null) context = "resourcepack";
            String messageJson = config.getString("message");
            Component message = messageJson == null
                    ? Component.text("Please install our resourcepack. It is required for a better server experience.")
                    .color(NamedTextColor.YELLOW)
                    : JSONComponentSerializer.json().deserialize(messageJson);
            this.resourcepackServer = new ResourcepackServer(port, context, message);
            this.registerEvents(this.resourcepackServer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Path zipPath = Cinnamon.getInstance().getDataFolder().toPath()
                .resolve("resourcepack.zip");
        Path resourcepack = Cinnamon.getInstance().getDataFolder().toPath()
                .resolve(CinnamonResourcesManager.RESOURCE_PACK_FOLDER);
        try {
            if (!Files.exists(resourcepack)) {
                log.warning("Resourcepack is empty. Transmitting server is not started");
                return;
            }
            Files.deleteIfExists(zipPath);
            try (FileSystem fs = FileSystems.newFileSystem(zipPath, Map.of("create", "true"));
                 var walker = Files.walk(resourcepack)) {
                walker.filter(Files::isRegularFile)
                        .forEach(file -> {
                            Path relative = resourcepack.relativize(file);
                            Path inZip = fs.getPath(relative.toString());
                            try {
                                Files.createDirectories(inZip.resolve(".."));
                                Files.copy(file, inZip, StandardCopyOption.REPLACE_EXISTING);
                            } catch (IOException e) {
                                throw new UncheckedIOException(e);
                            }
                        });
                log.info("Created resourcepack's zip");
            }
            byte[] resourcePack = Files.readAllBytes(zipPath);
            this.resourcepackServer.setResourcePack(resourcePack);
            if (active) this.resourcepackServer.start();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
