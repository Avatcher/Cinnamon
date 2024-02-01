package dev.avatcher.cinnamon.core.resources;

import dev.avatcher.cinnamon.core.CinnamonPlugin;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Representation of Minecraft's item tag
 * 'CustomModelData' that changes item's model
 *
 * @param identifier Identifier of the model
 * @param numeric    Value of CustomModelData tag used in game
 */
public record CustomModelData(NamespacedKey identifier, int numeric) implements Keyed {
    /**
     * Starting value of CustomModelData Cinnamon registers
     */
    public static final int START_NUMERIC = 39000;

    /**
     * Items that use `minecraft:item/handheld` as the
     * base of their minecraft model
     */
    public static final List<Material> HANDHELD_ITEMS;

    static {
        List<Material> handheldItems = List.of(
                // Bows
                Material.BOW,
                Material.CROSSBOW,

                // Fishing rods
                Material.FISHING_ROD,
                Material.CARROT_ON_A_STICK,
                Material.WARPED_FUNGUS_ON_A_STICK,

                // Sticks
                Material.STICK,
                Material.BLAZE_ROD
        );

        List<String> toolMaterials = List.of("WOODEN", "STONE", "IRON", "GOLDEN", "DIAMOND", "NETHERITE");
        List<String> toolNames     = List.of("SWORD", "PICKAXE", "AXE", "SHOVEL", "HOE");
        List<Material> tools = toolMaterials.stream()
                .flatMap(material -> toolNames.stream()
                        .map(tool -> material + "_" + tool)
                        .map(Material::getMaterial)
                )
                .toList();

        HANDHELD_ITEMS = Stream.of(handheldItems, tools)
                .flatMap(Collection::stream)
                .toList();

        HANDHELD_ITEMS.forEach(System.out::println);
    }

    /**
     * Creates a new CustomModelData with
     * given identifier and numeric id.
     *
     * @param identifier Identifier of the model
     * @param numeric Numeric id of the model used in-game
     */
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
        return CinnamonPlugin.getInstance().getResourcesManager().getCustomModelData(identifier);
    }

    @Override
    public @NotNull NamespacedKey getKey() {
        return this.identifier();
    }
}
