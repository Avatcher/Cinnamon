package dev.avatcher.cinnamon.resources;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
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

/**
 * Manager of Cinnamon resources such as custom items,
 * item models or blocks.
 *
 * @see CinnamonResources
 */
public class CinnamonResourcesManager {
    /**
     * Path to plugin's folder with preload data
     */
    public static final String PRELOAD_FOLDER = "preload";

    /**
     * Map of registered CustomModelData
     */
    @Getter
    private final Map<NamespacedKey, CustomModelData> customModelMap;
    /**
     * Last {@link CustomModelData#numeric} assigned in CustomModelData registration
     */
    private int lastCustomModelNumeric = CustomModelData.START_NUMERIC;
    /**
     * Map of registered custom items
     */
    @Getter
    private final Map<NamespacedKey, CItem> customItemMap;

    private final Logger log;

    public CinnamonResourcesManager() throws IOException {
        this.log = Cinnamon.getInstance().getLogger();
        this.customModelMap = new HashMap<>();
        this.customItemMap = new HashMap<>();
        this.preload();
    }

    /**
     * Returns {@link CustomModelData} with the certain {@link CustomModelData#identifier}.
     * Empty optional will be returned, if CustomModelData was not found.
     *
     * @param identifier Item's identifier
     * @return Optional {@link CustomModelData} (Empty, if CustomModelData was not found)
     */
    public Optional<CustomModelData> getCustomModelData(NamespacedKey identifier) {
        return Optional.ofNullable(this.customModelMap.get(identifier));
    }

    /**
     * Returns {@link CItem} with the certain {@link CItem#identifier}.
     * Empty optional will be returned, if item was not found.
     *
     * @param identifier Item's identifier
     * @return Optional {@link CItem} (Empty, if item was not found)
     */
    public Optional<CItem> getCItem(NamespacedKey identifier) {
        return Optional.ofNullable(this.customItemMap.get(identifier));
    }

    /**
     * Registers {@link CustomModelData}
     *
     * @param customModelData CustomModelData to be registered
     */
    public void registerCustomModel(CustomModelData customModelData) {
        this.customModelMap.put(customModelData.identifier(), customModelData);
        log.info("Registered item model " + customModelData.identifier());
    }

    /**
     * Registers {@link CItem}
     *
     * @param cItem CItem to be registered
     */
    public void registerCItem(CItem cItem) {
        this.customItemMap.put(cItem.getIdentifier(), cItem);
        log.info("Registered item " + cItem.getIdentifier());
    }

    /**
     * Loads certain Cinnamon resources.
     *
     * @param resources Resources to be loaded
     */
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

    /**
     * Loads CustomModelData from {@link CinnamonResourcesLoader}
     *
     * @param loader Loader of Cinnamon resources
     */
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

    /**
     * Loads custom items from {@link CinnamonResourcesLoader}
     *
     * @param loader Loader of Cinnamon resources
     */
    private void loadItems(@NotNull CinnamonResourcesLoader loader) throws CinnamonResourcesLoadException {
        List<CItem> items = loader.loadItems();
        items.forEach(this::registerCItem);
        log.info("Loaded a total of " + items.size() + " items for plugin '"
                + loader.getResources().getPlugin().getName() + "'");
    }

    /**
     * Preloads some data from Cinnamon's {@value #PRELOAD_FOLDER} data folder.
     * This is a very important method, as it loads some previously registered
     * resources in Cinnamon.
     */
    private void preload() throws IOException {
        Path folder = Cinnamon.getInstance().getDataFolder().toPath().resolve(PRELOAD_FOLDER);
        if (!Files.exists(folder)) return;

        Path modelsPath = folder.resolve("models.json");
        if (Files.exists(modelsPath)) {
            try (var modelsReader = new InputStreamReader(Files.newInputStream(modelsPath))) {
                Map<String, Integer> itemModels = new Gson().fromJson(modelsReader, new TypeToken<>() {
                });
                for (var entry : itemModels.entrySet()) {
                    var model = new CustomModelData(NamespacedKey.fromString(entry.getKey()), entry.getValue());
                    this.customModelMap.put(model.identifier(), model);
                    log.info("Preloaded item model " + model.identifier());
                }
                log.info("Preloaded a total of " + itemModels.size() + " item models");
                this.lastCustomModelNumeric = itemModels.values().stream()
                        .max(Integer::compareTo)
                        .orElse(CustomModelData.START_NUMERIC);
            }
        }
    }

    /**
     * Saves some important registered resources into
     * Cinnamon's {@value #PRELOAD_FOLDER} data folder.
     */
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
