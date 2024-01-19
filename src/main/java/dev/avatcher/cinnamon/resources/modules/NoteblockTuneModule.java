package dev.avatcher.cinnamon.resources.modules;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dev.avatcher.cinnamon.block.CBlock;
import dev.avatcher.cinnamon.block.NoteblockTune;
import dev.avatcher.cinnamon.json.NamespacedKeyAdapter;
import dev.avatcher.cinnamon.resources.CinnamonResources;
import dev.avatcher.cinnamon.resources.Preloadable;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 * A Cinnamon Module storing noteblock tunes
 *
 * @see dev.avatcher.cinnamon.resources.CinnamonModule
 */
public class NoteblockTuneModule extends AbstractCinnamonModule<NoteblockTune> implements Preloadable {
    /**
     * The name of the file where
     * the preload data is stored
     */
    public static final String PRELOAD_FILE = "NoteblockTunes.json";

    /**
     * Creates a new noteblock tunes module.
     */
    public NoteblockTuneModule() {
        super(NoteblockTune.class);
    }

    /**
     * Finds and returns the next free
     * noteblock tone, that can be registered.
     *
     * @param key The key willing to be associated
     *            with a noteblock tone
     * @return The next free noteblock tone
     */
    public NoteblockTune getFreeTone(NamespacedKey key) {
        if (this.get(key).isPresent()) {
            return this.get(key).get();
        }
        byte note = this.getValues().stream()
                .map(NoteblockTune::note)
                .max(Byte::compareTo)
                .orElse((byte) 0);
        byte instrument = this.getValues().stream()
                .map(NoteblockTune::instrument)
                .max(Byte::compareTo)
                .orElse((byte) 0);
        if (note + 1 > 24) {
            note = (byte) 0;
            instrument = (byte) (instrument + 1);
        } else {
            note = (byte) (note + 1);
        }
        return new NoteblockTune(note, instrument);
    }

    @Override
    public void register(NamespacedKey key, NoteblockTune tune) {
        if (this.map.containsKey(key)) {
            log.severe("[%s] Overriding NoteblockTune is not allowed: %s".formatted(
                    this.clazz.getSimpleName(), key));
            return;
        }
        super.register(key, tune);
    }

    @Override
    public void load(@NotNull CinnamonResources resources) throws IOException {
        // The method is empty, as noteblock tunes
        // cannot be defined in cinnamon resources,
        // and are registered automatically.
    }

    /**
     * Preloads a noteblock tune.
     *
     * @param key The key of the noteblock tune
     * @param tune The tune to be registered
     */
    private void preloadTune(NamespacedKey key, NoteblockTune tune) {
        if (this.map.containsKey(key)) return;
        if (tune.equals(CBlock.NOTEBLOCK.getTune())) {
            log.warning("[%s] Override default noteblock's tune (0,0) is not allowed: %s"
                    .formatted(this.clazz.getSimpleName(), key));
            return;
        }
        this.map.put(key, tune);
        log.info("[%s] Preloaded tune: %s"
                .formatted(this.clazz.getSimpleName(), key));
    }

    @Override
    public void preload(Path folder) throws IOException {
        Path tunePath = folder.resolve(PRELOAD_FILE);
        if (!Files.exists(tunePath)) return;
        try (var reader = new InputStreamReader(Files.newInputStream(tunePath))) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(NamespacedKey.class, new NamespacedKeyAdapter())
                    .create();
            Map<NamespacedKey, NoteblockTune> tunes = gson.fromJson(reader, new TypeToken<>(){});
            int wasLoaded = this.map.size();
            tunes.forEach(this::preloadTune);
            int loaded = this.map.size() - wasLoaded;
            log.info("[%s] Preloaded a total of %d NoteblockTune(s)"
                    .formatted(this.clazz.getSimpleName(), loaded));
        }
    }

    @Override
    public void savePreload(Path folder) throws IOException {
        Path tunesPath = folder.resolve(PRELOAD_FILE);
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(NamespacedKey.class, new NamespacedKeyAdapter())
                .setPrettyPrinting()
                .create();
        Files.writeString(tunesPath, gson.toJson(this.map));
    }
}
