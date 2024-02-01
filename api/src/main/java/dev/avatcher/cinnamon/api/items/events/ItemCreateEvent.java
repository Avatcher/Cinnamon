package dev.avatcher.cinnamon.api.items.events;

import dev.avatcher.cinnamon.api.items.ItemBehaviourEvent;
import org.bukkit.inventory.ItemStack;

public interface ItemCreateEvent extends ItemBehaviourEvent {
    void setItemStack(ItemStack itemStack);
}
