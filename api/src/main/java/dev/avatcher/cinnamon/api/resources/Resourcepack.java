package dev.avatcher.cinnamon.api.resources;

import dev.avatcher.cinnamon.api.Cinnamon;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

/**
 * A Minecraft resourcepack
 */
public interface Resourcepack {
    /**
     * Gets instance of the resourcepack made
     * by Cinnamon
     *
     * @return Cinnamon-made resourcepack
     */
    static Resourcepack getInstance() {
        return Cinnamon.getInstance().getResourcepackServer().getResourcepack();
    }

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
     * Gets SHA1 hash of resourcepack, that can
     * be used to send resourcepack to a player.
     *
     * @return SHA1 hash
     */
    byte[] getSHA1Hash();

    /**
     * Gets bytes of resourcepack's .zip archive.
     *
     * @return Resourcepack's .zip archive
     */
    byte[] getBytes();
}
