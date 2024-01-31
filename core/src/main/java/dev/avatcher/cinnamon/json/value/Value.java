package dev.avatcher.cinnamon.json.value;

/**
 * Special value that can be passed during JSON deserialization
 *
 * @param <T> The type of special data
 *
 * @see ValueProvider
 */
public record Value<T>(T value) {
}
