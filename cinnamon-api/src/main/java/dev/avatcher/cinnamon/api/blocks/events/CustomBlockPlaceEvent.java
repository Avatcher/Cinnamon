package dev.avatcher.cinnamon.api.blocks.events;

import dev.avatcher.cinnamon.api.blocks.CustomBlockBehaviour;
import dev.avatcher.cinnamon.api.blocks.CustomBlockBehaviourEvent;
import org.bukkit.entity.Player;

/**
 * Custom block event called when block
 * is placed in Minecraft world
 *
 * @see CustomBlockBehaviour
 */
public interface CustomBlockPlaceEvent extends CustomBlockBehaviourEvent {
    /**
     * Gets the player who placed the block.
     *
     * @return Player who placed the block
     */
    Player getPlayer();
}
