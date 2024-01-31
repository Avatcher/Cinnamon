package dev.avatcher.cinnamon.core.resources.modules;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.avatcher.cinnamon.core.item.CItem;
import dev.avatcher.cinnamon.core.json.CItemDeserializer;
import dev.avatcher.cinnamon.core.resources.CinnamonModule;
import dev.avatcher.cinnamon.core.resources.CinnamonResources;
import dev.avatcher.cinnamon.core.resources.CustomModelData;
import lombok.Getter;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * A Cinnamon Module storing custom items
 *
 * @see CinnamonModule
 */
@Getter
public class CItemModule extends AbstractCinnamonModule<CItem> {
    private final CustomModelDataModule customModelDataModule;

    /**
     * Creates a new Custom Items Module with a
     * dependency on a certain CustomModelData Module.
     *
     * @param customModelDataModule A CustomModelData Module dependency
     *
     * @see CustomModelData
     */
    public CItemModule(CustomModelDataModule customModelDataModule) {
        super(CItem.class);
        this.customModelDataModule = customModelDataModule;
    }

    @Override
    public void register(NamespacedKey key, CItem item) {
        if (!this.customModelDataModule.getKeys().contains(item.getModel().identifier())) {
            this.customModelDataModule.register(item.getModel().identifier(), item.getModel());
        }
        super.register(key, item);
    }

    @Override
    public void load(@NotNull CinnamonResources resources) throws IOException {
        Path itemsFolder = resources.getItemsFolder();
        if (!Files.exists(itemsFolder)) return;
        try (var walker = Files.walk(itemsFolder)) {
            List<Path> files = walker.filter(Files::isRegularFile).toList();
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(CItem.class, new CItemDeserializer(resources.getPlugin()))
                    .create();
            int wasLoaded = this.map.size();
            files.stream()
                    .map(file -> {
                        try (var in = new InputStreamReader(Files.newInputStream(file))) {
                            return gson.fromJson(in, CItem.class);
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    })
                    .forEach(item -> this.register(item.getIdentifier(), item));
            int loaded = this.map.size() - wasLoaded;
            log.info("[%s] Loaded a total of %d item(s)".formatted(this.clazz.getSimpleName(), loaded));
        }
    }
}
