package dev.avatcher.cinnamon.item;

import dev.avatcher.cinnamon.item.events.CItemRightClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Behaviour of a {@link CItem}
 */
public interface CItemBehaviour {
    /**
     * Called when a new {@link ItemStack} of the item
     * is created.
     *
     * @param itemStack Prepared {@link ItemStack}
     * @return Final {@link ItemStack} instance
     */
    default ItemStack onCreate(ItemStack itemStack) {
        return itemStack;
    }

    /**
     * Called when player right clicks with
     * the item in hand.
     *
     * @param event Event
     */
    default void onRightClick(@NotNull CItemRightClickEvent event) {}

//    default void onBlockDestroyed() {}
//    default void onEntityKilled() {}
//    default void onSwitchedInHand() {}
//    default void onSwitchedOutHand() {}
}
