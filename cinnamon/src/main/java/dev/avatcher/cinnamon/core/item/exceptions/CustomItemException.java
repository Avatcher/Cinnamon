package dev.avatcher.cinnamon.core.item.exceptions;

/**
 * Exception related to {@link dev.avatcher.cinnamon.api.items.CustomItem}
 */
public class CustomItemException extends RuntimeException {
    /**
     * Creates a new exception with a certain message.
     *
     * @param message Error message
     */
    public CustomItemException(String message) {
        super(message);
    }
}
