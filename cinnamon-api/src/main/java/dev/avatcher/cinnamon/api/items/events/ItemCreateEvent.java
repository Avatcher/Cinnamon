package dev.avatcher.cinnamon.api.items.events;

import dev.avatcher.cinnamon.api.items.CustomItem;
import dev.avatcher.cinnamon.api.items.ItemBehaviourEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Event called when a new {@link ItemStack} of
 * a custom item with {@link CustomItem#createItemStack()}
 * is created.
 *
 * @see dev.avatcher.cinnamon.api.items.ItemBehaviour
 */
public interface ItemCreateEvent extends ItemBehaviourEvent {
    /**
     * Sets the item stack of the
     * custom item.
     *
     * @param itemStack Item stack to set
     */
    void setItemStack(ItemStack itemStack);
}
