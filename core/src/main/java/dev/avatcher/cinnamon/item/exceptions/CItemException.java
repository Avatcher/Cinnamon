package dev.avatcher.cinnamon.item.exceptions;

/**
 * Exception related to {@link dev.avatcher.cinnamon.item.CItem}
 */
public class CItemException extends RuntimeException {
    /**
     * Creates a new exception with a certain message.
     *
     * @param message Error message
     */
    public CItemException(String message) {
        super(message);
    }
}
