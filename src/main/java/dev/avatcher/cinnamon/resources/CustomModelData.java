package dev.avatcher.cinnamon.resources;

import dev.avatcher.cinnamon.Cinnamon;
import org.bukkit.NamespacedKey;

import java.util.Optional;

/**
 * Representation of Minecraft's item tag
 * 'CustomModelData' that changes item's model
 *
 * @param identifier Identifier of the model
 * @param numeric    Value of CustomModelData tag used in game
 */
public record CustomModelData(NamespacedKey identifier, int numeric) {
    /**
     * Starting value of CustomModelData Cinnamon registers
     */
    public static final int START_NUMERIC = 39000;

    public CustomModelData(NamespacedKey identifier, int numeric) {
        this.identifier = identifier;
        if (!isCorrectNumber(numeric)) {
            throw new IllegalArgumentException("CustomModelData's numeric id '" + numeric + "' is invalid.");
        }
        this.numeric = numeric;
    }

    /**
     * Checks, if CustomModelData value can be applied in game.
     *
     * @param numeric CustomModelData value ({@link CustomModelData#numeric})
     * @return {@code true}, if {@code numeric} is applicable in game
     */
    public static boolean isCorrectNumber(int numeric) {
        return String.valueOf(numeric).length() < 6;
    }

    /**
     * Returns {@link CustomModelData} with the certain {@link CustomModelData#identifier}.
     * Empty optional will be returned, if CustomModelData was not found.
     *
     * @param identifier Item's identifier
     * @return Optional {@link CustomModelData} (Empty, if CustomModelData was not found)
     */
    public static Optional<CustomModelData> of(NamespacedKey identifier) {
        return Cinnamon.getInstance().getResourcesManager().getCustomModelData(identifier);
    }
}
