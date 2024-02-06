package dev.avatcher.cinnamon.core.item.events;

import dev.avatcher.cinnamon.api.items.events.ItemClickBlockEvent;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

/**
 * A custom item event, called when a
 * player right-clicks upon a block with
 * a custom item in one of the hands
 *
 * @see dev.avatcher.cinnamon.api.items.ItemBehaviour
 */
@Getter
@SuperBuilder
public class ItemClickBlockEventImpl extends ItemUseEventImpl implements ItemClickBlockEvent {
    private Block block;
    private BlockFace blockFace;
}
