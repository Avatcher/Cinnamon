package dev.avatcher.cinnamon.core.block.events;

import com.google.common.base.Preconditions;
import dev.avatcher.cinnamon.api.blocks.CustomBlock;
import dev.avatcher.cinnamon.api.blocks.events.CustomBlockBreakEvent;
import lombok.Getter;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Implementation of {@link CustomBlockBreakEvent}
 */
@Getter
public class CustomBlockBreakEventImpl extends CustomBlockDestroyEventImpl implements CustomBlockBreakEvent {
    private final Player player;
    private final @Nullable ItemStack tool;

    /**
     * Creates a custom block break event, trying to find
     * a custom block corresponding to a given Minecraft block.
     *
     * @param block Block assumed to be custom
     * @param player Player that broke the block
     * @param tool Tool used to break the block
     */
    public CustomBlockBreakEventImpl(@NotNull Block block, @NotNull Player player, @Nullable ItemStack tool) {
        super(block);
        Preconditions.checkNotNull(player);
        this.player = player;
        this.tool = tool;
    }

    /**
     * Creates a custom block break event.
     *
     * @param block Minecraft block
     * @param customBlock Custom block
     * @param player Player that broke the block
     * @param tool Tool used to break the block
     */
    public CustomBlockBreakEventImpl(@NotNull Block block, @NotNull CustomBlock customBlock, @NotNull Player player, @Nullable ItemStack tool) {
        super(block, customBlock);
        Preconditions.checkNotNull(player);
        this.player = player;
        this.tool = tool;
    }
}
