package dev.avatcher.cinnamon.json.recipes;

import com.google.common.base.Preconditions;
import com.google.gson.*;
import dev.avatcher.cinnamon.item.CItem;
import dev.avatcher.cinnamon.json.RecipeDeserializer;
import dev.avatcher.cinnamon.json.value.Value;
import lombok.AllArgsConstructor;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * JSON deserializer for {@link ShapedRecipe}
 */
@AllArgsConstructor
public class ShapedRecipeDeserializer implements JsonDeserializer<ShapedRecipe> {
    /**
     * Json field name responsible for recipe's shape
     */
    public static final String SHAPE_FIELD = "shape";

    /**
     * Json field name responsible for recipe's ingredients
     * keys inside shape matrix
     */
    public static final String KEY_FIELD = "key";

    @Override
    public ShapedRecipe deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jObject = jsonElement.getAsJsonObject();
        NamespacedKey recipeIdentifier = jsonDeserializationContext
                .<Value<NamespacedKey>>deserialize(jsonElement, Value.class).value();

        Preconditions.checkNotNull(jObject.get(SHAPE_FIELD), RecipeDeserializer.missingField(recipeIdentifier, SHAPE_FIELD));
        Preconditions.checkNotNull(jObject.get(KEY_FIELD), RecipeDeserializer.missingField(recipeIdentifier, KEY_FIELD));

        List<JsonElement> jShape = jObject.get(SHAPE_FIELD).getAsJsonArray().asList();
        String[] shape = jShape.stream().map(JsonElement::getAsString).toList().toArray(new String[0]);

        JsonObject jKey = jObject.get("key").getAsJsonObject();
        Map<Character, ItemStack> ingredients = jKey.entrySet().stream()
                .map(entry -> Map.entry(
                        entry.getKey().charAt(0),
                        jsonDeserializationContext.<ItemStack>deserialize(entry.getValue(), ItemStack.class)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        JsonElement jResult = jObject.get("result").getAsJsonObject();
        ItemStack result = jsonDeserializationContext.deserialize(jResult, ItemStack.class);

        CraftingBookCategory category = jObject.has("category")
                ? CraftingBookCategory.valueOf(jObject.get("category").getAsString())
                : CraftingBookCategory.MISC;

        ShapedRecipe recipe = new ShapedRecipe(recipeIdentifier, CItem.markCustomRecipeResult(result));
        recipe.shape(shape);
        ingredients.forEach(recipe::setIngredient);
        recipe.setCategory(category);
        recipe.setGroup(recipeIdentifier.getNamespace());
        return recipe;
    }
}
