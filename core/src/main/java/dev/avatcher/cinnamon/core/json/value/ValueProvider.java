package dev.avatcher.cinnamon.core.json.value;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.lang.reflect.Type;

/**
 * A JSON "deserializer" that passes some special data
 * during the JSON deserialization process
 *
 * @param <T> The type of special data
 */
@Getter
@NoArgsConstructor
public class ValueProvider <T> implements JsonDeserializer<Value<T>> {
    private Value<T> value;

    @Override
    public Value<T> deserialize(
            JsonElement jsonElement,
            Type type,
            JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return value;
    }

    /**
     * Sets the value of the provider's data.
     *
     * @param value New value
     */
    public void setValue(T value) {
        this.value = new Value<>(value);
    }
}
