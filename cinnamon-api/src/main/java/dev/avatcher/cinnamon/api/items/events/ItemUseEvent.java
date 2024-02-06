package dev.avatcher.cinnamon.api.items.events;

import dev.avatcher.cinnamon.api.items.ItemBehaviour;
import dev.avatcher.cinnamon.api.items.ItemBehaviourEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;

/**
 * Custom item event, called when player
 * right-clicks with a custom item in one
 * of the hands.
 *
 * @see ItemBehaviour
 */
public interface ItemUseEvent extends ItemBehaviourEvent {
    /**
     * Gets the player, that used the item.
     *
     * @return Player, that used the item
     */
    Player getPlayer();

    /**
     * Gets the hand, in which the custom
     * item was located, when player
     * used it.
     *
     * @return Player hand, where the item
     *         is located
     */
    EquipmentSlot getHand();
}
