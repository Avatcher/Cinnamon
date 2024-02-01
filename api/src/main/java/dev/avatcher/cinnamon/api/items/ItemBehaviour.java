package dev.avatcher.cinnamon.api.items;

import dev.avatcher.cinnamon.api.items.events.ItemCreateEvent;
import dev.avatcher.cinnamon.api.items.events.ItemUseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Behaviour of a custom item
 *
 * @see CustomItem
 */
public interface ItemBehaviour {
    /**
     * Called when a new {@link ItemStack} of the item
     * is created. Use {@link ItemCreateEvent#setItemStack(ItemStack)}
     * to modify created item
     *
     * @param event Event
     */
    default void onCreate(ItemCreateEvent event) {}

    /**
     * Called when player right clicks with
     * the item in hand.
     *
     * @param event Event
     */
    default void onUse(@NotNull ItemUseEvent event) {}
}
