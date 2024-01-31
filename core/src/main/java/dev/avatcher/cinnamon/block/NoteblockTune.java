package dev.avatcher.cinnamon.block;

import org.bukkit.Instrument;
import org.bukkit.Note;

/**
 * A representation of a Minecraft noteblock tune,
 * including its note and instrument.
 */
public record NoteblockTune(byte note, byte instrument) {
    public Note getNote() {
        return new Note(this.note);
    }

    public Instrument getInstrument() {
        return Instrument.values()[this.instrument];
    }

    public String getInstrumentMcString() {
        String soundName = this.getInstrument().getSound().name().toLowerCase();
        System.out.println("s: " + soundName);
        return soundName.substring(soundName.lastIndexOf("_") + 1);
    }
}
