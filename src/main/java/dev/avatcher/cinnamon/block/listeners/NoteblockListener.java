package dev.avatcher.cinnamon.block.listeners;

import com.destroystokyo.paper.event.block.BlockDestroyEvent;
import dev.avatcher.cinnamon.block.CBlock;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

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
        if (CBlock.isRegularNoteblock(event.getBlock())) {
            return;
        }
        event.setCancelled(true);
    }

    /**
     * Manages how noteblocks are broken.
     *
     * @param event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onNoteblockDestroyed(BlockDestroyEvent event) {
        Block block = event.getBlock();
        if (block.getType() != Material.NOTE_BLOCK
                || CBlock.isRegularNoteblock(block)) return;
        ItemStack testDrop = new ItemStack(Material.EMERALD);
        testDrop.editMeta(meta -> {
            meta.displayName(Component.text("TEST DROP")
                    .color(NamedTextColor.YELLOW)
                    .decoration(TextDecoration.ITALIC, false));
        });
        event.setWillDrop(false);
        block.getWorld().dropItemNaturally(block.getLocation(), testDrop);
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
                || !(event.getAction() == Action.RIGHT_CLICK_BLOCK
                && event.getClickedBlock() != null
                && event.getClickedBlock().getType() == Material.NOTE_BLOCK
        )) return;
        event.setCancelled(true);

        Player player = event.getPlayer();
        ItemStack itemstack;
        itemstack = player.getInventory().getItemInMainHand();
        if (itemstack.getType() == Material.AIR) return;

        BlockFace blockFace = event.getBlockFace();
        Location placeLocation = event.getClickedBlock().getLocation().add(
                blockFace.getModX(), blockFace.getModY(), blockFace.getModZ()
        );
        World world = event.getClickedBlock().getWorld();
        // Cancel block placement, if there is an entity
        if (!world.getNearbyLivingEntities(
                placeLocation.add(.5, .5, .5), .5, .5, .5
        ).isEmpty()) return;
        BlockData blockdata = itemstack.getType().createBlockData();
        Sound placeSound = blockdata.getSoundGroup().getPlaceSound();

        if (!event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) itemstack.add(-1);
        world.setBlockData(placeLocation, blockdata);
        world.playSound(placeLocation, placeSound, 1f, 1f);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onNoteblockPlaced(BlockPlaceEvent event) {
        Block block = event.getBlock();
        if (block.getType() != Material.NOTE_BLOCK) return;
        NoteBlock blockData = (NoteBlock) block.getBlockData();
        blockData.setInstrument(Instrument.values()[0]);
        block.setBlockData(blockData);
    }

    /**
     * Cancels unintentional change of noteblock's tune via updates of neighbour blocks.
     *
     * @param event Event
     *
     * @see dev.avatcher.cinnamon.block.NoteblockTune
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPhysics(@NotNull BlockPhysicsEvent event) {
        Block aboveBlock = event.getBlock().getRelative(BlockFace.UP);
        if (aboveBlock.getType() == Material.NOTE_BLOCK) {
            updateAndCheck(aboveBlock);
            event.setCancelled(true);
        }
        if (event.getBlock().getType() == Material.NOTE_BLOCK) event.setCancelled(true);
        if (event.getBlock().getBlockData() instanceof Sign) return;
        event.getBlock().getState().update(true, false);
    }

    private void updateAndCheck(@NotNull Block block) {
        if (block.getType() == Material.NOTE_BLOCK) block.getState().update(true, false);
        Block aboveBlock = block.getRelative(BlockFace.UP);
        if (aboveBlock.getType() == Material.NOTE_BLOCK) updateAndCheck(aboveBlock);
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
