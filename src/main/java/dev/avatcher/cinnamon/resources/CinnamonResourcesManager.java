package dev.avatcher.cinnamon.resources;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.avatcher.cinnamon.Cinnamon;
import dev.avatcher.cinnamon.config.CinnamonConfig;
import dev.avatcher.cinnamon.exceptions.CinnamonRuntimeException;
import dev.avatcher.cinnamon.item.CItem;
import dev.avatcher.cinnamon.resources.exceptions.CinnamonResourcesLoadException;
import lombok.Getter;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class CinnamonResourcesManager {
    public static final String PRELOAD_FOLDER = "preload";
    private final Logger log;

    @Getter
    private final Map<NamespacedKey, CustomModelData> customModelMap;
    private int lastCustomModelNumeric = CustomModelData.START_NUMERIC;
    @Getter
    private final Map<NamespacedKey, CItem> customItemMap;

    public CinnamonResourcesManager() throws IOException {
        this.log = Cinnamon.getInstance().getLogger();
        this.customModelMap = new HashMap<>();
        this.customItemMap = new HashMap<>();
        this.preload();
    }

    public Optional<CustomModelData> getCustomModel(NamespacedKey identifier) {
        return Optional.ofNullable(this.customModelMap.get(identifier));
    }

    public Optional<CItem> getCItem(NamespacedKey identifier) {
        return Optional.ofNullable(this.customItemMap.get(identifier));
    }

    public void registerCustomModel(CustomModelData customModelData) {
        this.customModelMap.put(customModelData.identifier(), customModelData);
        log.info("Registered item model " + customModelData.identifier());
    }

    public void registerCItem(CItem cItem) {
        this.customItemMap.put(cItem.getIdentifier(), cItem);
        log.info("Registered item " + cItem.getIdentifier());
    }

    public void load(CinnamonResources resources) {
        try (var loader = new CinnamonResourcesLoader(resources)) {
            CinnamonConfig config = loader.loadConfig();
            loader.setConfig(config);

            this.loadItemModels(loader);
            this.loadItems(loader);

            this.savePreload();
        } catch (CinnamonResourcesLoadException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            log.severe("Failed to load resources '" + resources + "' for plugin '"
                    + resources.getPlugin().getName() + "'");
        } catch (IOException e) {
            throw new CinnamonRuntimeException(e);
        }
    }

    private void loadItemModels(@NotNull CinnamonResourcesLoader loader) throws CinnamonResourcesLoadException {
        List<CustomModelData> models = loader.loadCustomModelIdentifiers().stream()
                .filter(modelName -> !this.customModelMap.containsKey(modelName))
                .map(modelName -> new CustomModelData(modelName, this.lastCustomModelNumeric++))
                .toList();
        models.forEach(this::registerCustomModel);
        if (!models.isEmpty()) {
            log.info("Loaded a total of " + models.size() + " new item models for plugin '"
                    + loader.getResources().getPlugin().getName() + "'");
        }
    }

    private void loadItems(@NotNull CinnamonResourcesLoader loader) throws CinnamonResourcesLoadException {
        List<CItem> items = loader.loadItems();
        items.forEach(this::registerCItem);
        log.info("Loaded a total of " + items.size() + " items for plugin '"
                + loader.getResources().getPlugin().getName() + "'");
    }

    private void preload() throws IOException {
        Path folder = Cinnamon.getInstance().getDataFolder().toPath().resolve(PRELOAD_FOLDER);
        if (!Files.exists(folder)) return;

        Path modelsPath = folder.resolve("models.json");
        if (Files.exists(modelsPath)) {
            try (var modelsReader = new InputStreamReader(Files.newInputStream(modelsPath))) {
                @SuppressWarnings("unchecked")
                Map<String, Integer> registeredModels = (Map<String, Integer>)
                        ((Map<?, ?>) new Gson().fromJson(modelsReader, Map.class))
                                .entrySet()
                                .stream()
                                .filter(entry -> entry.getKey().getClass().equals(String.class)
                                        && entry.getValue().getClass().equals(Integer.class))
                                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                for (var entry : registeredModels.entrySet()) {
                    var model = new CustomModelData(NamespacedKey.fromString(entry.getKey()), entry.getValue());
                    this.registerCustomModel(model);
                }
                this.lastCustomModelNumeric = registeredModels.values().stream()
                        .max(Integer::compareTo)
                        .orElse(CustomModelData.START_NUMERIC);
            }
        }
    }

    private void savePreload() throws IOException {
        Path folder = Cinnamon.getInstance().getDataFolder().toPath().resolve(PRELOAD_FOLDER);
        Files.createDirectories(folder);

        Path modelsPath = folder.resolve("models.json");
        Map<String, Integer> modelMap = this.customModelMap.values().stream()
                .map(model -> Map.entry(model.identifier().asString(), model.numeric()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        String modelJson = new GsonBuilder()
                .setPrettyPrinting()
                .create()
                .toJson(modelMap);
        Files.writeString(modelsPath, modelJson);
    }
}
