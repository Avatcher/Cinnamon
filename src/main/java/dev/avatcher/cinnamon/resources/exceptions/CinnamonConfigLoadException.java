package dev.avatcher.cinnamon.resources.exceptions;

import dev.avatcher.cinnamon.resources.CinnamonResources;
import dev.avatcher.cinnamon.resources.CinnamonResourcesLoader;

public class CinnamonConfigLoadException extends CinnamonResourcesLoadException {
    public CinnamonConfigLoadException(CinnamonResources resources, Throwable e) {
        super(resources, "Failed to load " + CinnamonResourcesLoader.CONFIG_FILE + " from " + resources.toString(), e);
    }
}
