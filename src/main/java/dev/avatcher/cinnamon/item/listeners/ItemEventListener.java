package dev.avatcher.cinnamon.item.listeners;

import dev.avatcher.cinnamon.item.CItem;
import dev.avatcher.cinnamon.item.CItemBehaviour;
import dev.avatcher.cinnamon.item.events.CItemRightClickEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Listener of item-related events
 */
public class ItemEventListener implements Listener {

    /**
     * Handles players' right click events
     *
     * @param event Event
     */
    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if (!this.isRightClick(event)
                || !event.hasItem()
                || !CItem.isCustom(event.getItem())
        ) return;
        CItem cItem = CItem.of(event.getItem()).orElseThrow();
        CItemBehaviour cBehaviour = cItem.createBehaviour();
        CItemRightClickEvent cEvent = CItemRightClickEvent
                .builder()
                .cItem(cItem)
                .itemStack(event.getItem())
                .player(event.getPlayer())
                .clickedBlock(event.getClickedBlock())
                .build();
        cBehaviour.onRightClick(cEvent);
    }

    /**
     * Checks, if {@link PlayerInteractEvent} is a right click event.
     *
     * @param event Event
     * @return {@code true}, if it is right click event
     */
    private boolean isRightClick(@NotNull PlayerInteractEvent event) {
        return event.getAction().equals(Action.RIGHT_CLICK_AIR)
                || (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)
                && event.getPlayer().isSneaking());
    }
}
