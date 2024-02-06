package dev.avatcher.cinnamon.core.exceptions;

/**
 * Runtime exception made by Cinnamon
 */
public class CinnamonRuntimeException extends RuntimeException {
    /**
     * Wraps and existing exception into a
     * runtime exception produced by Cinnamon.
     *
     * @param e Exception to be wrapped
     */
    public CinnamonRuntimeException(Throwable e) {
        super(e);
    }
}
