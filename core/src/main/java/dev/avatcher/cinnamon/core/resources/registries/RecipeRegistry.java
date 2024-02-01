package dev.avatcher.cinnamon.core.resources.registries;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.avatcher.cinnamon.core.json.ItemStackDeserializer;
import dev.avatcher.cinnamon.core.json.RecipeDeserializer;
import dev.avatcher.cinnamon.core.json.value.Value;
import dev.avatcher.cinnamon.core.json.value.ValueProvider;
import dev.avatcher.cinnamon.core.recipes.CustomRecipe;
import dev.avatcher.cinnamon.core.resources.CinnamonRegistry;
import dev.avatcher.cinnamon.core.resources.CinnamonResources;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * A Cinnamon Module storing custom recipes
 *
 * @see CinnamonRegistry
 */
public class RecipeRegistry extends AbstractCinnamonRegistry<CustomRecipe> {
    /**
     * Creates a new custom recipes module.
     */
    public RecipeRegistry() {
        super(CustomRecipe.class);
    }

    @Override
    public void register(NamespacedKey key, CustomRecipe value) {
        super.register(key, value);
        Bukkit.addRecipe(value.getRecipe());
    }

    @Override
    public void load(@NotNull CinnamonResources resources) throws IOException {
        Path recipesFolder = resources.getRecipesFolder();
        if (!Files.exists(recipesFolder)) return;
        try (var walker = Files.walk(recipesFolder)) {
            ValueProvider<NamespacedKey> identifierProvider = new ValueProvider<>();
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Value.class, identifierProvider)
                    .registerTypeAdapter(ItemStack.class, new ItemStackDeserializer())
                    .registerTypeAdapter(Recipe.class, new RecipeDeserializer())
                    .create();
            int wasLoaded = this.map.size();
            walker.filter(Files::isRegularFile)
                    .map(recipePath -> {
                        String relativeRecipePath = recipesFolder.relativize(recipePath).toString();
                        String recipeName = relativeRecipePath.substring(0, relativeRecipePath.indexOf(".json"));
                        NamespacedKey recipeIdentifier = new NamespacedKey(resources.getPlugin(), recipeName);
                        identifierProvider.setValue(recipeIdentifier);
                        try {
                            return new CustomRecipe(
                                    recipeIdentifier,
                                    gson.fromJson(Files.readString(recipePath), Recipe.class));
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    })
                    .forEach(recipe -> this.register(recipe.getKey(), recipe));
            int loaded = this.map.size() - wasLoaded;
            log.info("[%s] Loaded a total of %d recipe(s)".formatted(this.clazz.getSimpleName(), loaded));
        }
    }
}
