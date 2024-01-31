package dev.avatcher.cinnamon.core.json;

import com.google.gson.*;
import dev.avatcher.cinnamon.core.block.CBlock;
import dev.avatcher.cinnamon.core.resources.CinnamonResourcesManager;
import dev.avatcher.cinnamon.core.resources.modules.CBlockModule;
import lombok.AllArgsConstructor;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Type;

/**
 * JSON deserializer of {@link CBlock} request for registration
 * in {@link CinnamonResourcesManager}
 */
@AllArgsConstructor
public class CBlockDeserializer implements JsonDeserializer<CBlockModule.BlockRegistrationRequest> {
    private final Plugin plugin;

    @Override
    public CBlockModule.BlockRegistrationRequest deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jObject = jsonElement.getAsJsonObject();
        NamespacedKey identifier = new NamespacedKey(this.plugin, jObject.get("identifier").getAsString());
        NamespacedKey model = NamespacedKey.fromString(jObject.get("model").getAsString());
        return CBlockModule.BlockRegistrationRequest.builder()
                .identifier(identifier)
                .model(model)
                .build();
    }
}
