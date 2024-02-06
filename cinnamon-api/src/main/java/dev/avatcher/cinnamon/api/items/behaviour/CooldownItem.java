package dev.avatcher.cinnamon.api.items.behaviour;

import dev.avatcher.cinnamon.api.items.ItemBehaviour;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Behaviour of a custom item with
 * a cooldown on its usage
 */
public interface CooldownItem extends ItemBehaviour {
    /**
     * Checks, if a certain player put under
     * cooldown and cannot use the item.
     *
     * @param player Player to check
     * @return {@code true}, if player is
     *         under cooldown
     */
    boolean isCooledDown(@NotNull Player player);

    /**
     * Puts player under cooldown, banning it
     * from using the item.
     *
     * @param player Player to put under cooldown
     */
    void coolDown(@NotNull Player player);
}
