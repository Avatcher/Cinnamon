package dev.avatcher.cinnamon.block.listeners;

import dev.avatcher.cinnamon.block.CBlock;
import dev.avatcher.cinnamon.block.NoteblockTune;
import dev.avatcher.cinnamon.item.CItem;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Listener that processes noteblock-related events
 */
public class NoteblockListener implements Listener {

    /**
     * Cancels a play of note by a noteblock.
     *
     * @param event Event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onNoteblockPlay(@NotNull NotePlayEvent event) {
        if (CBlock.isRegularNoteblock(event.getBlock())) return;
        event.setCancelled(true);
    }

    /**
     * Manages how noteblocks are broken.
     *
     * @param event Event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onNoteblockDestroyed(BlockBreakEvent event) {
        Block block = event.getBlock();
        Stream.of(BlockFace.DOWN, BlockFace.UP)
                .map(block::getRelative)
                .filter(b -> b.getType() == Material.NOTE_BLOCK)
                .forEach(b -> b.getState().update(true, true));

        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) return;

        Optional<CBlock> customBlock = CBlock.of(block);
        if (customBlock.isEmpty() || CBlock.isRegularNoteblock(block)) return;

        Optional<CItem> customItem = CItem.of(customBlock.get().getIdentifier());
        if (customItem.isEmpty()) return;

        ItemStack drop = customItem.get().getItemStack();
        event.setDropItems(false);
        block.getWorld().dropItemNaturally(block.getLocation(), drop);
    }

    /**
     * Places noteblock in the world, even if player right-clicked on another
     * noteblock. Without this handler player will just try to change noteblock's note,
     * everytime they click on one.
     *
     * @param event Event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onNoteblockClicked(@NotNull PlayerInteractEvent event) {
        if (event.getPlayer().isSneaking()
                || event.getAction() != Action.RIGHT_CLICK_BLOCK
                || event.getClickedBlock() == null
                || event.getClickedBlock().getType() != Material.NOTE_BLOCK
        ) return;
        event.setCancelled(true);

        Player player = event.getPlayer();
        ItemStack itemstack;
        itemstack = player.getInventory().getItemInMainHand();
        if (itemstack.getType() == Material.AIR) return;

        if (!CItem.isCustom(itemstack) && itemstack.getType().isBlock()) {
            Block placeBlock = event.getClickedBlock().getRelative(event.getBlockFace());
            if (!placeBlock.getWorld().getNearbyLivingEntities(placeBlock.getLocation().toCenterLocation(),
                    .5, .5, .5).isEmpty()) return;
            BlockData blockdata = itemstack.getType().createBlockData();
            if (!event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) itemstack.add(-1);
            placeBlock.setBlockData(blockdata, true);
            Sound placeSound = blockdata.getSoundGroup().getPlaceSound();
            placeBlock.getWorld().playSound(placeBlock.getLocation(), placeSound, 1f, 1f);
        }
    }

    /**
     * Cancels unintentional change of noteblock's tune via updates of neighbour blocks.
     *
     * @param event Event
     *
     * @see NoteblockTune
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void MY_onBlockPhysics(BlockPhysicsEvent event) {
        Block blockAbove = event.getBlock().getRelative(BlockFace.UP);
        if (blockAbove.getType() == Material.NOTE_BLOCK) {
            event.setCancelled(true);
            blockAbove.getState().update(true, true);
        }
        if (event.getBlock().getRelative(BlockFace.DOWN).getType() == Material.NOTE_BLOCK) {
            event.setCancelled(true);
        }
    }

    /**
     * Cancels noteblock's movement by a piston on extend.
     *
     * @param event Event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onNoteblockMoved(@NotNull BlockPistonExtendEvent event) {
        if (event.getBlocks().stream().anyMatch(b -> b.getType().equals(Material.NOTE_BLOCK))) {
            event.setCancelled(true);
        }
    }

    /**
     * Cancels noteblock's movement by a piston on retract.
     *
     * @param event Event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onNoteblockMoved(@NotNull BlockPistonRetractEvent event) {
        if (event.getBlocks().stream().anyMatch(b -> b.getType().equals(Material.NOTE_BLOCK))) {
            event.setCancelled(true);
        }
    }
}
