package dev.avatcher.cinnamon.core.json;

import com.google.gson.*;
import dev.avatcher.cinnamon.core.block.NoteblockCustomBlock;
import dev.avatcher.cinnamon.core.resources.CinnamonResourcesManager;
import dev.avatcher.cinnamon.core.resources.registries.CustomBlocksRegistryImpl;
import lombok.AllArgsConstructor;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Type;

/**
 * JSON deserializer of {@link NoteblockCustomBlock} request for registration
 * in {@link CinnamonResourcesManager}
 */
@AllArgsConstructor
public class CBlockDeserializer implements JsonDeserializer<CustomBlocksRegistryImpl.BlockRegistrationRequest> {
    private final Plugin plugin;

    @Override
    public CustomBlocksRegistryImpl.BlockRegistrationRequest deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jObject = jsonElement.getAsJsonObject();
        NamespacedKey identifier = new NamespacedKey(this.plugin, jObject.get("identifier").getAsString());
        NamespacedKey model = NamespacedKey.fromString(jObject.get("model").getAsString());
        return CustomBlocksRegistryImpl.BlockRegistrationRequest.builder()
                .identifier(identifier)
                .model(model)
                .build();
    }
}
