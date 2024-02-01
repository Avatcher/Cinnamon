package dev.avatcher.cinnamon.api.items;

import org.bukkit.Registry;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

/**
 * Registry of custom items
 */
public interface CustomItemsRegistry extends Registry<CustomItem> {
    /**
     * Checks, if a minecraft item stack
     * contains custom item. The result of this
     * method does not imply that the custom
     * item can be surely found in {@link CustomItemsRegistry}.
     *
     * @param itemStack Item stack to check
     * @return {@code true}, if {@code itemStack} is a custom item
     */
    boolean isCustom(ItemStack itemStack);

    /**
     * Gets custom item contained in a certain
     * item stack, or {@link Optional#empty()},
     * if item stack contains an unregistered custom
     * item, or does not contain a custom item at all.
     *
     * @param itemStack Custom item stack
     * @return {@link Optional#empty()} if item stack
     *         contains an unregistered custom item,
     *         or does not contain a custom item at all
     */
    CustomItem get(ItemStack itemStack);
}
