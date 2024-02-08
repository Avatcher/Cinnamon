package dev.avatcher.cinnamon.api.blocks.events;

import dev.avatcher.cinnamon.api.blocks.CustomBlockBehaviour;
import dev.avatcher.cinnamon.api.blocks.CustomBlockBehaviourEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Custom block event called when block is
 * destroyed by any reason.
 *
 * @see CustomBlockBehaviour
 */
public interface CustomBlockDestroyEvent extends CustomBlockBehaviourEvent {
    /**
     * Gets block's drop items after its destruction.
     *
     * @return Block's drop items
     */
    List<ItemStack> getDrop();

    /**
     * Sets block's drop items after its destruction.
     *
     * @param drop Block's drop items
     */
    void setDrop(List<ItemStack> drop);
}
