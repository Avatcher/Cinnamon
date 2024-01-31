package dev.avatcher.cinnamon.core.resources.modules;

import dev.avatcher.cinnamon.core.Cinnamon;
import dev.avatcher.cinnamon.core.resources.CinnamonModule;
import org.bukkit.NamespacedKey;

import java.util.*;
import java.util.logging.Logger;

/**
 * Abstract implementation of {@link CinnamonModule}
 *
 * @param <T> Type of the resources in the storage
 */
public abstract class AbstractCinnamonModule<T> implements CinnamonModule<T> {
    /**
     * Map where the resources are stored
     */
    protected final Map<NamespacedKey, T> map;
    /**
     * Class of the resources
     */
    protected final Class<T> clazz;

    protected final Logger log;

    /**
     * Initializes CinnamonModule for a certain resources type.
     *
     * @param clazz The type of the resources
     */
    public AbstractCinnamonModule(Class<T> clazz) {
        this.map = new HashMap<>();
        this.clazz = clazz;
        this.log = Cinnamon.getInstance().getLogger();
    }

    @Override
    public Optional<T> get(NamespacedKey key) {
        return Optional.ofNullable(this.map.get(key));
    }

    @Override
    public void register(NamespacedKey key, T value) {
        if (this.map.containsKey(key)) {
            log.warning("[%s] Overrode: %s".formatted(this.clazz.getSimpleName(), key));
        } else {
            log.info("[%s] Registered: %s".formatted(this.clazz.getSimpleName(), key));
        }
        this.map.put(key, value);
    }

    @Override
    public Set<NamespacedKey> getKeys() {
        return this.map.keySet();
    }

    @Override
    public Collection<T> getValues() {
        return this.map.values();
    }
}
