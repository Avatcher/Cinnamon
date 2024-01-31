package dev.avatcher.cinnamon.item.behaviour;

import com.google.common.base.Preconditions;
import dev.avatcher.cinnamon.block.CBlock;
import dev.avatcher.cinnamon.item.CItem;
import dev.avatcher.cinnamon.item.events.ItemUseEvent;
import lombok.Getter;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * A behaviour of an item, that places custom blocks on right click
 */
@Getter
public class CustomBlockPlacingItem extends StructurePlacingItem {
    /**
     * Custom block to be placed
     */
    private final CBlock block;

    /**
     * Creates a new behaviour for an item, that
     * is supposed to place custom blocks on
     * right click.
     *
     * @param block Custom block to be placed
     */
    public CustomBlockPlacingItem(CBlock block) {
        this.block = block;
    }

    /**
     * Creates a new behaviour for an item and tries
     * to find a block with the same identifier.
     *
     * @param cItem Custom item of the block
     */
    public CustomBlockPlacingItem(CItem cItem) {
        Optional<CBlock> block = CBlock.of(cItem.getIdentifier());
        Preconditions.checkArgument(block.isPresent(), "Could not find block %s for responding item"
                .formatted(cItem.getIdentifier()));
        this.block = block.get();
    }

    @Override
    public void onUse(@NotNull ItemUseEvent event) {
        super.onUse(event, () -> {
            event.getPlayer().swingHand(event.getHand());
            if (event.getPlayer().getGameMode() == GameMode.CREATIVE || event.getItemStack() == null) return;
            event.getItemStack().add(-1);
        });
    }

    @Override
    public void placeBlocks(Location location) {
        block.placeAt(location);
    }
}
