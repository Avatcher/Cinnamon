package dev.avatcher.cinnamon.core.block;

import com.google.common.base.Preconditions;
import dev.avatcher.cinnamon.api.blocks.CustomBlock;
import dev.avatcher.cinnamon.api.blocks.CustomBlockBehaviour;
import dev.avatcher.cinnamon.api.blocks.CustomBlockBehaviourEvent;
import lombok.Getter;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Abstract implementation of {@link CustomBlockBehaviourEvent}
 */
@Getter
public abstract class AbstractCustomBlockBehaviourEvent implements CustomBlockBehaviourEvent {

    private final CustomBlock customBlock;

    private final Block block;

    /**
     * Creates a custom block event, trying to find
     * a custom block corresponding to a given Minecraft block.
     *
     * @param block Block assumed to be custom
     */
    public AbstractCustomBlockBehaviourEvent(@NotNull Block block) {
        Preconditions.checkNotNull(block);
        Optional<CustomBlock> customBlockOptional = CustomBlock.get(block);
        Preconditions.checkArgument(customBlockOptional.isPresent(), "Given block is not custom");
        this.block = block;
        this.customBlock = customBlockOptional.get();
    }

    /**
     * Creates a custom block event.
     *
     * @param block Minecraft block
     * @param customBlock Custom block
     */
    public AbstractCustomBlockBehaviourEvent(@NotNull Block block, @NotNull CustomBlock customBlock) {
        Preconditions.checkNotNull(block);
        Preconditions.checkNotNull(customBlock);
        this.block = block;
        this.customBlock = customBlock;
    }

    /**
     * Fires event for a specific behaviour.
     *
     * @param behaviour Behaviour to fire event on
     */
    public abstract void fire(CustomBlockBehaviour behaviour);
}
