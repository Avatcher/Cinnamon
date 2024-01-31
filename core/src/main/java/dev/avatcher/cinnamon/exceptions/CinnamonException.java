package dev.avatcher.cinnamon.exceptions;

/**
 * Exception made by Cinnamon
 */
public class CinnamonException extends Exception {
    /**
     * Wraps an existing exception into an
     * exception produced by Cinnamon.
     *
     * @param message Error message
     * @param e The exception to be wrapped
     */
    public CinnamonException(String message, Throwable e) {
        super(message, e);
    }
}
