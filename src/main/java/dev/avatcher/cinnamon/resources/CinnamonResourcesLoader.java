package dev.avatcher.cinnamon.resources;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.avatcher.cinnamon.Cinnamon;
import dev.avatcher.cinnamon.config.CinnamonConfig;
import dev.avatcher.cinnamon.item.CItem;
import dev.avatcher.cinnamon.json.CItemDeserializer;
import dev.avatcher.cinnamon.resources.exceptions.CinnamonConfigLoadException;
import dev.avatcher.cinnamon.resources.exceptions.CinnamonItemsLoadException;
import dev.avatcher.cinnamon.resources.exceptions.CinnamonResourcesInitializationException;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.Plugin;
import org.yaml.snakeyaml.Yaml;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.net.URI;
import java.nio.file.*;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

public class CinnamonResourcesLoader implements AutoCloseable {
    public static final String CINNAMON_FOLDER = "cinnamon";
    public static final String ITEMS_FOLDER = "items";
    public static final String CONFIG_FILE = "cinnamon.yml";

    private final Logger log = Cinnamon.getInstance().getLogger();

    @Getter
    private final CinnamonResources resources;

    @Getter
    @Setter
    private CinnamonConfig config;

    public CinnamonResourcesLoader(CinnamonResources resources) {
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

    public List<CItem> loadItems() throws CinnamonItemsLoadException {
        try (var walker = Files.walk(this.resources.getFolder().resolve(ITEMS_FOLDER))) {
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
        } catch (Throwable e) {
            throw new CinnamonItemsLoadException(this.resources, e);
        }
    }
}
