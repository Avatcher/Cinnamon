package dev.avatcher.cinnamon.api.blocks;

import dev.avatcher.cinnamon.api.blocks.events.CustomBlockBreakEvent;
import dev.avatcher.cinnamon.api.blocks.events.CustomBlockDestroyEvent;
import dev.avatcher.cinnamon.api.blocks.events.CustomBlockInteractEvent;
import dev.avatcher.cinnamon.api.blocks.events.CustomBlockPlaceEvent;

/**
 * Behaviour of a custom block
 *
 * @see CustomBlock
 */
public interface CustomBlockBehaviour {
    /**
     * Called when player right-clicks the block.
     *
     * @param event Event
     */
    default void onInteract(CustomBlockInteractEvent event) {
        // Makes block not interactable by default
        event.setInteractable(false);
    }

    /**
     * Called when block is placed in the world.
     *
     * @param event Event
     */
    default void onPlace(CustomBlockPlaceEvent event) {}

    /**
     * Called when block is destroyed by any reason.
     *
     * @param event Event
     */
    default void onDestroy(CustomBlockDestroyEvent event) {}

    /**
     * Called when block is broken by player.
     *
     * @param event Event
     */
    default void onBreak(CustomBlockBreakEvent event) {
        this.onDestroy(event);
    }
}
