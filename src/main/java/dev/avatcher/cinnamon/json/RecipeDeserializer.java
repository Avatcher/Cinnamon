package dev.avatcher.cinnamon.json;

import com.google.common.base.Preconditions;
import com.google.gson.*;
import dev.avatcher.cinnamon.Cinnamon;
import dev.avatcher.cinnamon.item.CItem;
import dev.avatcher.cinnamon.json.recipes.ShapedRecipeDeserializer;
import dev.avatcher.cinnamon.json.recipes.ShapelessRecipeDeserializer;
import dev.avatcher.cinnamon.json.value.Value;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.logging.Logger;

/**
 * JSON deserializer for Minecraft recipes
 *
 * @see Recipe
 */
public class RecipeDeserializer implements JsonDeserializer<Recipe> {
    /**
     * Default JSON deserializers for different recipe types
     */
    public static final Map<String, JsonDeserializer<? extends Recipe>> DEFAULT_RECIPE_DESERIALIZERS = Map.of(
            "crafting_shaped", new ShapedRecipeDeserializer(),
            "crafting_shapeless", new ShapelessRecipeDeserializer()
    );

    public static final String TYPE_FIELD = "type";
    public static final String RESULT_FIELD = "result";

    private final Logger log;

    /**
     * Deserializers of each individual recipe type
     */
    private final Map<String, JsonDeserializer<? extends Recipe>> deserializers;


    public RecipeDeserializer(Map<String, JsonDeserializer<? extends Recipe>> deserializers) {
        this.log = Cinnamon.getInstance().getLogger();
        this.deserializers = deserializers;
    }

    public RecipeDeserializer() {
        this(DEFAULT_RECIPE_DESERIALIZERS);
    }

    public static String missingField(NamespacedKey recipeIdentifier, String jsonField) {
        return "Missing JSON field '" + jsonField + "' in recipe " + recipeIdentifier;
    }

    @Override
    public Recipe deserialize(
            JsonElement jsonElement,
            Type type,
            JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jObject = jsonElement.getAsJsonObject();
        NamespacedKey recipeIdentifier = jsonDeserializationContext
                .<Value<NamespacedKey>>deserialize(jsonElement, Value.class).value();

        Preconditions.checkNotNull(jObject.get(TYPE_FIELD), missingField(recipeIdentifier, TYPE_FIELD));
        Preconditions.checkNotNull(jObject.get(RESULT_FIELD), missingField(recipeIdentifier, RESULT_FIELD));

        String recipeType = jObject.get("type").getAsString();
        if (recipeType == null || !this.deserializers.containsKey(recipeType)) {
            log.warning("Unknown recipe type '" + recipeType + "' in recipe " + recipeIdentifier);
            return null;
        }
        Recipe recipe = this.deserializers.get(recipeType).deserialize(jsonElement, type, jsonDeserializationContext);
        if (!CItem.isCustom(recipe.getResult())) {
            log.warning("Vanilla item being a result of Cinnamon recipe is not allowed: " + recipeIdentifier);
            return null;
        }
        return recipe;
    }
}
