package dev.avatcher.cinnamon.item.events;

import dev.avatcher.cinnamon.item.CItem;
import lombok.Builder;
import lombok.Getter;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Event for {@link dev.avatcher.cinnamon.item.CItemBehaviour#onRightClick(CItemRightClickEvent)},
 * when a player right clicks with a custom item in hand
 */
@Getter
@Builder
public class CItemRightClickEvent {
    private CItem cItem;
    private ItemStack itemStack;
    private Player player;
    private Block clickedBlock;
}
