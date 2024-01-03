package dev.avatcher.cinnamon.resources.exceptions;

import dev.avatcher.cinnamon.resources.CinnamonResources;

/**
 * Exception for when Cinnamon resources' configuration loading fails
 */
public class CinnamonConfigLoadException extends CinnamonResourcesLoadException {
    public CinnamonConfigLoadException(CinnamonResources resources, Throwable e) {
        super(resources, "Failed to load " + CinnamonResources.CONFIG_FILE + " from " + resources.toString(), e);
    }
}
