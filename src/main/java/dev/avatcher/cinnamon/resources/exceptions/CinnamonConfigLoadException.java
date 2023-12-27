package dev.avatcher.cinnamon.resources.exceptions;

import dev.avatcher.cinnamon.resources.CinnamonResources;
import dev.avatcher.cinnamon.resources.CinnamonResourcesLoader;

public class CinnamonConfigLoadException extends CinnamonResourcesLoadException {
    public CinnamonConfigLoadException(CinnamonResources resources, Throwable e) {
        super(resources,
                String.format("Failed to load %s from resource '%s'", CinnamonResourcesLoader.CONFIG_FILE, resources.toString()),
                e
        );
    }
}
