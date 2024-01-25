package dev.avatcher.cinnamon.item.listeners;

import com.google.common.base.Preconditions;
import dev.avatcher.cinnamon.item.CItem;
import dev.avatcher.cinnamon.item.events.ItemClickBlockEvent;
import dev.avatcher.cinnamon.item.events.ItemUseEvent;
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
        if (!event.hasItem() || !CItem.isCustom(event.getItem())) return;
        CItem cItem = CItem.of(event.getItem()).orElseThrow();

        ItemUseEvent itemEvent = null;
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            itemEvent = ItemClickBlockEvent.builder()
                    .itemStack(event.getItem())
                    .player(event.getPlayer())
                    .hand(event.getHand())
                    .block(event.getClickedBlock())
                    .blockFace(event.getBlockFace())
                    .build();
        } else if (event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
            itemEvent = ItemUseEvent.builder()
                    .itemStack(event.getItem())
                    .player(event.getPlayer())
                    .hand(event.getHand())
                    .build();
        }
        if (itemEvent != null) cItem.getBehaviour().onUse(itemEvent);
    }
}
