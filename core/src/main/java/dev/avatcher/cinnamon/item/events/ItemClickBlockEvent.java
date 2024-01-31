package dev.avatcher.cinnamon.item.events;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

/**
 * A custom item event, called when a
 * player right-clicks upon a block with
 * a custom item in one of the hands
 *
 * @see dev.avatcher.cinnamon.item.ItemBehaviour
 */
@Getter
@SuperBuilder
public class ItemClickBlockEvent extends ItemUseEvent {
    private Block block;
    private BlockFace blockFace;
}
