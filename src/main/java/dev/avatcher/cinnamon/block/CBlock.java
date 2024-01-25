package dev.avatcher.cinnamon.block;

import dev.avatcher.cinnamon.Cinnamon;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.NoteBlock;

import java.util.Optional;
import java.util.Set;

@AllArgsConstructor
public class CBlock {
    public static final CBlock NOTEBLOCK = new CBlock(
            NamespacedKey.minecraft("note_block"),
            NamespacedKey.minecraft("block/note_block"),
            new NoteblockTune((byte) 0, (byte) 0)
    );

    @Getter
    private final NamespacedKey identifier;
    @Getter
    private final NamespacedKey model;
    @Getter
    private final NoteblockTune tune;

    public Note getNote() {
        return this.tune.getNote();
    }

    public Instrument getInstrument() {
        return this.tune.getInstrument();
    }

    public void placeAt(Location location) {
        Block block = location.getBlock();
        NoteBlock blockData = (NoteBlock) Material.NOTE_BLOCK.createBlockData();
        blockData.setNote(this.tune.getNote());
        blockData.setInstrument(this.tune.getInstrument());
        block.setBlockData(blockData);
    }

    public static boolean isCustom(Block block) {
        return block.getType() == Material.NOTE_BLOCK;
    }

    public static boolean isRegularNoteblock(Block block) {
        if (block.getType() != Material.NOTE_BLOCK) return false;
        NoteBlock blockData = (NoteBlock) block.getBlockData();
        return blockData.getNote().equals(CBlock.NOTEBLOCK.getNote())
                && blockData.getInstrument().equals(CBlock.NOTEBLOCK.getInstrument());
    }

    private static final Set<Material> INTERACTABLE_BLOCKS = Set.of(
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

    public static boolean isInteractable(Block block) {
        if (INTERACTABLE_BLOCKS.contains(block.getType())) {
            return true;
        }
        String type = block.getType().toString();
        return type.contains("CAULDRON") || type.contains("SIGN")
                || type.contains("BUTTON") || type.contains("a")
                || ( type.contains("DOOR") && !type.contains("IRON") );
    }

    public static Optional<CBlock> of(NamespacedKey key) {
        return Cinnamon.getInstance().getResourcesManager().getCustomBlocks().get(key);
    }

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
