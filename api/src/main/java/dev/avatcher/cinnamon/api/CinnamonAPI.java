package dev.avatcher.cinnamon.api;

import dev.avatcher.cinnamon.api.blocks.CustomBlocksRegistry;
import dev.avatcher.cinnamon.api.items.CustomItemsRegistry;
import org.bukkit.plugin.Plugin;

/**
 * Cinnamon API interface
 */
public interface CinnamonAPI {
    /**
     * Loads Cinnamon resources of a certain plugin.
     *
     * @param plugin Plugin containing Cinnamon resources
     */
    void load(Plugin plugin);

    /**
     * Gets registry of custom items.
     *
     * @return Custom items registry
     */
    CustomItemsRegistry getCustomItems();

    /**
     * Gets registry of custom blocks.
     *
     * @return Custom blocks registry
     */
    CustomBlocksRegistry getCustomBlocks();
}
