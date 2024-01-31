package dev.avatcher.cdummy.items;

import dev.avatcher.cdummy.CDummy;
import dev.avatcher.cinnamon.item.ItemBehaviour;
import dev.avatcher.cinnamon.item.events.ItemCreateEvent;
import dev.avatcher.cinnamon.item.events.ItemUseEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.logging.Logger;

public class Excalibur implements ItemBehaviour {
    private final Logger log;

    public Excalibur() {
        log = CDummy.getInstance().getLogger();
    }

    @Override
    public void onCreate(ItemCreateEvent event) {
        event.getItemStack().editMeta(meta -> {
            meta.lore(List.of(Component.empty()
                    .decoration(TextDecoration.ITALIC, false)
                    .color(NamedTextColor.GRAY)
                    .append(Component.text("Press "))
                    .append(Component.keybind("key.use"))
                    .append(Component.text(" to strike")))
            );
        });
    }

    @Override
    public void onUse(@NotNull ItemUseEvent event) {
        Block targetBlock = event.getPlayer().getTargetBlock(null, 50);
        if (targetBlock.getType() != Material.AIR) {
            targetBlock.getWorld().spawn(targetBlock.getLocation(), EntityType.LIGHTNING.getEntityClass());
            event.getPlayer().swingHand(event.getHand());
        }
    }
}
