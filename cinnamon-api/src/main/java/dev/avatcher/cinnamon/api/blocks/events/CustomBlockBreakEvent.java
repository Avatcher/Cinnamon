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
    Player getPlayer();
    @Nullable ItemStack getTool();
}
