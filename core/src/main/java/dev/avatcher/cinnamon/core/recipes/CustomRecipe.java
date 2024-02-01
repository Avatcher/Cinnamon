package dev.avatcher.cinnamon.core.recipes;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;

@Getter
@AllArgsConstructor
public class CustomRecipe implements Keyed {
    private final NamespacedKey key;
    private final Recipe recipe;
}
