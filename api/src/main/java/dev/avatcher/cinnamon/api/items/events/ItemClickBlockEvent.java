package dev.avatcher.cinnamon.api.items.events;

import dev.avatcher.cinnamon.api.items.ItemBehaviourEvent;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public interface ItemClickBlockEvent extends ItemBehaviourEvent {
    Block getBlock();
    BlockFace getBlockFace();
}
