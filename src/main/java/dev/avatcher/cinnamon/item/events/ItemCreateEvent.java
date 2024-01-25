package dev.avatcher.cinnamon.item.events;

import dev.avatcher.cinnamon.item.ItemBehaviourEvent;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.bukkit.inventory.ItemStack;

@Getter
@SuperBuilder
public class ItemCreateEvent implements ItemBehaviourEvent {
    @Setter
    private ItemStack itemStack;
}
