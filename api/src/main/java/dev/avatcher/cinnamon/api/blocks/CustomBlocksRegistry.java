package dev.avatcher.cinnamon.api.blocks;

import org.bukkit.Registry;
import org.bukkit.block.Block;

public interface CustomBlocksRegistry extends Registry<CustomBlock> {
    boolean isCustom(Block block);

    boolean isInteractable(Block block);

    CustomBlock get(Block block);
}
