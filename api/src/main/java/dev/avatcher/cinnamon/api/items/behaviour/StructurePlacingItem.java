package dev.avatcher.cinnamon.api.items.behaviour;

import dev.avatcher.cinnamon.api.items.events.ItemClickBlockEvent;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public interface StructurePlacingItem extends CooldownItem {
    /**
     * Checks, if it is possible to place a structure
     * at given location.
     *
     * @param location Location to place the structure
     * @return {@code true}, if it is possible to place the structure
     */
    boolean isPlaceableLocation(Location location);

    /**
     * Places structure at the given location.
     *
     * @param location Location where to place
     *                 the structure
     */
    void placeAt(@NotNull Location location);

    /**
     * Performs an action after structure was
     * successfully placed.
     *
     * @param event Event
     * @param placeLocation Location where the structure
     *                      has been placed
     */
    default void afterSuccessfulPlacement(@NotNull ItemClickBlockEvent event, @NotNull Location placeLocation) {}
}
