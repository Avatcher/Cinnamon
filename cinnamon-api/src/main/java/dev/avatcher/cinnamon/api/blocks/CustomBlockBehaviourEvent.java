package dev.avatcher.cinnamon.api.blocks;

import org.bukkit.block.Block;

/**
 * Event passed to {@link CustomBlockBehaviour} methods,
 * representing different events happening to custom blocks.
 *
 * @see CustomBlockBehaviour
 * @see CustomBlock
 */
public interface CustomBlockBehaviourEvent {
    /**
     * Gets custom block this event belongs to.
     *
     * @return Custom block
     */
    CustomBlock getCustomBlock();

    /**
     * Gets Minecraft block of the event.
     *
     * @return Minecraft block
     */
    Block getBlock();
}
