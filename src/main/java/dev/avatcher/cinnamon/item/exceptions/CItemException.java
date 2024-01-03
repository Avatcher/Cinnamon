package dev.avatcher.cinnamon.item.exceptions;

/**
 * Exception related to {@link dev.avatcher.cinnamon.item.CItem}
 */
public class CItemException extends RuntimeException {
    public CItemException(String message) {
        super(message);
    }

    public CItemException(Throwable e) {
        super(e);
    }
}
