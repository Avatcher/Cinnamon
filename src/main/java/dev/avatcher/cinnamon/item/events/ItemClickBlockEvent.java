package dev.avatcher.cinnamon.item.events;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

@Getter
@SuperBuilder
public class ItemClickBlockEvent extends ItemUseEvent {
    private Block block;
    private BlockFace blockFace;
}
