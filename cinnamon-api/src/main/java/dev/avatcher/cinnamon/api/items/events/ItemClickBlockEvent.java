package dev.avatcher.cinnamon.api.items.events;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

/**
 * Custom item event called when a
 * player clicks on a block with the
 * custom item in on of the hands
 *
 * @see dev.avatcher.cinnamon.api.items.ItemBehaviour
 */
public interface ItemClickBlockEvent extends ItemUseEvent {
    /**
     * Gets the block player clicked on.
     *
     * @return Clicked block
     */
    Block getBlock();

    /**
     * Gets the face of the block
     * player clicked on.
     *
     * @return Clicked block face
     */
    BlockFace getBlockFace();
}
