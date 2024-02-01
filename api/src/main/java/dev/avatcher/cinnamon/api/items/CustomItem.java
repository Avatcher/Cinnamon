package dev.avatcher.cinnamon.api.items;

import dev.avatcher.cinnamon.api.Cinnamon;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Optional;

public interface CustomItem extends Keyed {
    static boolean isCustom(ItemStack itemStack) {
        return Cinnamon.getInstance().getCustomItems().isCustom(itemStack);
    }

    static Optional<CustomItem> get(NamespacedKey key) {
        return Optional.ofNullable(Cinnamon.getInstance().getCustomItems().get(key));
    }

    static Optional<CustomItem> get(Plugin plugin, String name) {
        NamespacedKey key = new NamespacedKey(plugin, name);
        return CustomItem.get(key);
    }

    static Optional<CustomItem> get(String key) {
        NamespacedKey namespacedKey = NamespacedKey.fromString(key);
        return CustomItem.get(namespacedKey);
    }

    static Optional<CustomItem> get(ItemStack itemStack) {
        if (!CustomItem.isCustom(itemStack)) return Optional.empty();
        return Optional.ofNullable(Cinnamon.getInstance().getCustomItems().get(itemStack));
    }

    Material getMaterial();

    ItemBehaviour getBehaviour();

    ItemStack createItemStack();
}
