package dev.avatcher.cinnamon.item.listeners;

import com.google.common.base.Preconditions;
import dev.avatcher.cinnamon.item.CItem;
import dev.avatcher.cinnamon.item.CItemBehaviour;
import dev.avatcher.cinnamon.item.events.CItemRightClickEvent;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Listener of item-related events
 */
public class ItemEventListener implements Listener {

    /**
     * Cancels craft in vanilla recipes using custom items.
     *
     * @param event Event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBeforeCraft(PrepareItemCraftEvent event) {
        if (event.getRecipe() == null) return;
        ItemStack resultStack = event.getRecipe().getResult();
        PersistentDataContainer persistentDataContainer = resultStack.getItemMeta().getPersistentDataContainer();
        if (persistentDataContainer.has(CItem.RECIPE_MARK_KEY)) {
            NamespacedKey identifier = NamespacedKey.fromString(
                    Objects.requireNonNull(persistentDataContainer.get(CItem.IDENTIFIER_KEY, PersistentDataType.STRING)));
            Preconditions.checkNotNull(identifier);

            resultStack.editMeta(meta -> {
               PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
               if (identifier.equals(resultStack.getType().getKey())) {
                   dataContainer.remove(CItem.IDENTIFIER_KEY);
               }
               dataContainer.remove(CItem.RECIPE_MARK_KEY);
            });
            event.getInventory().setResult(resultStack);
            return;
        }
        for (ItemStack itemStack : event.getInventory().getStorageContents()) {
            if (CItem.isCustom(itemStack)) {
                event.getInventory().setResult(null);
                return;
            }
        }
    }

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
