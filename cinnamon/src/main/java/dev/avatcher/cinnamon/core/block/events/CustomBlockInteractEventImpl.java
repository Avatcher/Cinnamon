package dev.avatcher.cinnamon.core.block.events;

import dev.avatcher.cinnamon.api.blocks.CustomBlock;
import dev.avatcher.cinnamon.api.blocks.CustomBlockBehaviour;
import dev.avatcher.cinnamon.api.blocks.events.CustomBlockInteractEvent;
import dev.avatcher.cinnamon.core.block.AbstractCustomBlockBehaviourEvent;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

/**
 * Implementation of {@link CustomBlockInteractEvent}
 */
@Getter
@Setter
public class CustomBlockInteractEventImpl extends AbstractCustomBlockBehaviourEvent implements CustomBlockInteractEvent {
    boolean interactable = true;

    /**
     * Creates a custom block interact event, trying to find
     * a custom block corresponding to a given Minecraft block.
     *
     * @param block Block assumed to be custom
     */
    public CustomBlockInteractEventImpl(@NotNull Block block) {
        super(block);
    }

    /**
     * Creates a custom block event.
     *
     * @param block Minecraft block
     * @param customBlock Custom block
     */
    public CustomBlockInteractEventImpl(@NotNull Block block, @NotNull CustomBlock customBlock) {
        super(block, customBlock);
    }

    @Override
    public void fire(CustomBlockBehaviour behaviour) {
        behaviour.onInteract(this);
    }
}
