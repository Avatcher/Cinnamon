package dev.avatcher.cinnamon.api.blocks.behaviour;

import dev.avatcher.cinnamon.api.blocks.CustomBlock;
import dev.avatcher.cinnamon.api.blocks.CustomBlockBehaviour;
import dev.avatcher.cinnamon.api.blocks.events.CustomBlockDestroyEvent;
import dev.avatcher.cinnamon.api.items.CustomItem;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A behaviour for a custom block dropping
 * pre-defined loot upon its destruction.
 */
public class LootableBlock implements CustomBlockBehaviour {
    private final List<ItemStack> loot;

    /**
     * A constructor that Cinnamon may use to
     * create this behaviour instance, when used
     * inside Cinnamon resources file.
     * <p>
     * This constructor looks for a custom item with
     * the same name as this block and assign it
     * as behaviour's loot.
     *
     * @param customBlock Block to create behaviour for
     */
    public LootableBlock(@NotNull CustomBlock customBlock) {
        var customItem = CustomItem.get(customBlock.getKey());
        if (customItem.isEmpty()) {
            throw new IllegalArgumentException("Custom block '%s' does not have a corresponding item"
                    .formatted(customBlock.getKey()));
        }
        this.loot = List.of(customItem.get().createItemStack());
    }

    /**
     * Creates this behaviour with
     * the given loot.
     *
     * @param loot Loot for custom block
     */
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
