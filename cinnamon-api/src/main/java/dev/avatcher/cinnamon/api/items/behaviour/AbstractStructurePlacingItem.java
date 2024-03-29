package dev.avatcher.cinnamon.api.items.behaviour;

import dev.avatcher.cinnamon.api.blocks.CustomBlock;
import dev.avatcher.cinnamon.api.items.events.ItemClickBlockEvent;
import dev.avatcher.cinnamon.api.items.events.ItemUseEvent;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/**
 * An abstract implementation of {@link StructurePlacingItem}
 */
public abstract class AbstractStructurePlacingItem extends AbstractCooldownItem implements StructurePlacingItem {
    /**
     * Creates a structure placing item
     * belonging to certain plugin.
     *
     * @param plugin Plugin owning the item
     */
    public AbstractStructurePlacingItem(Plugin plugin) {
        super(plugin);
    }

    @Override
    public void onUse(@NotNull ItemUseEvent event) {
        if (!(event instanceof ItemClickBlockEvent blockEvent)
                || (CustomBlock.isInteractable(blockEvent.getBlock()) && !event.getPlayer().isSneaking())) return;
        if (!this.isCooledDown(event.getPlayer())) return;
        this.coolDown(event.getPlayer());
        Location placeLocation = blockEvent.getBlock().getRelative(blockEvent.getBlockFace()).getLocation();
        if (this.isPlaceableLocation(placeLocation)) {
            this.placeAt(placeLocation, event.getPlayer());
            this.afterSuccessfulPlacement(blockEvent, placeLocation);
        }
    }

    @Override
    public boolean isPlaceableLocation(Location location) {
        return location.getBlock().isReplaceable()
                && location.toCenterLocation().getNearbyEntities(.5, .5, .5).isEmpty();
    }
}
