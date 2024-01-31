package dev.avatcher.cinnamon.core.json;

import com.google.gson.*;
import org.bukkit.NamespacedKey;

import java.lang.reflect.Type;

/**
 * JSON adapter for a {@link NamespacedKey}
 */
public class NamespacedKeyAdapter implements JsonDeserializer<NamespacedKey>, JsonSerializer<NamespacedKey> {
    @Override
    public NamespacedKey deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        String str = jsonElement.getAsString();
        return NamespacedKey.fromString(str);
    }

    @Override
    public JsonElement serialize(NamespacedKey namespacedKey, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(namespacedKey.asString());
    }
}
