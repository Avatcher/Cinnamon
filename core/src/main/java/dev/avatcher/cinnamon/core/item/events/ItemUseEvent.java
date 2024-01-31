package dev.avatcher.cinnamon.core.item.events;

import dev.avatcher.cinnamon.core.item.ItemBehaviour;
import dev.avatcher.cinnamon.core.item.ItemBehaviourEvent;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

/**
 * Event for {@link ItemBehaviour#onUse(ItemUseEvent)},
 * when a player right clicks with a custom item in hand
 */
@Getter
@SuperBuilder
public class ItemUseEvent implements ItemBehaviourEvent {
    private ItemStack itemStack;
    private Player player;
    private EquipmentSlot hand;
}
