package dev.avatcher.cinnamon.core.block.events;

import dev.avatcher.cinnamon.api.blocks.CustomBlock;
import dev.avatcher.cinnamon.api.blocks.CustomBlockBehaviour;
import dev.avatcher.cinnamon.api.blocks.events.CustomBlockPlaceEvent;
import dev.avatcher.cinnamon.core.block.AbstractCustomBlockBehaviourEvent;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

/**
 * Implementation of {@link CustomBlockPlaceEvent}
 */
public class CustomBlockPlaceEventImpl extends AbstractCustomBlockBehaviourEvent implements CustomBlockPlaceEvent {
    /**
     * Creates a custom block place event, trying to find
     * a custom block corresponding to a given Minecraft block.
     *
     * @param block Block assumed to be custom
     */
    public CustomBlockPlaceEventImpl(@NotNull Block block) {
        super(block);
    }

    /**
     * Creates a custom place block event.
     *
     * @param block Minecraft block
     * @param customBlock Custom block
     */
    public CustomBlockPlaceEventImpl(@NotNull Block block, @NotNull CustomBlock customBlock) {
        super(block, customBlock);
    }

    @Override
    public void fire(CustomBlockBehaviour behaviour) {
        behaviour.onPlace(this);
    }
}
