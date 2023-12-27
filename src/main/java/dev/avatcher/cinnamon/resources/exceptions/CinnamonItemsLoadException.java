package dev.avatcher.cinnamon.resources.exceptions;

import dev.avatcher.cinnamon.resources.CinnamonResources;

public class CinnamonItemsLoadException extends CinnamonResourcesLoadException {
    public CinnamonItemsLoadException(CinnamonResources resources, Throwable e) {
        super(resources,
                String.format("Failed to load an item from resource '%s'", resources.toString()),
                e
        );
    }
}
