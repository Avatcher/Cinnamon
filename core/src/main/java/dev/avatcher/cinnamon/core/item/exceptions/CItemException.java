package dev.avatcher.cinnamon.core.item.exceptions;

import dev.avatcher.cinnamon.core.item.CItem;

/**
 * Exception related to {@link CItem}
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
