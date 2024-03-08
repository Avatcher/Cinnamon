package dev.avatcher.cinnamon.dummy.blocks;

import dev.avatcher.cinnamon.api.blocks.CustomBlockBehaviour;
import dev.avatcher.cinnamon.api.blocks.events.CustomBlockDestroyEvent;
import dev.avatcher.cinnamon.api.blocks.events.CustomBlockPlaceEvent;
import dev.avatcher.cinnamon.api.items.CustomItem;
import dev.avatcher.cinnamon.dummy.CDummy;
import org.bukkit.Location;
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
        event.getPlayer().sendMessage("You placed crystalheart ore");
    }

    @Override
    public void onDestroy(CustomBlockDestroyEvent event) {
        Random random = new Random();
        ItemStack drop = CustomItem.get(CDummy.getInstance(), "crystalheart").orElseThrow().createItemStack();
        drop.setAmount(random.nextInt(1, 4));
        event.getDrop().add(drop);

        Location location = event.getBlock().getLocation();
        location.getWorld().spawnParticle(Particle.CHERRY_LEAVES, location.toCenterLocation(), 5, .5, .5, .5);
        location.getWorld().playSound(location, Sound.BLOCK_AMETHYST_BLOCK_BREAK, .15f, 1f);
    }
}
