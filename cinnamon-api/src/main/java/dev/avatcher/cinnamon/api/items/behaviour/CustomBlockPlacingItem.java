package dev.avatcher.cinnamon.api.items.behaviour;

import com.google.common.base.Preconditions;
import dev.avatcher.cinnamon.api.blocks.CustomBlock;
import dev.avatcher.cinnamon.api.items.CustomItem;
import dev.avatcher.cinnamon.api.items.events.ItemClickBlockEvent;
import lombok.Getter;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * A behaviour of an item, that places custom blocks on right click
 */
@Getter
public class CustomBlockPlacingItem extends AbstractStructurePlacingItem {
    /**
     * Custom block to be placed
     */
    private final CustomBlock block;

    /**
     * Creates a new behaviour for an item, that
     * is supposed to place custom blocks on
     * right click.
     *
     * @param plugin Plugin owning the item behaviour
     * @param block  Custom block to be placed
     */
    public CustomBlockPlacingItem(Plugin plugin, CustomBlock block) {
        super(plugin);
        this.block = block;
    }

    /**
     * Creates a new behaviour for an item and tries
     * to find a block with the same identifier.
     *
     * @param plugin Plugin owning the item behaviour
     * @param cItem  Custom item of the block
     */
    public CustomBlockPlacingItem(Plugin plugin, CustomItem cItem) {
        super(plugin);
        Optional<CustomBlock> block = CustomBlock.get(cItem.getKey());
        Preconditions.checkArgument(block.isPresent(), "Could not find block %s for responding item"
                .formatted(cItem.getKey()));
        this.block = block.get();
    }

    @Override
    public void afterSuccessfulPlacement(@NotNull ItemClickBlockEvent event, @NotNull Location placeLocation) {
        event.getPlayer().swingHand(event.getHand());
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) return;
        event.getItemStack().add(-1);
    }


    @Override
    public void placeAt(@NotNull Location location, Player player) {
        block.placeAt(location, player);
    }
}
