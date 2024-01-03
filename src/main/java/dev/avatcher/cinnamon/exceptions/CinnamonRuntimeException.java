package dev.avatcher.cinnamon.exceptions;

/**
 * Runtime exception made by Cinnamon
 */
public class CinnamonRuntimeException extends RuntimeException {
    public CinnamonRuntimeException(String message) {
        super(message);
    }

    public CinnamonRuntimeException(Throwable e) {
        super(e);
    }
}
