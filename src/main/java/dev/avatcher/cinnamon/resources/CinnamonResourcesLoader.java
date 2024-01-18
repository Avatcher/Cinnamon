package dev.avatcher.cinnamon.resources;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.avatcher.cinnamon.Cinnamon;
import dev.avatcher.cinnamon.block.CBlock;
import dev.avatcher.cinnamon.item.CItem;
import dev.avatcher.cinnamon.json.CBlockDeserializer;
import dev.avatcher.cinnamon.json.CItemDeserializer;
import dev.avatcher.cinnamon.json.ItemStackDeserializer;
import dev.avatcher.cinnamon.json.RecipeDeserializer;
import dev.avatcher.cinnamon.json.value.Value;
import dev.avatcher.cinnamon.json.value.ValueProvider;
import dev.avatcher.cinnamon.resources.config.CinnamonResourcesConfig;
import dev.avatcher.cinnamon.resources.exceptions.CinnamonConfigLoadException;
import dev.avatcher.cinnamon.resources.exceptions.CinnamonResourcesLoadException;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Loader of Cinnamon resources
 */
public class CinnamonResourcesLoader implements AutoCloseable {
    private final Logger log;

    /**
     * Cinnamon resources
     */
    @Getter
    private final CinnamonResources resources;

    /**
     * Loader configuration loaded from {@value CinnamonResources#CONFIG_FILE} file
     */
    @Getter
    @Setter
    private CinnamonResourcesConfig config;

    public CinnamonResourcesLoader(CinnamonResources resources) {
        this.log = Cinnamon.getInstance().getLogger();
        this.resources = resources;
    }

    @Override
    public void close() throws IOException {
        this.resources.close();
    }

    /**
     * Reads and returns configuration for resources loader.
     *
     * @return Configuration for {@link CinnamonResourcesLoader}
     */
    public CinnamonResourcesConfig loadConfig() throws CinnamonConfigLoadException {
        Path configPath = this.resources.getConfig();
        try (var in = new InputStreamReader(this.resources.read(configPath))) {
            return new Yaml().loadAs(in, CinnamonResourcesConfig.class);
        } catch (Throwable e) {
            throw new CinnamonConfigLoadException(this.resources, e);
        }
    }

    /**
     * Reads and returns custom items from {@link #resources}.
     *
     * @return A {@link List} of custom items
     */
    public List<CItem> loadItems() throws CinnamonResourcesLoadException {
        Path itemsFolder = this.resources.getItemsFolder();
        try (var walker = Files.walk(itemsFolder)) {
            List<Path> files = walker.filter(Files::isRegularFile).toList();
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(CItem.class, new CItemDeserializer(this.resources.getPlugin()))
                    .create();
            return files.stream()
                    .map(file -> {
                        try (var in = new InputStreamReader(this.resources.read(file))) {
                            return gson.fromJson(in, CItem.class);
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    })
                    .toList();
        } catch (NoSuchFileException e) {
            return List.of();
        } catch (Exception e) {
            throw new CinnamonResourcesLoadException(this.resources,
                    String.format("Failed to load an item from resource '%s'", this.resources), e);
        }
    }

    public List<CBlock.RegistrationRequest> loadBlocks() throws CinnamonResourcesLoadException {
        Path blocksFolder = this.resources.getBlocksFolder();
        if (!Files.exists(blocksFolder)) return List.of();
        try (var walker = Files.walk(blocksFolder)) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(CBlock.RegistrationRequest.class,
                            new CBlockDeserializer(this.getResources().getPlugin()))
                    .create();
            return walker.filter(Files::isRegularFile)
                    .map(file -> {
                        try (var in = new InputStreamReader(Files.newInputStream(file))) {
                            return gson.fromJson(in, CBlock.RegistrationRequest.class);
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    })
                    .toList();
        } catch (IOException e) {
            throw new CinnamonResourcesLoadException(this.resources,
                    String.format("Failed to load a block from resource '%s'", this.resources), e);
        }
    }

    /**
     * Reads and returns item model names from {@link #resources}.
     *
     * @return A {@link List} of item model names
     */
    public List<NamespacedKey> loadCustomModelIdentifiers() throws CinnamonResourcesLoadException {
        Path modelsFolder = this.resources.getCustomModelsFolder();
        if (!Files.exists(modelsFolder)) return List.of();
        try (var walker = Files.walk(modelsFolder.resolve("item/"))) {
            return walker.filter(Files::isRegularFile)
                    .map(f -> {
                        String fName = modelsFolder.relativize(f).toString();
                        return fName.substring(0, fName.indexOf(".json"));
                    })
                    .map(name -> new NamespacedKey(this.resources.getPlugin(), name))
                    .toList();
        } catch (IOException e) {
            throw new CinnamonResourcesLoadException(this.resources,
                    String.format("Failed to load an item model from resource '%s'", this.resources), e);
        }
    }

    /**
     * Reads and returns a list of recipes and their identifiers from {@link #resources}.
     *
     * @return Recipe identifier and recipe
     */
    public List<Map.Entry<NamespacedKey, Recipe>> loadRecipes() throws CinnamonResourcesLoadException {
        Path recipesFolder = this.resources.getRecipesFolder();
        if (!Files.exists(recipesFolder)) return List.of();
        try (var walker = Files.walk(recipesFolder)) {
            ValueProvider<NamespacedKey> identifierProvider = new ValueProvider<>();
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Value.class, identifierProvider)
                    .registerTypeAdapter(ItemStack.class, new ItemStackDeserializer())
                    .registerTypeAdapter(Recipe.class, new RecipeDeserializer())
                    .create();
            return walker.filter(Files::isRegularFile)
                    .map(recipePath -> {
                        String relativeRecipePath = recipesFolder.relativize(recipePath).toString();
                        String recipeName = relativeRecipePath.substring(0, relativeRecipePath.indexOf(".json"));
                        NamespacedKey recipeIdentifier = new NamespacedKey(this.resources.getPlugin(), recipeName);
                        identifierProvider.setValue(recipeIdentifier);
                        try {
                            return Map.entry(
                                    recipeIdentifier,
                                    gson.fromJson(Files.readString(recipePath), Recipe.class));
                        } catch (IOException e) {
                            log.warning("Failed to load recipe '" + recipeIdentifier + "'");
                            log.warning(e.getMessage());
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .toList();
        } catch (IOException e) {
            throw new CinnamonResourcesLoadException(this.resources, "", e);
        }
    }
}
