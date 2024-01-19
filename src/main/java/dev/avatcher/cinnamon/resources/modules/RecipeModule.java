package dev.avatcher.cinnamon.resources.modules;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.avatcher.cinnamon.json.ItemStackDeserializer;
import dev.avatcher.cinnamon.json.RecipeDeserializer;
import dev.avatcher.cinnamon.json.value.Value;
import dev.avatcher.cinnamon.json.value.ValueProvider;
import dev.avatcher.cinnamon.resources.CinnamonResources;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 * A Cinnamon Module storing custom recipes
 *
 * @see dev.avatcher.cinnamon.resources.CinnamonModule
 */
public class RecipeModule extends AbstractCinnamonModule<Recipe> {
    /**
     * Creates a new custom recipes module.
     */
    public RecipeModule() {
        super(Recipe.class);
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
                            return Map.entry(
                                    recipeIdentifier,
                                    gson.fromJson(Files.readString(recipePath), Recipe.class));
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    })
                    .forEach(recipe -> this.register(recipe.getKey(), recipe.getValue()));
            int loaded = this.map.size() - wasLoaded;
            log.info("[%s] Loaded a total of %d recipe(s)".formatted(this.clazz.getSimpleName(), loaded));
        }
    }
}
