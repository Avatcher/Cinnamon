package dev.avatcher.cinnamon.item;

import dev.avatcher.cinnamon.item.events.CItemRightClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface CItemBehaviour {
    default ItemStack onCreate(ItemStack itemStack) {
        return itemStack;
    }
    default void onRightClick(@NotNull CItemRightClickEvent event) {}
//    default void onBlockDestroyed() {}
//    default void onEntityKilled() {}
//    default void onSwitchedInHand() {}
//    default void onSwitchedOutHand() {}
}
