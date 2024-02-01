package dev.avatcher.cinnamon.dummy.items;

import dev.avatcher.cinnamon.api.items.behaviour.AbstractStructurePlacingItem;
import dev.avatcher.cinnamon.api.items.events.ItemClickBlockEvent;
import dev.avatcher.cinnamon.api.items.events.ItemCreateEvent;
import dev.avatcher.cinnamon.api.items.events.ItemUseEvent;
import dev.avatcher.cinnamon.dummy.CDummy;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

/**
 * Crystalhearted sword's custom item behaviour
 */
public class CrystalheartSword extends AbstractStructurePlacingItem {
    private static final Material BLOCK_MATERIAL = Material.PINK_STAINED_GLASS;
    private static final int DECAY_TIME = 3 * 20;
    private final ConcurrentMap<UUID, ConcurrentLinkedQueue<Location>> placedBlocks = new ConcurrentHashMap<>();

    public CrystalheartSword() {
        super(CDummy.getInstance());
    }

    @Override
    public void onCreate(ItemCreateEvent event) {
        event.getItemStack().editMeta(meta -> {
            meta.lore(List.of(Component.empty()
                    .decoration(TextDecoration.ITALIC, false)
                    .color(NamedTextColor.GRAY)
                    .append(Component.text("Press "))
                    .append(Component.keybind("key.use"))
                    .append(Component.text(" to place glass")))
            );
        });
    }

    @Override
    public void onUse(@NotNull ItemUseEvent event) {
        if (!this.isCooledDown(event.getPlayer())) return;
        if (event.getPlayer().isSneaking()
                && this.placedBlocks.containsKey(event.getPlayer().getUniqueId())) {
            Queue<Location> playerBlocks = this.placedBlocks.get(event.getPlayer().getUniqueId());
            if (playerBlocks.isEmpty()) return;
            this.coolDown(event.getPlayer());

            playerBlocks.forEach(block -> this.decayBlock(event.getPlayer().getUniqueId(), block));
            playerBlocks.clear();
            event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(), Sound.BLOCK_AMETHYST_BLOCK_RESONATE, .5f, .25f);
            event.getPlayer().swingHand(event.getHand());
        }
        super.onUse(event);
    }

    @Override
    public void afterSuccessfulPlacement(@NotNull ItemClickBlockEvent event, @NotNull Location placeLocation) {
        UUID playerUUID = event.getPlayer().getUniqueId();
        if (!this.placedBlocks.containsKey(playerUUID)) {
            this.placedBlocks.put(playerUUID, new ConcurrentLinkedQueue<>());
        }
        this.placedBlocks.get(playerUUID).add(placeLocation.toBlockLocation());
        new BukkitRunnable() {
            @Override
            public void run() {
                decayBlock(event.getPlayer().getUniqueId(), placeLocation);
            }
        }.runTaskLater(this.getPlugin(), DECAY_TIME);
        event.getPlayer().swingHand(event.getHand());
        event.getItemStack().editMeta(meta -> {
            if (meta instanceof Damageable damageable && !event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
                damageable.setDamage(damageable.getDamage() + 10);
            }
        });
    }

    @Override
    public void placeAt(@NotNull Location location) {
        location.getBlock().setType(Material.PINK_STAINED_GLASS);
    }

    private void decayBlock(UUID playerUUID, Location location) {
        if (location.getBlock().getType() != BLOCK_MATERIAL) return;
        location.getBlock().setType(Material.AIR);
        this.playEffectsAt(location);
        if (!placedBlocks.get(playerUUID).remove(location)) {
            System.out.println("Could not remove location");
        }
    }

    private void playEffectsAt(Location location) {
        location.getWorld().spawnParticle(Particle.CHERRY_LEAVES, location.toCenterLocation(), 5, .5, .5, .5);
        location.getWorld().playSound(location, Sound.BLOCK_AMETHYST_BLOCK_BREAK, .15f, 1f);
    }
}
