package dev.avatcher.cinnamon.core.item.events;

import dev.avatcher.cinnamon.api.items.events.ItemCreateEvent;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.bukkit.inventory.ItemStack;

/**
 * The implementation of {@link ItemCreateEvent}
 */
@Setter
@Getter
@SuperBuilder
public class ItemCreateEventImpl implements ItemCreateEvent {
    private ItemStack itemStack;
}
