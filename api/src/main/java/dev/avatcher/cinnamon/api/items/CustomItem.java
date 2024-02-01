package dev.avatcher.cinnamon.api.items;

import dev.avatcher.cinnamon.api.Cinnamon;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Contract;

import java.util.Optional;

/**
 * Custom item
 */
public interface CustomItem extends Keyed {
    /**
     * Checks, if a minecraft item stack
     * contains custom item. The result of this
     * method does not imply that the custom
     * item can be surely found in {@link CustomItemsRegistry}.
     *
     * @param itemStack Item stack to check
     * @return {@code true}, if {@code itemStack} is a custom item
     */
    static boolean isCustom(ItemStack itemStack) {
        return Cinnamon.getInstance().getCustomItems().isCustom(itemStack);
    }

    /**
     * Gets a custom item with a corresponding
     * key, or an {@link Optional#empty()}, if
     * it is not found.
     *
     * @param key Key of the custom item
     * @return {@link Optional#empty()}, if item
     *         is not found
     */
    static Optional<CustomItem> get(NamespacedKey key) {
        return Optional.ofNullable(Cinnamon.getInstance().getCustomItems().get(key));
    }

    /**
     * Gets a custom item with a corresponding
     * name owned by certain plugin, or an
     * {@link Optional#empty()}, if it is not found.
     *
     * @param plugin Plugin owning the item
     * @param name   The name of the item
     * @return {@link Optional#empty()}, if item
     *         is not found
     */
    static Optional<CustomItem> get(Plugin plugin, String name) {
        NamespacedKey key = new NamespacedKey(plugin, name);
        return CustomItem.get(key);
    }

    /**
     * Gets a custom item with a corresponding
     * key, or an {@link Optional#empty()}, if
     * it is not found.
     *
     * @param key Key of the custom item
     * @return {@link Optional#empty()}, if item
     *         is not found
     */
    static Optional<CustomItem> get(String key) {
        NamespacedKey namespacedKey = NamespacedKey.fromString(key);
        return CustomItem.get(namespacedKey);
    }

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
    static Optional<CustomItem> get(ItemStack itemStack) {
        if (!CustomItem.isCustom(itemStack)) return Optional.empty();
        return Optional.ofNullable(Cinnamon.getInstance().getCustomItems().get(itemStack));
    }

    /**
     * Gets material of the custom item.
     *
     * @return Item's material
     */
    Material getMaterial();

    /**
     * Gets behaviour of the custom item.
     *
     * @return Item's behaviour
     */
    ItemBehaviour getBehaviour();

    /**
     * Creates a new item stack
     * containing the custom item.
     *
     * @return Custom item stack
     */
    @Contract("-> new")
    ItemStack createItemStack();
}
