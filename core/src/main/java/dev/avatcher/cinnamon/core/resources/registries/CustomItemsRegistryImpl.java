package dev.avatcher.cinnamon.core.resources.registries;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.avatcher.cinnamon.api.items.CustomItem;
import dev.avatcher.cinnamon.api.items.CustomItemsRegistry;
import dev.avatcher.cinnamon.core.item.CustomItemImpl;
import dev.avatcher.cinnamon.core.json.CItemDeserializer;
import dev.avatcher.cinnamon.core.resources.CinnamonRegistry;
import dev.avatcher.cinnamon.core.resources.CinnamonResources;
import dev.avatcher.cinnamon.core.resources.CustomModelData;
import lombok.Getter;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
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
 * @see CinnamonRegistry
 */
@Getter
public class CustomItemsRegistryImpl extends AbstractCinnamonRegistry<CustomItem> implements CustomItemsRegistry {
    private final CustomModelDataRegistry customModelDataModule;

    /**
     * Creates a new Custom Items Module with a
     * dependency on a certain CustomModelData Module.
     *
     * @param customModelDataModule A CustomModelData Module dependency
     *
     * @see CustomModelData
     */
    public CustomItemsRegistryImpl(CustomModelDataRegistry customModelDataModule) {
        super(CustomItem.class);
        this.customModelDataModule = customModelDataModule;
    }

    @Override
    public void register(NamespacedKey key, CustomItem customItem) {
        if (customItem instanceof CustomItemImpl modeledItem
                && !this.customModelDataModule.getKeys().contains(modeledItem.getModel().identifier())) {
            this.customModelDataModule.register(modeledItem.getModel().identifier(), modeledItem.getModel());
        }
        super.register(key, customItem);
    }

    @Override
    public void load(@NotNull CinnamonResources resources) throws IOException {
        Path itemsFolder = resources.getItemsFolder();
        if (!Files.exists(itemsFolder)) return;
        try (var walker = Files.walk(itemsFolder)) {
            List<Path> files = walker.filter(Files::isRegularFile).toList();
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(CustomItem.class, new CItemDeserializer(resources.getPlugin()))
                    .create();
            int wasLoaded = this.map.size();
            files.stream()
                    .map(file -> {
                        try (var in = new InputStreamReader(Files.newInputStream(file))) {
                            return gson.fromJson(in, CustomItem.class);
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    })
                    .forEach(item -> this.register(item.getKey(), item));
            int loaded = this.map.size() - wasLoaded;
            log.info("[%s] Loaded a total of %d item(s)".formatted(this.clazz.getSimpleName(), loaded));
        }
    }

    @Override
    public boolean isCustom(ItemStack itemStack) {
        return CustomItemImpl.isCustom(itemStack);
    }

    @Override
    public CustomItem get(ItemStack itemStack) {
        return CustomItemImpl.of(itemStack).orElse(null);
    }
}
