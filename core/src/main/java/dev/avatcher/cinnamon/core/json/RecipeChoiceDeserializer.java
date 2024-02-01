package dev.avatcher.cinnamon.core.json;

import com.google.gson.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

import java.lang.reflect.Type;

/**
 * JSON deserializer for {@link RecipeChoice}
 */
public class RecipeChoiceDeserializer implements JsonDeserializer<RecipeChoice> {
    @Override
    public RecipeChoice deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jObject = jsonElement.getAsJsonObject();

        ItemStack itemStack = jsonDeserializationContext.deserialize(jObject, ItemStack.class);
        return new RecipeChoice.ExactChoice(itemStack);
    }
}
