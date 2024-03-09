package dev.avatcher.cinnamon.api.blocks.behaviour;

import dev.avatcher.cinnamon.api.blocks.CustomBlock;
import dev.avatcher.cinnamon.api.blocks.CustomBlockBehaviour;
import dev.avatcher.cinnamon.api.blocks.events.CustomBlockDestroyEvent;
import dev.avatcher.cinnamon.api.items.CustomItem;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LootableBlock implements CustomBlockBehaviour {
    private final List<ItemStack> loot;

    public LootableBlock(@NotNull CustomBlock customBlock) {
        var customItem = CustomItem.get(customBlock.getKey());
        if (customItem.isEmpty()) {
            throw new IllegalArgumentException("Custom block '%s' does not have a corresponding item"
                    .formatted(customBlock.getKey()));
        }
        this.loot = List.of(customItem.get().createItemStack());
    }

    public LootableBlock(List<ItemStack> loot) {
        this.loot = loot == null
                ? List.of()
                : loot;
    }

    @Override
    public void onDestroy(CustomBlockDestroyEvent event) {
        event.setDrop(this.loot);
    }
}
