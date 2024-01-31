package dev.avatcher.cinnamon.core.json;

import com.google.common.base.Preconditions;
import com.google.gson.*;
import dev.avatcher.cinnamon.core.Cinnamon;
import dev.avatcher.cinnamon.core.json.recipes.ShapedRecipeDeserializer;
import dev.avatcher.cinnamon.core.json.recipes.ShapelessRecipeDeserializer;
import dev.avatcher.cinnamon.core.json.value.Value;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

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

    /**
     * JSON field name responsible for storing
     * the recipe's type, as defined in {@link #DEFAULT_RECIPE_DESERIALIZERS}
     * keys
     */
    public static final String TYPE_FIELD = "type";

    /**
     * JSON field name responsible for storing the
     * result of the recipe
     */
    public static final String RESULT_FIELD = "result";

    private final Logger log;

    /**
     * Deserializers of each individual recipe type
     */
    private final Map<String, JsonDeserializer<? extends Recipe>> deserializers;

    /**
     * Creates a new JSON deserializer for
     * minecraft recipes.
     *
     * @param deserializers Deserializers of different
     *                      recipes types
     */
    public RecipeDeserializer(Map<String, JsonDeserializer<? extends Recipe>> deserializers) {
        this.log = Cinnamon.getInstance().getLogger();
        this.deserializers = deserializers;
    }

    /**
     * Creates a new JSON deserializer for
     * minecraft recipes using the default
     * recipe deserializers
     *
     * @see #DEFAULT_RECIPE_DESERIALIZERS
     */
    public RecipeDeserializer() {
        this(DEFAULT_RECIPE_DESERIALIZERS);
    }

    /**
     * Formats an error message for when a
     * certain JSON field is not found.
     *
     * @param recipeIdentifier Identifier of the recipe
     * @param jsonField The name of the missing JSON field
     * @return Formatted error message
     */
    @Contract(pure = true)
    public static @NotNull String missingField(NamespacedKey recipeIdentifier, String jsonField) {
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
        return this.deserializers.get(recipeType).deserialize(jsonElement, type, jsonDeserializationContext);
    }
}
