package dev.avatcher.cinnamon.item;

import dev.avatcher.cinnamon.item.events.ItemCreateEvent;
import dev.avatcher.cinnamon.item.events.ItemUseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Behaviour of a {@link CItem}
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

//    default void onBlockDestroyed() {}
//    default void onEntityKilled() {}
//    default void onSwitchedInHand() {}
//    default void onSwitchedOutHand() {}
}
