package dev.avatcher.cinnamon.core.item.events;

import dev.avatcher.cinnamon.api.items.events.ItemUseEvent;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

/**
 * The implementation of {@link ItemUseEvent}
 */
@Getter
@SuperBuilder
public class ItemUseEventImpl implements ItemUseEvent {
    private ItemStack itemStack;
    private Player player;
    private EquipmentSlot hand;
}
