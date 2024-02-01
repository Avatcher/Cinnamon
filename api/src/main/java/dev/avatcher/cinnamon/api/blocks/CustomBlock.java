package dev.avatcher.cinnamon.api.blocks;

import dev.avatcher.cinnamon.api.Cinnamon;
import org.bukkit.Keyed;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.plugin.Plugin;

import java.util.Optional;

public interface CustomBlock extends Keyed {
    static boolean isCustom(Block block) {
        return Cinnamon.getInstance().getCustomBlocks().isCustom(block);
    }

    static Optional<CustomBlock> get(NamespacedKey key) {
        return Optional.ofNullable(Cinnamon.getInstance().getCustomBlocks().get(key));
    }

    static Optional<CustomBlock> get(Plugin plugin, String name) {
        NamespacedKey key = new NamespacedKey(plugin, name);
        return CustomBlock.get(key);
    }

    static Optional<CustomBlock> get(String key) {
        NamespacedKey namespacedKey = NamespacedKey.fromString(key);
        return CustomBlock.get(namespacedKey);
    }

    static Optional<CustomBlock> get(Block block) {
        if (!CustomBlock.isCustom(block)) return Optional.empty();
        return Optional.ofNullable(Cinnamon.getInstance().getCustomBlocks().get(block));
    }

    BlockData createBlockData();

    void placeAt(Location location);
}
