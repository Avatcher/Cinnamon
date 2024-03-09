package dev.avatcher.cinnamon.api.blocks.events;

import dev.avatcher.cinnamon.api.blocks.CustomBlockBehaviour;
import dev.avatcher.cinnamon.api.blocks.CustomBlockBehaviourEvent;

/**
 * <p>
 * Custom block event called when player
 * right-clicks on block.
 * </p><p>
 * By default any custom block is <i>not</i> interactable,
 * unless its behaviour overrides {@link CustomBlockBehaviour#onInteract(CustomBlockInteractEvent)}
 * method. Although, if you want block to be dynamically
 * interactable use {@link #setInteractable(boolean)}.
 * </p>
 *
 * @see CustomBlockBehaviour
 */
public interface CustomBlockInteractEvent extends CustomBlockBehaviourEvent {
    /**
     * Checks, if custom block is interactable.
     *
     * @return {@code true}, if block is interactable
     */
    boolean isInteractable();

    /**
     * Sets, whether block is interactable or not.
     * Blocks that have no response to interaction
     * must have this value of {@code false}
     *
     * @param value Whether block is interactable or not
     */
    void setInteractable(boolean value);
}
