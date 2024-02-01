package dev.avatcher.cinnamon.api.items;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Event passed to {@link ItemBehaviour} methods,
 * representing different events happening to
 * custom items.
 *
 * @see ItemBehaviour
 * @see CustomItem
 */
public interface ItemBehaviourEvent {
    /**
     * Gets the itemstack of the custom item
     * involved in the event.
     *
     * @return Custom item stack
     */
    @NotNull ItemStack getItemStack();
}
