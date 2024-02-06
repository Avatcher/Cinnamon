package dev.avatcher.cinnamon.api.resources;

import dev.avatcher.cinnamon.api.Cinnamon;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.net.URL;

/**
 * An HTTP server responsible for transmission
 * of the resourcepack to players
 */
public interface ResourcepackServer {
    /**
     * Gets instance of the server made by Cinnamon
     *
     * @return Resourcepack server instance
     */
    static ResourcepackServer getInstance() {
        return Cinnamon.getInstance().getResourcepackServer();
    }

    /**
     * Checks, if server is started yet.
     *
     * @return {@code true}, if server is active
     */
    boolean isStarted();

    /**
     * Starts the server.
     */
    void start();

    /**
     * Stops the server.
     */
    void stop();

    /**
     * Forcibly applies server's resourcepack to selected
     * player with default message.
     *
     * @param player Player to apply resourcepack on
     */
    void applyTo(Player player);

    /**
     * Forcibly applies server's resourcepack to selected
     * player with a certain message.
     *
     * @param player  Player to apply resourcepack on
     * @param message Message to show to player
     */
    void applyTo(Player player, Component message);

    /**
     * Gets URL link used to download the resourcepack.
     *
     * @return URL leading to resourcepack downloading
     */
    URL getDownloadLink();

    /**
     * Gets resourcepack managed by this server.
     *
     * @return Server's resourcepack
     */
    Resourcepack getResourcepack();
}
