package dev.avatcher.cinnamon.core.block;

import dev.avatcher.cinnamon.core.Cinnamon;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.NoteBlock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;

/**
 * The representation of Cinnamon noteblock-based custom block
 */
@Getter
@AllArgsConstructor
public class CBlock {
    /**
     * A custom block representing a "normal" noteblock
     */
    public static final CBlock NOTEBLOCK = new CBlock(
            NamespacedKey.minecraft("note_block"),
            NamespacedKey.minecraft("block/note_block"),
            new NoteblockTune((byte) 0, (byte) 0)
    );

    private final NamespacedKey identifier;
    private final NamespacedKey model;
    private final NoteblockTune tune;

    /**
     * Gets the note of the noteblock's tune
     *
     * @return Noteblock's note
     *
     * @see NoteblockTune
     */
    public Note getNote() {
        return this.tune.getNote();
    }

    /**
     * Gets the note of the noteblock's tune
     *
     * @return Noteblock's instrument
     *
     * @see NoteblockTune
     */
    public Instrument getInstrument() {
        return this.tune.getInstrument();
    }

    /**
     * Places this custom block at a given location.
     *
     * @param location Location to place the custom
     *                 block at
     */
    public void placeAt(@NotNull Location location) {
        Block block = location.getBlock();
        NoteBlock blockData = (NoteBlock) Material.NOTE_BLOCK.createBlockData();
        blockData.setNote(this.tune.getNote());
        blockData.setInstrument(this.tune.getInstrument());
        block.setBlockData(blockData, true);
    }

    /**
     * Checks, if certain block is custom.
     *
     * @param block Block to be checked
     * @return {@code true}, if it is a custom block
     */
    public static boolean isCustom(@Nullable Block block) {
        return block != null && block.getType() == Material.NOTE_BLOCK;
    }

    /**
     * Checks, if certain block is a custom block
     * <strong>and</strong> it is a normal noteblock.
     *
     * @param block Block to be checked
     * @return {@code true}, if it is a normal noteblock
     */
    public static boolean isRegularNoteblock(@Nullable Block block) {
        if (!isCustom(block)) return false;
        NoteBlock blockData = (NoteBlock) block.getBlockData();
        return blockData.getNote().equals(CBlock.NOTEBLOCK.getNote())
                && blockData.getInstrument().equals(CBlock.NOTEBLOCK.getInstrument());
    }

    /**
     * The set of blocks player can interact
     * with using right-click
     */
    private static final Set<Material> INTRACTABLE_BLOCKS = Set.of(
            Material.CRAFTING_TABLE,
            Material.FURNACE,
            Material.SMOKER,
            Material.BLAST_FURNACE,
            Material.SMITHING_TABLE,
            Material.CARTOGRAPHY_TABLE,
            Material.BREWING_STAND,
            Material.CHEST,
            Material.TRAPPED_CHEST,
            Material.ENDER_CHEST,
            Material.SHULKER_BOX,
            Material.BARREL,
            Material.LEVER
    );

    /**
     * Checks, if player can interact with a
     * certain block by right-clicking it.
     *
     * @param block Block to be checked
     * @return {@code true}, if player can right-click
     *         the block for interaction
     */
    public static boolean isIntractable(Block block) {
        if (INTRACTABLE_BLOCKS.contains(block.getType())) {
            return true;
        }
        String type = block.getType().toString();
        return type.contains("CAULDRON") || type.contains("SIGN")
                || type.contains("BUTTON") || type.contains("a")
                || ( type.contains("DOOR") && !type.contains("IRON") );
    }

    /**
     * Gets a custom block with a specific key.
     *
     * @param key Key of the custom block
     * @return {@code Optional.empty()}, if the custom block
     *         is not found
     */
    public static Optional<CBlock> of(NamespacedKey key) {
        return Cinnamon.getInstance().getResourcesManager().getCustomBlocks().get(key);
    }

    /**
     * Gets a custom block of the specific placed block.
     *
     * @param block Block that is assumed to be custom
     * @return {@code Optional.empty()}, if the given block
     *         is not custom and finding a custom block
     *         is not possible
     */
    public static Optional<CBlock> of(Block block) {
        if (!isCustom(block)) return Optional.empty();
        NoteBlock blockData = (NoteBlock) block.getBlockData();
        NoteblockTune blockTune = new NoteblockTune(blockData.getNote().getId(), blockData.getInstrument().getType());
        return Cinnamon.getInstance().getResourcesManager()
                .getCustomBlocks()
                .getValues()
                .stream()
                .filter(cBlock -> cBlock.getTune().equals(blockTune))
                .findAny();
    }
}
