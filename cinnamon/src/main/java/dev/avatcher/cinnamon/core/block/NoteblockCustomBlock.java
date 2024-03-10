package dev.avatcher.cinnamon.core.block;

import com.google.common.base.Preconditions;
import dev.avatcher.cinnamon.api.blocks.CustomBlock;
import dev.avatcher.cinnamon.api.blocks.CustomBlockBehaviour;
import dev.avatcher.cinnamon.core.CinnamonPlugin;
import dev.avatcher.cinnamon.core.block.behaviour.DefaultCustomBlockBehaviour;
import dev.avatcher.cinnamon.core.block.behaviour.NoteblockBehaviour;
import dev.avatcher.cinnamon.core.block.events.CustomBlockPlaceEventImpl;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.Set;

/**
 * The representation of Cinnamon noteblock-based custom block
 */
@Getter
public class NoteblockCustomBlock implements CustomBlock {
    /**
     * A custom block representing a "normal" noteblock
     */
    public static final NoteblockCustomBlock NOTEBLOCK;

    static {
        NamespacedKey noteblockKey = NamespacedKey.minecraft("note_block");
        NamespacedKey noteblockModelKey = NamespacedKey.minecraft("block/note_block");
        NoteblockTune noteblockTune = new NoteblockTune(noteblockKey, (byte) 0, (byte) 0);
        NOTEBLOCK = new NoteblockCustomBlock(noteblockKey, noteblockModelKey, noteblockTune, new NoteblockBehaviour());
    }

    private final NamespacedKey identifier;
    private final NamespacedKey model;
    private final NoteblockTune tune;
    private CustomBlockBehaviour behaviour = new DefaultCustomBlockBehaviour();

    /**
     * Creates a new custom block based
     * on Minecraft noteblock's blockstates.
     *
     * @param key      The identifier of the block,
     *                 that is later can be used in
     *                 various methods such as
     *                 {@link CustomBlock#get(NamespacedKey)}
     * @param modelKey The key of block's model defined
     *                 inside a resource pack
     * @param tune     Noteblock tune reserved by
     *                 this block
     */
    public NoteblockCustomBlock(NamespacedKey key, NamespacedKey modelKey, NoteblockTune tune) {
        this(key, modelKey, tune, new DefaultCustomBlockBehaviour());
    }

    /**
     * Creates a new custom block based
     * on Minecraft noteblock's blockstates
     * with some behaviour.
     *
     * @param key       The identifier of the block,
     *                  that is later can be used in
     *                  various methods such as
     *                  {@link CustomBlock#get(NamespacedKey)}
     * @param modelKey  The key of block's model defined
     *                  inside a resource pack
     * @param tune      Noteblock tune reserved by
     *                  this block
     * @param behaviour The behaviour of the block
     */
    public NoteblockCustomBlock(NamespacedKey key, NamespacedKey modelKey, NoteblockTune tune, CustomBlockBehaviour behaviour) {
        this.identifier = key;
        this.model = modelKey;
        this.tune = tune;
        this.setBehaviour(behaviour);
    }

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
        this.placeAt(location, null);
    }

    /**
     * Places this custom block at a given location
     * by a specific player.
     *
     * @param location Location to place the custom
     *                 block at
     * @param player   Player that placed the block
     */
    public void placeAt(@NotNull Location location, Player player) {
        Block block = location.getBlock();
        NoteBlock blockData = (NoteBlock) Material.NOTE_BLOCK.createBlockData();
        blockData.setNote(this.tune.getNote());
        blockData.setInstrument(this.tune.getInstrument());
        block.setBlockData(blockData, true);

        CustomBlockPlaceEventImpl.builder()
                .block(block)
                .customBlock(this)
                .player(player)
                .build()
                .fire(this.getBehaviour());
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
     * Sets the behaviour of this block. If
     * {@code null} is provided, a new instance of
     * {@link DefaultCustomBlockBehaviour} will be used.
     *
     * @param behaviour New behaviour of the block
     */
    public void setBehaviour(@Nullable CustomBlockBehaviour behaviour) {
        this.behaviour = behaviour == null
                ? new DefaultCustomBlockBehaviour()
                : behaviour;
        CinnamonPlugin.getInstance().getSLF4JLogger().warn("Set behaviour of '{}' to {}",
                this.getKey(), this.behaviour.getClass().getSimpleName());
    }

    /**
     * Sets the behaviour of this block by a new instance
     * of a given behaviour's class. The method initializes
     * a new behaviour instance using either the constructor
     * accepting a single {@link CustomBlock} argument or
     * the No-Args constructor, if the first one is not found.
     *
     * @param behaviourClazz Class of block behaviour
     */
    public void setBehaviour(Class<?> behaviourClazz) {
        if (behaviourClazz == null) {
            this.setBehaviour((CustomBlockBehaviour) null);
            return;
        }
        Preconditions.checkArgument(CustomBlockBehaviour.class.isAssignableFrom(behaviourClazz),
                "Custom block behaviour class '%s' does not implement %s interface"
                        .formatted(behaviourClazz, CustomBlockBehaviour.class));
        @SuppressWarnings("unchecked")
        Class<? extends CustomBlockBehaviour> clazz = (Class<? extends CustomBlockBehaviour>) behaviourClazz;
        try {
            Constructor<? extends CustomBlockBehaviour> constructor = clazz.getConstructor(CustomBlock.class);
            CustomBlockBehaviour behaviour = constructor.newInstance(this);
            this.setBehaviour(behaviour);
            return;
        } catch (NoSuchMethodException ignored) {
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        try {
            Constructor<? extends CustomBlockBehaviour> constructor = clazz.getConstructor();
            CustomBlockBehaviour behaviour = constructor.newInstance();
            this.setBehaviour(behaviour);
            return;
        } catch (NoSuchMethodException ignored) {
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        // Reached, only if two instances of
        // NoSuchMethodException were thrown
        throw new IllegalStateException("Custom block behaviour '%s' does not have a matching constructor"
                .formatted(behaviourClazz));
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
