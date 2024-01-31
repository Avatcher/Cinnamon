package dev.avatcher.cinnamon.item.events;

import dev.avatcher.cinnamon.item.CItem;
import dev.avatcher.cinnamon.item.ItemBehaviourEvent;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.bukkit.inventory.ItemStack;

/**
 * A custom item event, called when a
 * new itemstack of the custom item is created
 * via {@link CItem#getItemStack()}
 *
 * @see dev.avatcher.cinnamon.item.ItemBehaviour
 */
@Setter
@Getter
@SuperBuilder
public class ItemCreateEvent implements ItemBehaviourEvent {
    private ItemStack itemStack;
}
