package dev.avatcher.cinnamon.resources.exceptions;

import dev.avatcher.cinnamon.exceptions.CinnamonException;
import dev.avatcher.cinnamon.resources.CinnamonResources;
import lombok.Getter;

@Getter
public class CinnamonResourcesLoadException extends CinnamonException {
    private final CinnamonResources resources;

    public CinnamonResourcesLoadException(CinnamonResources resources, String message, Throwable e) {
        super(message, e);
        this.resources = resources;
    }
}
