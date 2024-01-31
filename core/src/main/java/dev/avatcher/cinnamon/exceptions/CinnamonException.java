package dev.avatcher.cinnamon.exceptions;

/**
 * Exception made by Cinnamon
 */
public class CinnamonException extends Exception {
    public CinnamonException(String message, Throwable e) {
        super(message, e);
    }

    public CinnamonException(String message) {
        super(message);
    }

    public CinnamonException(Throwable e) {
        super(e);
    }
}
