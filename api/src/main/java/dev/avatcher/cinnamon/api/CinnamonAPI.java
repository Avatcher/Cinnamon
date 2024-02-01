package dev.avatcher.cinnamon.api;

import dev.avatcher.cinnamon.api.blocks.CustomBlocksRegistry;
import dev.avatcher.cinnamon.api.items.CustomItemsRegistry;
import org.bukkit.plugin.Plugin;

public interface CinnamonAPI {
    void load(Plugin plugin);

    CustomItemsRegistry getCustomItems();

    CustomBlocksRegistry getCustomBlocks();
}
