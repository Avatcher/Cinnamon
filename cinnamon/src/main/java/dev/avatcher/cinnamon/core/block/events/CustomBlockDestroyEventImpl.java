package dev.avatcher.cinnamon.core.block.events;

import dev.avatcher.cinnamon.api.blocks.CustomBlock;
import dev.avatcher.cinnamon.api.blocks.CustomBlockBehaviour;
import dev.avatcher.cinnamon.api.blocks.events.CustomBlockDestroyEvent;
import dev.avatcher.cinnamon.core.block.AbstractCustomBlockBehaviourEvent;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

/**
 * Implementation of {@link CustomBlockDestroyEvent}
 */
@Getter
@Setter
public class CustomBlockDestroyEventImpl extends AbstractCustomBlockBehaviourEvent implements CustomBlockDestroyEvent {
    private List<ItemStack> drop = new LinkedList<>();

    /**
     * Creates a custom block destroy event, trying to find
     * a custom block corresponding to a given Minecraft block.
     *
     * @param block Block assumed to be custom
     */
    public CustomBlockDestroyEventImpl(@NotNull Block block) {
        super(block);
    }

    /**
     * Creates a custom block destroy event.
     *
     * @param block Minecraft block
     * @param customBlock Custom block
     */
    public CustomBlockDestroyEventImpl(@NotNull Block block, @NotNull CustomBlock customBlock) {
        super(block, customBlock);
    }

    @Override
    public void fire(CustomBlockBehaviour behaviour) {
        behaviour.onDestroy(this);
    }
}
