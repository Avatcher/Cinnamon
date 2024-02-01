package dev.avatcher.cinnamon.api.items.behaviour;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Abstract implementation of {@link CooldownItem}
 */
public abstract class AbstractCooldownItem implements CooldownItem {
    private final Set<UUID> cooldowns = ConcurrentHashMap.newKeySet();

    @Getter
    private final Plugin plugin;

    public AbstractCooldownItem(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean isCooledDown(@NotNull Player player) {
        return !this.cooldowns.contains(player.getUniqueId());
    }

    @Override
    public void coolDown(@NotNull Player player) {
        this.cooldowns.add(player.getUniqueId());
        new BukkitRunnable() {
            @Override
            public void run() {
                cooldowns.remove(player.getUniqueId());
            }
        }.runTaskLater(plugin, 1);
    }
}
