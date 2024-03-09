package dev.avatcher.cinnamon.core.block.listeners;

import com.destroystokyo.paper.event.block.BlockDestroyEvent;
import dev.avatcher.cinnamon.api.blocks.CustomBlock;
import dev.avatcher.cinnamon.api.items.CustomItem;
import dev.avatcher.cinnamon.core.CinnamonPlugin;
import dev.avatcher.cinnamon.core.block.NoteblockCustomBlock;
import dev.avatcher.cinnamon.core.block.NoteblockTune;
import dev.avatcher.cinnamon.core.block.events.CustomBlockBreakEventImpl;
import dev.avatcher.cinnamon.core.block.events.CustomBlockDestroyEventImpl;
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
        if (NoteblockCustomBlock.isRegularNoteblock(event.getBlock())) return;
        event.setCancelled(true);
    }

    /**
     * Manages how noteblocks are broken.
     *
     * @param event Event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onNoteblockBreak(BlockBreakEvent event) {
        if (this.updateSurroundings(event.getBlock())) return;
        Block block = event.getBlock();
        var customBlock = CustomBlock.get(block).orElseThrow();

        var behaviourEvent = CustomBlockBreakEventImpl.builder()
                .block(block)
                .customBlock(customBlock)
                .player(event.getPlayer())
                .tool(event.getPlayer().getActiveItem())
                .build();
        behaviourEvent.fire(customBlock.getBehaviour());
        event.setDropItems(false);
        if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            behaviourEvent.getDrop().forEach(itemStack -> block.getWorld().dropItemNaturally(block.getLocation(), itemStack));
        }
    }

    /**
     * Manages how noteblocks are destroyed.
     *
     * @param event Event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onNoteblockDestroy(BlockDestroyEvent event) {
        CinnamonPlugin.getInstance().getSLF4JLogger().warn("Destroyed: " + event.getBlock());
        if (this.updateSurroundings(event.getBlock())) return;
        Block block = event.getBlock();
        CustomBlock customBlock = CustomBlock.get(block).orElseThrow();

        var behaviourEvent = CustomBlockDestroyEventImpl.builder()
                .block(block)
                .customBlock(customBlock)
                .build();
        behaviourEvent.fire(customBlock.getBehaviour());
        event.setWillDrop(false);
        behaviourEvent.getDrop().forEach(itemStack -> block.getWorld().dropItemNaturally(block.getLocation(), itemStack));
    }

    private boolean updateSurroundings(Block block) {
        Stream.of(BlockFace.DOWN, BlockFace.UP)
                .map(block::getRelative)
                .filter(b -> b.getType() == Material.NOTE_BLOCK)
                .forEach(b -> b.getState().update(true, true));

        Optional<NoteblockCustomBlock> customBlock = NoteblockCustomBlock.of(block);
        return customBlock.isEmpty() || NoteblockCustomBlock.isRegularNoteblock(block);
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

        if (!CustomItem.isCustom(itemstack) && itemstack.getType().isBlock()) {
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
