package dev.avatcher.cinnamon.resources;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.avatcher.cinnamon.Cinnamon;
import dev.avatcher.cinnamon.config.CinnamonConfig;
import dev.avatcher.cinnamon.item.CItem;
import dev.avatcher.cinnamon.json.CItemDeserializer;
import dev.avatcher.cinnamon.resources.exceptions.CinnamonConfigLoadException;
import dev.avatcher.cinnamon.resources.exceptions.CinnamonResourcesLoadException;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.NamespacedKey;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;
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
    private CinnamonConfig config;

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
    public CinnamonConfig loadConfig() throws CinnamonConfigLoadException {
        Path configPath = this.resources.getConfig();
        try (var in = new InputStreamReader(this.resources.read(configPath))) {
            return new Yaml().loadAs(in, CinnamonConfig.class);
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

    /**
     * Reads and returns item model names from {@link #resources}.
     *
     * @return A {@link List} of item model names
     */
    public List<NamespacedKey> loadCustomModelIdentifiers() throws CinnamonResourcesLoadException {
        Path modelsFolder = this.resources.getCustomModelsFolder();
        if (!Files.exists(modelsFolder)) return List.of();
        try (var walker = Files.walk(modelsFolder)) {
            return walker.filter(Files::isRegularFile)
                    .map(f -> {
                        String fName = f.getFileName().toString();
                        return fName.substring(0, fName.indexOf(".json"));
                    })
                    .map(name -> new NamespacedKey(this.resources.getPlugin(), name))
                    .toList();
        } catch (IOException e) {
            throw new CinnamonResourcesLoadException(this.resources,
                    String.format("Failed to load an item model from resource '%s'", this.resources), e);
        }
    }
}
