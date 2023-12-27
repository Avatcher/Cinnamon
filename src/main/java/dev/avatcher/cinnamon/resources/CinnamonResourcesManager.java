package dev.avatcher.cinnamon.resources;

import dev.avatcher.cinnamon.Cinnamon;
import dev.avatcher.cinnamon.config.CinnamonConfig;
import dev.avatcher.cinnamon.exceptions.CinnamonRuntimeException;
import dev.avatcher.cinnamon.item.CItem;
import dev.avatcher.cinnamon.resources.exceptions.CinnamonResourcesLoadException;
import lombok.Getter;
import org.bukkit.NamespacedKey;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

public class CinnamonResourcesManager {
    private final Logger log;

    @Getter
    private final Map<NamespacedKey, CustomModelData> customModelDataMap;
    @Getter
    private final Map<NamespacedKey, CItem> customItemMap;

    public CinnamonResourcesManager() {
        this.log = Cinnamon.getInstance().getLogger();
        this.customModelDataMap = new HashMap<>();
        this.customItemMap = new HashMap<>();
    }

    public Optional<CustomModelData> getCustomModelData(NamespacedKey identifier) {
        return Optional.ofNullable(this.customModelDataMap.get(identifier));
    }

    public Optional<CItem> getCItem(NamespacedKey identifier) {
        return Optional.ofNullable(this.customItemMap.get(identifier));
    }

    public void registerCItem(CItem cItem) {
        this.customItemMap.put(cItem.getIdentifier(), cItem);
        log.info("Registered item " + cItem.getIdentifier());
    }

    public void load(CinnamonResources resources) {
        boolean successful = false;
        try (var loader = new CinnamonResourcesLoader(resources)) {
            CinnamonConfig config = loader.loadConfig();
            loader.setConfig(config);

            List<CItem> items = loader.loadItems();
            items.forEach(this::registerCItem);
            log.info("Loaded a total of " + items.size() + " items for plugin '" + resources.getPlugin().getName() + "'");
        } catch (CinnamonResourcesLoadException e) {
            e.log(this.log);
            log.severe("Failed to load resources '" + resources + "' for plugin '" + resources.getPlugin().getName() + "'");
        } catch (IOException e) {
            throw new CinnamonRuntimeException(e);
        }
    }
}
