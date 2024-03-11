package dev.avatcher.cinnamon.core.json.value;

/**
 * Wrapper of a special value that can be
 * passed during JSON deserialization
 *
 * @param <T> The type of special data
 * @param value Value that needs to be wrapped
 *
 * @see ValueProvider
 */
public record Value<T>(T value) {
}
