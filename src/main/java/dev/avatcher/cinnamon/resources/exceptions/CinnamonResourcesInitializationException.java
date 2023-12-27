package dev.avatcher.cinnamon.resources.exceptions;

import dev.avatcher.cinnamon.resources.CinnamonResources;

public class CinnamonResourcesInitializationException extends CinnamonResourcesLoadException {
    public CinnamonResourcesInitializationException(CinnamonResources resources, Throwable e) {
        super(resources,
                String.format("Failed to initialize ResourceLoader for resource '%s'", resources.toString()),
                e
        );
    }
}
