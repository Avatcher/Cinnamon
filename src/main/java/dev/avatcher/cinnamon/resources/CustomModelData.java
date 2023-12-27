package dev.avatcher.cinnamon.resources;

import org.bukkit.NamespacedKey;

public record CustomModelData(NamespacedKey identifier, int numeric) {
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
}
