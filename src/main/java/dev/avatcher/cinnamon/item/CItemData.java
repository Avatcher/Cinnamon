package dev.avatcher.cinnamon.item;

import org.bukkit.inventory.ItemStack;

public abstract class CItemData {

    public void write(ItemStack itemStack) {

    }

    public static CItemData of(ItemStack itemStack) {
        return null;
    }
}
