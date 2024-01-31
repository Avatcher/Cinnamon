package dev.avatcher.cinnamon.item;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Representation of an event of a custom item
 *
 * @see ItemBehaviour
 */
public interface ItemBehaviourEvent {
    /**
     * Gets the itemstack of the custom item,
     * involved in this event.
     *
     * @return Itemstack of custom item
     */
    @NotNull ItemStack getItemStack();
}
