package dev.avatcher.cinnamon.api.items.events;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public interface ItemClickBlockEvent extends ItemUseEvent {
    Block getBlock();
    BlockFace getBlockFace();
}
