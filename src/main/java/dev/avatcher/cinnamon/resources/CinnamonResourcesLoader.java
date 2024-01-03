package dev.avatcher.cinnamon.resources;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.avatcher.cinnamon.Cinnamon;
import dev.avatcher.cinnamon.config.CinnamonConfig;
import dev.avatcher.cinnamon.exceptions.CinnamonException;
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
import java.nio.file.*;
import java.util.List;
import java.util.logging.Logger;

public class CinnamonResourcesLoader implements AutoCloseable {
    public static final String ITEMS_FOLDER = "items";
    public static final String CONFIG_FILE = "cinnamon.yml";

    private final Logger log;

    @Getter
    private final CinnamonResources resources;

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

    public CinnamonConfig loadConfig() throws CinnamonConfigLoadException {
        Path configPath = this.resources.getConfig();
        try (var in = new InputStreamReader(this.resources.read(configPath))) {
            return new Yaml().loadAs(in, CinnamonConfig.class);
        } catch (Throwable e) {
            throw new CinnamonConfigLoadException(this.resources, e);
        }
    }

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
