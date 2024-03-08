package dev.avatcher.cinnamon.dummy.blocks;

import dev.avatcher.cinnamon.api.blocks.CustomBlockBehaviour;
import dev.avatcher.cinnamon.api.blocks.events.CustomBlockPlaceEvent;
import dev.avatcher.cinnamon.dummy.CDummy;

/**
 * Behaviour of 'crystalheart_ore' block
 */
public class CrystalheartOre implements CustomBlockBehaviour {
    @Override
    public void onPlace(CustomBlockPlaceEvent event) {
        CDummy.getInstance().getLogger().info("Crystalheart ore placed!");
        event.getPlayer().sendMessage("You placed crystalheart ore");
    }
}
