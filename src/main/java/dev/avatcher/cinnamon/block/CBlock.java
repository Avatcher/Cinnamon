package dev.avatcher.cinnamon.block;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.NoteBlock;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

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

    public void placeAt(@NotNull World world, Location location) {
        Block block = world.getBlockAt(location);
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

    public static Optional<CBlock> of(Block block) {
        if (!isCustom(block)) return Optional.empty();
        // TODO: Store custom blocks in Resources manager
        return Optional.empty();
    }

    @Builder
    @Getter
    public static class RegistrationRequest {
        private NamespacedKey identifier;
        private NamespacedKey model;
    }
}
