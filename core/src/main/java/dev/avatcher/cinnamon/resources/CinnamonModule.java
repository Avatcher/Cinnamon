package dev.avatcher.cinnamon.resources;

import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

/**
 * A storage of Cinnamon-loadable keyed resources
 *
 * @param <T> The type of the resources in the storage
 */
public interface CinnamonModule<T> {
    /**
     * Gets resources by its key, or else an empty
     * {@link Optional}, if the data was not found.
     *
     * @param key Key of the resources
     * @return Empty {@link Optional}, if data is not found
     */
    Optional<T> get(NamespacedKey key);

    /**
     * Registers resources.
     *
     * @param key Key of the data
     * @param value Resources to be registered
     */
    void register(NamespacedKey key, T value);

    /**
     * Gets all the keys of registered resources.
     *
     * @return A set of all the valid keys
     */
    Set<NamespacedKey> getKeys();

    /**
     * Gets all the resources in the storage.
     *
     * @return All the resources
     */
    Collection<T> getValues();

    /**
     * Loads resources from {@link CinnamonResources}.
     *
     * @param resources Cinnamon resources
     */
    void load(@NotNull CinnamonResources resources) throws IOException;
}
