package dev.avatcher.cinnamon.resources.exceptions;

import dev.avatcher.cinnamon.exceptions.CinnamonException;
import dev.avatcher.cinnamon.resources.CinnamonResources;
import lombok.Getter;

import java.util.logging.Logger;

public class CinnamonResourcesLoadException extends CinnamonException {
    @Getter
    private final CinnamonResources resources;

    public CinnamonResourcesLoadException(CinnamonResources resources, String message, Throwable e) {
        super(message, e);
        this.resources = resources;
    }

    public void log(Logger log) {
        log.severe(this.getMessage());
        log.severe(this.getCause().getClass() + ": " + this.getCause().getMessage());
    }
}
