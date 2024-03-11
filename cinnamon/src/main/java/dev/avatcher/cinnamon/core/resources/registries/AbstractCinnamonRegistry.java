package dev.avatcher.cinnamon.core.resources.registries;

import dev.avatcher.cinnamon.core.CinnamonPlugin;
import dev.avatcher.cinnamon.core.resources.CinnamonRegistry;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * Abstract implementation of {@link CinnamonRegistry}
 *
 * @param <T> Type of the resources in the storage
 */
public abstract class AbstractCinnamonRegistry<T extends Keyed> implements CinnamonRegistry<T> {
    /**
     * Map where the resources are stored
     */
    protected final Map<NamespacedKey, T> map;
    /**
     * Class of the resources
     */
    protected final Class<T> clazz;

    /**
     * Logger
     */
    protected final Logger log;

    /**
     * Initializes CinnamonModule for a certain resources type.
     *
     * @param clazz The type of the resources
     */
    public AbstractCinnamonRegistry(Class<T> clazz) {
        this.map = new HashMap<>();
        this.clazz = clazz;
        this.log = CinnamonPlugin.getInstance().getLogger();
    }

    public @Nullable T get(@NotNull NamespacedKey key) {
        return this.map.get(key);
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


    @Override
    public @NotNull Stream<T> stream() {
        return this.getValues().stream();
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return this.getValues().iterator();
    }
}
