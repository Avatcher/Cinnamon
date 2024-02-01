package dev.avatcher.cinnamon.core.resources.registries;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dev.avatcher.cinnamon.core.json.NamespacedKeyAdapter;
import dev.avatcher.cinnamon.core.resources.CinnamonRegistry;
import dev.avatcher.cinnamon.core.resources.CinnamonResources;
import dev.avatcher.cinnamon.core.resources.CustomModelData;
import dev.avatcher.cinnamon.core.resources.Preloadable;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A Cinnamon Module storing CustomModelData
 *
 * @see CinnamonRegistry
 */
public class CustomModelDataRegistry extends AbstractCinnamonRegistry<CustomModelData> implements Preloadable {
    /**
     * The name of the file where
     * the preload data is stored
     */
    public static final String PRELOAD_FILE = "CustomModelData.json";

    private int lastModelId;

    /**
     * Creates a new CustomModelData Module.
     * <br>
     * Numeration of the CustomModelData
     * will begin at {@value CustomModelData#START_NUMERIC}.
     */
    public CustomModelDataRegistry() {
        super(CustomModelData.class);
        this.lastModelId = CustomModelData.START_NUMERIC;
    }

    @Override
    public void register(NamespacedKey key, CustomModelData customModelData) {
        if (this.map.containsKey(key)) {
            log.severe("[%s] Overriding CustomModelData is not allowed: %s".formatted(
                    this.clazz.getSimpleName(), key));
            return;
        }
        super.register(key, customModelData);
    }

    /**
     * Creates and registers a Custom Model Data with
     * a given name, automatically reserving the next
     * free numeric id.
     *
     * @param key The name of the model
     * @return Created and registered Custom Model Data
     */
    public CustomModelData createAndRegister(NamespacedKey key) {
        CustomModelData customModelData = new CustomModelData(key, ++this.lastModelId);
        this.register(key, customModelData);
        return customModelData;
    }

    /**
     * Registers a CustomModelData, but does not show an error,
     * when tried to override an existing CustomModelData.
     *
     * @param key Key of the CustomModelData
     * @param customModelData CustomModelData to be registered
     */
    private void softRegister(NamespacedKey key, CustomModelData customModelData) {
        if (this.map.containsKey(key)) return;
        super.register(key, customModelData);
    }

    @Override
    public void load(@NotNull CinnamonResources resources) throws IOException {
        Path modelsFolder = resources.getCustomModelsFolder();
        if (Files.exists(modelsFolder)) {
            try (var walker = Files.walk(modelsFolder.resolve("item/"))) {
                int wasLoaded = this.map.size();
                walker.filter(Files::isRegularFile)
                        .map(f -> {
                            String fName = modelsFolder.relativize(f).toString();
                            return fName.substring(0, fName.indexOf(".json"));
                        })
                        .map(name -> new NamespacedKey(resources.getPlugin(), name))
                        .map(modelName -> new CustomModelData(modelName, ++this.lastModelId))
                        .forEach(model -> this.softRegister(model.identifier(), model));
                int loaded = this.map.size() - wasLoaded;
                if (wasLoaded > 0) {
                    log.info("[%s] Loaded a total of %d custom model data"
                            .formatted(this.clazz.getSimpleName(), loaded));
                }
            }
        }
    }

    /**
     * Preloads a CustomModelData
     *
     * @param model CustomModelData to preload
     */
    public void preloadModel(CustomModelData model) {
        this.map.put(model.identifier(), model);
        log.info("[%s] Preloaded model: %s".formatted(this.clazz.getSimpleName(), model.identifier()));
    }

    @Override
    public void preload(Path folder) throws IOException {
        Path modelsPath = folder.resolve(PRELOAD_FILE);
        if (!Files.exists(modelsPath)) return;
        try (var modelsReader = new InputStreamReader(Files.newInputStream(modelsPath))) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(NamespacedKey.class, new NamespacedKeyAdapter())
                    .create();
            Map<Integer, NamespacedKey> itemModels = gson.fromJson(modelsReader, new TypeToken<>() {});
            for (var entry : itemModels.entrySet()) {
                var model = new CustomModelData(entry.getValue(), entry.getKey());
                this.preloadModel(model);
            }
            log.info("[%s] Preloaded a total of %d models".formatted(this.clazz.getSimpleName(), itemModels.size()));
            int biggestId = itemModels.keySet().stream()
                    .max(Integer::compareTo)
                    .orElse(0);
            if (biggestId > this.lastModelId) {
                this.lastModelId = biggestId;
            }
        }
    }

    @Override
    public void savePreload(Path folder) throws IOException {
        Path modelsPath = folder.resolve(PRELOAD_FILE);
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(NamespacedKey.class, new NamespacedKeyAdapter())
                .setPrettyPrinting()
                .create();
        Map<Integer, NamespacedKey> models = this.map.values().stream()
                .collect(Collectors.toMap(CustomModelData::numeric, CustomModelData::identifier));
        Files.writeString(modelsPath, gson.toJson(models));
    }
}
