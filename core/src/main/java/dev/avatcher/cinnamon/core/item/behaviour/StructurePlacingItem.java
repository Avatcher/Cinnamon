package dev.avatcher.cinnamon.core.item.behaviour;

import dev.avatcher.cinnamon.core.Cinnamon;
import dev.avatcher.cinnamon.core.block.CBlock;
import dev.avatcher.cinnamon.core.item.ItemBehaviour;
import dev.avatcher.cinnamon.core.item.events.ItemClickBlockEvent;
import dev.avatcher.cinnamon.core.item.events.ItemUseEvent;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * An abstract item behaviour for items, that place
 * some blocks by right click on other block
 */
public abstract class StructurePlacingItem implements ItemBehaviour {
    /**
     * A list of players under cooldown after placing a structure.
     * The cooldown is 1 tick long and only needed, so player
     * does not place structure twice instantly.
     */
    private final Set<UUID> cooldowns = new HashSet<>();

    @Override
    public void onUse(@NotNull ItemUseEvent event) {
        this.onUse(event, () -> {});
    }

    /**
     * Runs {@link #onUse(ItemUseEvent)} and then a {@link Runnable}, if
     * structure was placed successfully.
     *
     * @param event Event
     * @param after Runnable to run, if the structure is placed
     */
    public void onUse(@NotNull ItemUseEvent event, Runnable after) {
        if (!(event instanceof ItemClickBlockEvent blockEvent)
                || (CBlock.isIntractable(blockEvent.getBlock()) && !event.getPlayer().isSneaking())) return;
        if (!this.isCooledDown(event.getPlayer())) return;
        this.coolDown(event.getPlayer());
        Location placeLocation = blockEvent.getBlock().getRelative(blockEvent.getBlockFace()).getLocation();
        if (this.isPlaceableLocation(placeLocation)) this.placeBlocks(placeLocation);
        after.run();
    }

    /**
     * Places the structure at given location.
     *
     * @param location Location to place the structure
     */
    public abstract void placeBlocks(Location location);

    /**
     * Checks, if it is possible to place a structure
     * at given location.
     *
     * @param location Location to place the structure
     * @return {@code true}, if it is possible to place the structure
     */
    public boolean isPlaceableLocation(Location location) {
        return location.getBlock().isReplaceable()
                && location.toCenterLocation().getNearbyEntities(.5, .5, .5).isEmpty();
    }

    /**
     * Checks, if player is cooled down and may
     * place a new structure.
     *
     * @param player Player to be checked
     * @return {@code true}, if player may place the structure
     */
    private boolean isCooledDown(Player player) {
        return !this.cooldowns.contains(player.getUniqueId());
    }

    /**
     * Puts player under a cooldown, banning them
     * from placing the structure for a short amount
     * of time.
     *
     * @param player Player to be put under cooldown
     */
    private void coolDown(Player player) {
        this.cooldowns.add(player.getUniqueId());
        new BukkitRunnable() {
            @Override
            public void run() {
                cooldowns.remove(player.getUniqueId());
            }
        }.runTaskLater(Cinnamon.getInstance(), 1);
    }
}
