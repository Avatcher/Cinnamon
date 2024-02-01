package dev.avatcher.cinnamon.core.block;

import org.bukkit.Instrument;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.Note;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * A representation of a Minecraft noteblock tune,
 * including its note and instrument.
 */
public record NoteblockTune(NamespacedKey key, byte note, byte instrument) implements Keyed {
    /**
     * Gets the note of the noteblock tune.
     *
     * @return This tune's note
     */
    @Contract(value = " -> new", pure = true)
    public @NotNull Note getNote() {
        return new Note(this.note);
    }

    /**
     * Gets the instrument of the noteblock tune.
     *
     * @return This tune's instrument
     */
    @Contract(pure = true)
    public Instrument getInstrument() {
        return Instrument.values()[this.instrument];
    }

    /**
     * <p>Gets a minecraft name of the noteblock's instrument.</p>
     *
     * <p>
     *     All possible values include:
     * <ul>
     *     <li>{@code bass}</li>
     *     <li>{@code snare}</li>
     *     <li>{@code hat}</li>
     *     <li>{@code basedrum}</li>
     *     <li>{@code bell}</li>
     *     <li>{@code flute}</li>
     *     <li>{@code chime}</li>
     *     <li>{@code guitar}</li>
     *     <li>{@code xylophone}</li>
     *     <li>{@code cow_bell}</li>
     *     <li>{@code didgeridoo}</li>
     *     <li>{@code bit}</li>
     *     <li>{@code banjo}</li>
     *     <li>{@code pling}</li>
     *     <li>{@code skeleton}</li>
     *     <li>{@code wither_skeleton}</li>
     *     <li>{@code zombie}</li>
     *     <li>{@code creeper}</li>
     *     <li>{@code piglin}</li>
     *     <li>{@code ender_dragon}</li>
     *     <li>{@code harp}</li>
     * </ul>
     * </p>
     *
     * @return Minecraft name of this tune's instrument
     */
    public @NotNull String getInstrumentMcString() {
        String soundName = this.getInstrument().getSound().name().toLowerCase();
        System.out.println("s: " + soundName);
        return soundName.substring(soundName.lastIndexOf("_") + 1);
    }

    @Override
    public @NotNull NamespacedKey getKey() {
        return null;
    }
}
