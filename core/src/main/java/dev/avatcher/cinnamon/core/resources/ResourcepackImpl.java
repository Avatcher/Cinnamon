package dev.avatcher.cinnamon.core.resources;

import dev.avatcher.cinnamon.api.resources.Resourcepack;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

/**
 * The implementation of {@link Resourcepack} managed
 * by a {@link dev.avatcher.cinnamon.api.resources.ResourcepackServer}
 */
public class ResourcepackImpl implements Resourcepack {
    private final ResourcepackServerImpl server;

    /**
     * Creates a resourcepack managed by certain server.
     *
     * @param server Server managing the resourcepack
     */
    public ResourcepackImpl(ResourcepackServerImpl server) {
        this.server = server;
    }

    @Override
    public void applyTo(Player player) {
        this.server.applyTo(player);
    }

    @Override
    public void applyTo(Player player, Component message) {
        this.server.applyTo(player, message);
    }

    @Override
    public byte[] getSHA1Hash() {
        return this.server.getResourcepackSHA1();
    }

    @Override
    public byte[] getBytes() {
        return this.server.getResourcepackBytes();
    }
}
