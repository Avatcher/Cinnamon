package dev.avatcher.cinnamon.item.exceptions;

public class CItemException extends RuntimeException {
    public CItemException(String message) {
        super(message);
    }

    public CItemException(Throwable e) {
        super(e);
    }
}
