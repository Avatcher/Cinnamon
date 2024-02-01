package dev.avatcher.cinnamon.api.blocks;

import org.bukkit.Registry;
import org.bukkit.block.Block;

import java.util.Optional;

/**
 * Registry of custom blocks
 *
 * @see CustomBlock
 */
public interface CustomBlocksRegistry extends Registry<CustomBlock> {
    /**
     * Checks, if a block placed in world
     * is a custom block. The result of this
     * method does not imply that the custom
     * block can be found in {@link CustomBlocksRegistry},
     * but rather, if the block can be custom at all.
     *
     * @param block Block to check
     * @return {@code true}, if {@code block} is a custom block
     */
    boolean isCustom(Block block);

    /**
     * Checks, if player cen interact with
     * a certain block by right-clicking it.
     *
     * @param block Block to check
     * @return {@code true}, if player can
     *         interact with the block
     *         by right-clicking
     */
    boolean isInteractable(Block block);

    /**
     * Gets a custom block represented by a
     * placed minecraft block, or an {@link Optional#empty()},
     * if the given block is not a custom block or
     * an unregistered custom block.
     *
     * @param block Placed minecraft block assumed to be
     *              a custom block
     * @return {@link Optional#empty()}, if the given block
     *         is not a custom block or
     *         an unregistered custom block
     */
    CustomBlock get(Block block);
}
