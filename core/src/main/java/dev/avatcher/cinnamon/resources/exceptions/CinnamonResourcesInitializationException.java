package dev.avatcher.cinnamon.resources.exceptions;

import dev.avatcher.cinnamon.resources.CinnamonResources;

/**
 * Exception for when initialization of {@link CinnamonResources} fails
 */
public class CinnamonResourcesInitializationException extends CinnamonResourcesLoadException {
    /**
     * Wraps an existing exception into an exception
     * produced by Cinnamon during resources initialization.
     *
     * @param resources Resources attempted to be initialized
     * @param e The exception thrown during resources initialization
     */
    public CinnamonResourcesInitializationException(CinnamonResources resources, Throwable e) {
        super(resources, "Failed to initialize ResourceLoader for resource '" + resources.toString() + "'", e);
    }
}
