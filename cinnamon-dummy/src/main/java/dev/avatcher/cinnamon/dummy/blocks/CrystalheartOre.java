package dev.avatcher.cinnamon.dummy.blocks;

import dev.avatcher.cinnamon.api.blocks.CustomBlockBehaviour;
import dev.avatcher.cinnamon.api.blocks.events.CustomBlockBreakEvent;
import dev.avatcher.cinnamon.api.blocks.events.CustomBlockDestroyEvent;
import dev.avatcher.cinnamon.api.blocks.events.CustomBlockPlaceEvent;
import dev.avatcher.cinnamon.api.items.CustomItem;
import dev.avatcher.cinnamon.dummy.CDummy;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

/**
 * Behaviour of 'crystalheart_ore' block
 */
public class CrystalheartOre implements CustomBlockBehaviour {
    @Override
    public void onPlace(CustomBlockPlaceEvent event) {
        event.getPlayer().sendMessage(Component.text("You placed it").color(NamedTextColor.GREEN));
    }

    @Override
    public void onDestroy(CustomBlockDestroyEvent event) {
        this.addDrop(event);
        event.getBlock().getWorld().sendMessage(Component.text("It is destroyed").color(NamedTextColor.LIGHT_PURPLE));
    }

    @Override
    public void onBreak(CustomBlockBreakEvent event) {
        this.addDrop(event);
        event.getDrop().add(new ItemStack(Material.DIAMOND));
        event.getPlayer().sendMessage(Component.text("You broke it").color(NamedTextColor.RED));
    }

    private void addDrop(CustomBlockDestroyEvent event) {
        Random random = new Random();
        ItemStack drop = CustomItem.get(CDummy.getInstance(), "crystalheart").orElseThrow().createItemStack();
        drop.setAmount(random.nextInt(1, 4));
        event.getDrop().add(drop);

        Location location = event.getBlock().getLocation();
        location.getWorld().spawnParticle(Particle.CHERRY_LEAVES, location.toCenterLocation(), 5, .5, .5, .5);
        location.getWorld().playSound(location, Sound.BLOCK_AMETHYST_BLOCK_BREAK, .15f, 1f);
    }
}
