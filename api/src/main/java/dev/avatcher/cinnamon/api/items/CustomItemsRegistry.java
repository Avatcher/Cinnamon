package dev.avatcher.cinnamon.api.items;

import org.bukkit.Registry;
import org.bukkit.inventory.ItemStack;

public interface CustomItemsRegistry extends Registry<CustomItem> {
    boolean isCustom(ItemStack itemStack);
    CustomItem get(ItemStack itemStack);
}
