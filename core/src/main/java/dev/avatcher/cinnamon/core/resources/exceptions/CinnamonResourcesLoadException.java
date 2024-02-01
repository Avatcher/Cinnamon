package dev.avatcher.cinnamon.core.resources.exceptions;

import dev.avatcher.cinnamon.core.exceptions.CinnamonException;
import dev.avatcher.cinnamon.core.resources.CinnamonResources;
import lombok.Getter;

/**
 * Exception for when Cinnamon resources' configuration loading fails
 */
@Getter
public class CinnamonResourcesLoadException extends CinnamonException {
    /**
     * Resources attempted to be loaded
     */
    private final CinnamonResources resources;

    /**
     * Wraps an existing exception into an exception
     * produced by Cinnamon during resources loading.
     *
     * @param resources Resources attempted to be loaded
     * @param message Error message
     * @param e The exception thrown during resources loading
     */
    public CinnamonResourcesLoadException(CinnamonResources resources, String message, Throwable e) {
        super(message, e);
        this.resources = resources;
    }
}
