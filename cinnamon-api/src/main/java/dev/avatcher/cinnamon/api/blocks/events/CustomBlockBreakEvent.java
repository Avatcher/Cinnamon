package dev.avatcher.cinnamon.api.blocks.events;

import dev.avatcher.cinnamon.api.blocks.CustomBlockBehaviour;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Custom block event called when block
 * is broken by a player
 *
 * @see CustomBlockBehaviour
 * @see CustomBlockDestroyEvent
 */
public interface CustomBlockBreakEvent extends CustomBlockDestroyEvent {
    /**
     * Gets the player who broke this block.
     *
     * @return Player who brok this block
     */
    Player getPlayer();

    /**
     * Gets the tool player used to break
     * this block. It is simply the item
     * player held in their main hand in
     * order to break this block.
     *
     * @return Item used to break this block
     */
    @Nullable ItemStack getTool();
}
