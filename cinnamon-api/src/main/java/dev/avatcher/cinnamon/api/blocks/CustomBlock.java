package dev.avatcher.cinnamon.api.blocks;

import dev.avatcher.cinnamon.api.Cinnamon;
import org.bukkit.Keyed;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Optional;

/**
 * A custom minecraft block
 */
public interface CustomBlock extends Keyed {
    /**
     * Checks, if a block placed in world
     * is a custom block. The result of this
     * method does not imply that the custom
     * block can be surely found in {@link CustomBlocksRegistry}.
     *
     * @param block Block to check
     * @return {@code true}, if {@code block} is a custom block
     */
    static boolean isCustom(Block block) {
        return Cinnamon.getInstance().getCustomBlocks().isCustom(block);
    }

    /**
     * Checks, if player cen interact with
     * a certain block by right-clicking it.
     *
     * @param block Block to check
     * @return {@code true}, if player can
     *         interact with the block
     *         by right-clicking
     */
    static boolean isInteractable(Block block) {
        return Cinnamon.getInstance().getCustomBlocks().isInteractable(block);
    }

    /**
     * Gets a custom block with a corresponding
     * key, or an {@link Optional#empty()}, if
     * it is not found.
     *
     * @param key Key of the custom block
     * @return {@link Optional#empty()}, if block
     *         is not found
     */
    static Optional<CustomBlock> get(NamespacedKey key) {
        return Optional.ofNullable(Cinnamon.getInstance().getCustomBlocks().get(key));
    }

    /**
     * Gets a custom block with a corresponding
     * name owned by certain plugin, or an
     * {@link Optional#empty()}, if it is not found.
     *
     * @param plugin Plugin owning the block
     * @param name   The name of the block
     * @return {@link Optional#empty()}, if block
     *         is not found
     */
    static Optional<CustomBlock> get(Plugin plugin, String name) {
        NamespacedKey key = new NamespacedKey(plugin, name);
        return CustomBlock.get(key);
    }

    /**
     * Gets a custom block with a corresponding
     * key, or an {@link Optional#empty()}, if
     * it is not found.
     *
     * @param key Key of the custom block
     * @return {@link Optional#empty()}, if block
     *         is not found
     */
    static Optional<CustomBlock> get(String key) {
        NamespacedKey namespacedKey = NamespacedKey.fromString(key);
        return CustomBlock.get(namespacedKey);
    }

    /**
     * Gets a custom block represented by a
     * placed minecraft block, or an {@link Optional#empty()},
     * if the given block is an unregistered custom block
     * or is not a custom block at all.
     *
     * @param block Placed minecraft block assumed to be
     *              a custom block
     * @return {@link Optional#empty()}, if the given block
     *         is not a custom block or
     *         an unregistered custom block
     */
    static Optional<CustomBlock> get(Block block) {
        if (!CustomBlock.isCustom(block)) return Optional.empty();
        return Optional.ofNullable(Cinnamon.getInstance().getCustomBlocks().get(block));
    }

    /**
     * Creates a {@link BlockData} of
     * the custom block.
     *
     * @return {@link BlockData} of the
     *         custom block.
     */
    BlockData createBlockData();

    /**
     * Places the custom block at the
     * given location.
     *
     * @param location Location to place
     *                 the custom block at
     */
    default void placeAt(Location location) {
        this.placeAt(location, null);
    }

    /**
     * Placed the custom block at the given
     * location by a specific player.
     *
     * @param location Location to place the
     *                 custom block at
     * @param player   Player who placed the
     *                 block
     */
    void placeAt(Location location, Player player);

    /**
     * Gets the behaviour of custom block.
     * It describes the responses of the block
     * to various in-game events, such as
     * its in-world destruction or placement.
     *
     * @return Custom block's behaviour
     */
    CustomBlockBehaviour getBehaviour();
}
