package dev.avatcher.cinnamon.core.json.recipes;

import com.google.common.base.Preconditions;
import com.google.gson.*;
import dev.avatcher.cinnamon.core.item.CItem;
import dev.avatcher.cinnamon.core.json.RecipeDeserializer;
import dev.avatcher.cinnamon.core.json.value.Value;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;

import java.lang.reflect.Type;
import java.util.List;

/**
 * JSON deserializer for {@link ShapelessRecipe}
 */
public class ShapelessRecipeDeserializer implements JsonDeserializer<ShapelessRecipe> {
    /**
     * JSON field containing recipe's ingredients
     */
    public static final String INGREDIENTS_FIELD = "ingredients";

    @Override
    public ShapelessRecipe deserialize(
            JsonElement jsonElement,
            Type type,
            JsonDeserializationContext context) throws JsonParseException {
        JsonObject jObject = jsonElement.getAsJsonObject();
        NamespacedKey recipeIdentifier = context
                .<Value<NamespacedKey>>deserialize(jsonElement, Value.class).value();

        Preconditions.checkNotNull(jObject.get(INGREDIENTS_FIELD), RecipeDeserializer.missingField(recipeIdentifier, INGREDIENTS_FIELD));

        List<ItemStack> ingredients = jObject.get("ingredients").getAsJsonArray().asList()
                .stream()
                .map(JsonElement::getAsJsonObject)
                .map(obj -> context.<ItemStack>deserialize(obj, ItemStack.class))
                .toList();
        ItemStack result = context.deserialize(jObject.get("result").getAsJsonObject(), ItemStack.class);

        ShapelessRecipe recipe = new ShapelessRecipe(recipeIdentifier, CItem.markCustomRecipeResult(result));
        ingredients.forEach(recipe::addIngredient);

        return recipe;
    }
}
