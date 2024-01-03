package dev.avatcher.cinnamon.resources.exceptions;

import dev.avatcher.cinnamon.exceptions.CinnamonException;
import dev.avatcher.cinnamon.resources.CinnamonResources;
import lombok.Getter;

/**
 * Exception for when Cinnamon resources loading fails
 */
@Getter
public class CinnamonResourcesLoadException extends CinnamonException {
    /**
     * Resources failed to load
     */
    private final CinnamonResources resources;

    public CinnamonResourcesLoadException(CinnamonResources resources, String message, Throwable e) {
        super(message, e);
        this.resources = resources;
    }
}
