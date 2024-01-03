package dev.avatcher.cinnamon.resources;

import dev.avatcher.cinnamon.Cinnamon;
import lombok.Getter;
import org.bukkit.NamespacedKey;

import java.util.Optional;


public record CustomModelData(NamespacedKey identifier, int numeric) {
    public static final int START_NUMERIC = 39000;

    public CustomModelData(NamespacedKey identifier, int numeric) {
        this.identifier = identifier;
        if (!isCorrectNumber(numeric)) {
            throw new IllegalArgumentException("CustomModelData's numeric id '" + numeric + "' is invalid.");
        }
        this.numeric = numeric;
    }

    public static boolean isCorrectNumber(int number) {
        return String.valueOf(number).length() < 6;
    }

    public static Optional<CustomModelData> of(NamespacedKey identifier) {
        return Cinnamon.getInstance().getResourcesManager().getCustomModel(identifier);
    }
}
