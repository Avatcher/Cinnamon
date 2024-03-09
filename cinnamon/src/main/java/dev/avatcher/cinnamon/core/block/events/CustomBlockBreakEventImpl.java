package dev.avatcher.cinnamon.core.block.events;

import dev.avatcher.cinnamon.api.blocks.CustomBlockBehaviour;
import dev.avatcher.cinnamon.api.blocks.events.CustomBlockBreakEvent;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Implementation of {@link CustomBlockBreakEvent}
 */
@Getter
@SuperBuilder
public class CustomBlockBreakEventImpl extends CustomBlockDestroyEventImpl implements CustomBlockBreakEvent {
    private final Player player;
    private final @Nullable ItemStack tool;

    @Override
    public void fire(CustomBlockBehaviour behaviour) {
        behaviour.onBreak(this);
    }
}
