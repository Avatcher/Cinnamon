package dev.avatcher.cinnamon.core.block;

import dev.avatcher.cinnamon.api.blocks.CustomBlock;
import dev.avatcher.cinnamon.core.CinnamonPlugin;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
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
public class NoteblockCustomBlock implements CustomBlock {
    /**
     * A custom block representing a "normal" noteblock
     */
    public static final NoteblockCustomBlock NOTEBLOCK;

    static {
        NamespacedKey noteblockKey = NamespacedKey.minecraft("note_block");
        NamespacedKey noteblockModelKey = NamespacedKey.minecraft("block/note_block");
        NoteblockTune noteblockTune = new NoteblockTune(noteblockKey, (byte) 0, (byte) 0);
        NOTEBLOCK = new NoteblockCustomBlock(noteblockKey, noteblockModelKey, noteblockTune);
    }

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

    @Override
    public BlockData createBlockData() {
        return null;
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
        return blockData.getNote().equals(NoteblockCustomBlock.NOTEBLOCK.getNote())
                && blockData.getInstrument().equals(NoteblockCustomBlock.NOTEBLOCK.getInstrument());
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
    public static boolean isInteractable(Block block) {
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
    public static Optional<NoteblockCustomBlock> of(NamespacedKey key) {
        CustomBlock customBlock = CinnamonPlugin.getInstance().getResourcesManager().getCustomBlocks().get(key);
        if (!(customBlock instanceof NoteblockCustomBlock noteblockCustomBlock)) return Optional.empty();
        return Optional.of(noteblockCustomBlock);
    }

    /**
     * Gets a custom block of the specific placed block.
     *
     * @param block Block that is assumed to be custom
     * @return {@code Optional.empty()}, if the given block
     *         is not custom and finding a custom block
     *         is not possible
     */
    public static Optional<NoteblockCustomBlock> of(Block block) {
        if (!isCustom(block)) return Optional.empty();
        NoteBlock blockData = (NoteBlock) block.getBlockData();
        Note blockNote = blockData.getNote();
        Instrument blockInstrument = blockData.getInstrument();
        return CinnamonPlugin.getInstance().getResourcesManager().getCustomBlocks().stream()
                .filter(customBlock -> customBlock instanceof NoteblockCustomBlock)
                .map(customBlock -> (NoteblockCustomBlock) customBlock)
                .filter(customBlock -> customBlock.getNote().equals(blockNote))
                .filter(customBlock -> customBlock.getInstrument().equals(blockInstrument))
                .findAny();
    }

    @Override
    public @NotNull NamespacedKey getKey() {
        return this.getIdentifier();
    }
}
