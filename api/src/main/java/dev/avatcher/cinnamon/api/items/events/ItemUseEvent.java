package dev.avatcher.cinnamon.api.items.events;

import dev.avatcher.cinnamon.api.items.ItemBehaviour;
import dev.avatcher.cinnamon.api.items.ItemBehaviourEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;

/**
 * Event for {@link ItemBehaviour#onUse(ItemUseEvent)},
 * when a player right clicks with a custom item in hand
 */
public interface ItemUseEvent extends ItemBehaviourEvent {
    Player getPlayer();
    EquipmentSlot getHand();
}
