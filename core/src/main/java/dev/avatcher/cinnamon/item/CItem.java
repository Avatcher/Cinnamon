package dev.avatcher.cinnamon.item;

import dev.avatcher.cinnamon.Cinnamon;
import dev.avatcher.cinnamon.item.behaviour.DefaultItemBehaviour;
import dev.avatcher.cinnamon.item.behaviour.StructurePlacingItem;
import dev.avatcher.cinnamon.item.events.ItemCreateEvent;
import dev.avatcher.cinnamon.item.exceptions.CItemException;
import dev.avatcher.cinnamon.resources.CustomModelData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;


/**
 * Cinnamon custom item
 */
@Getter
@Builder
@AllArgsConstructor
public class CItem {
    /**
     * Item used as bare material of the most of the custom items
     */
    public static final Material DEFAULT_MATERIAL = Material.RABBIT_HIDE;

    /**
     * {@link NamespacedKey} for accessing custom item identifier
     * inside of {@link ItemStack}'s {@link PersistentDataContainer}
     */
    public static final NamespacedKey IDENTIFIER_KEY = new NamespacedKey(Cinnamon.getInstance(), "identifier");

    /**
     * {@link NamespacedKey} for {@link PersistentDataContainer} marking an {@link ItemStack} as a result
     * of some custom recipe. It is needed only for identification of Cinnamon recipes and is removed
     * when the item is crafted.
     * 
     * @see #markCustomRecipeResult(ItemStack)
     */
    @SuppressWarnings("SpellCheckingInspection")
    public static final NamespacedKey RECIPE_MARK_KEY = new NamespacedKey(Cinnamon.getInstance(), "reciperesult");

    /**
     * Identifier of the custom item, that follow default minecraft
     * item identifying conventions, where namespace is plugin's name
     * and key is item's name.
     * <p>
     * Examples:
     * <ul>
     *  <li>{@code minecraft:diamond}</li>
     *  <li>{@code example_plugin:excalibur}</li>
     *  <li>{@code geodic:amethyst_dust}</li>
     * </ul>
     */
    private final NamespacedKey identifier;

    /**
     * {@link CustomModelData} that gives item its unique model.
     */
    private final CustomModelData model;

    @Builder.Default
    private final Material material = CItem.DEFAULT_MATERIAL;

    /**
     * Item's in-game name
     */
    private final Component name;

    /**
     * Item's behaviour, handling responses to various events.
     * Examples:
     * <ul>
     *     <li>{@link DefaultItemBehaviour} describes behaviour without any effects.</li>
     *     <li>{@link StructurePlacingItem} describes
     *     block item behaviour, allowing to place it with right click.
     *     </li>
     * </ul>
     */
    @Builder.Default
    private ItemBehaviour behaviour = new DefaultItemBehaviour();

    @Override
    public String toString() {
        return this.getIdentifier().asString();
    }

    /**
     * Constructs {@link ItemStack} of this custom item.
     * To alter item's creating process, implement
     * {@link ItemBehaviour#onCreate(ItemCreateEvent)} method
     * in item's behaviour.
     *
     * @return {@link ItemStack}
     */
    public ItemStack getItemStack() {
        var item = new ItemStack(this.material);
        item.editMeta(meta -> {
            meta.displayName(this.name);
            meta.setCustomModelData(model.numeric());
            meta.getPersistentDataContainer().set(
                    IDENTIFIER_KEY,
                    PersistentDataType.STRING,
                    this.getIdentifier().asString()
            );
        });
        ItemCreateEvent event = ItemCreateEvent.builder()
                .itemStack(item)
                .build();
        this.getBehaviour().onCreate(event);
        return event.getItemStack();
    }

    /**
     * Sets the behaviour of the custom item
     *
     * @param behaviour Behaviour to be set
     */
    public void setBehaviour(@Nullable ItemBehaviour behaviour) {
        this.behaviour = behaviour == null
                ? new DefaultItemBehaviour()
                : behaviour;
    }

    /**
     * Sets the behaviour of the custom item
     * by behaviour's java class.
     *
     * @param clazz Class of the item behaviour
     *
     * @throws CItemException When behaviour's class
     *                        does not meet the requirements
     *                        of being an item behaviour
     */
    public void setBehaviour(Class<? extends ItemBehaviour> clazz) {
        if (clazz == null) {
            this.behaviour = new DefaultItemBehaviour();
            return;
        }
        if (!ItemBehaviour.class.isAssignableFrom(clazz)) {
            throw new CItemException("Custom behaviour class '" + clazz.getName()
                    + "' does not implement '" + ItemBehaviour.class.getName() + "'");
        }
        Optional<? extends Constructor<? extends ItemBehaviour>> constructor = this.findConstructor(clazz, CItem.class);
        Optional<? extends Constructor<? extends ItemBehaviour>> noArgsConstructor = this.findConstructor(clazz);
        try {
            if (constructor.isEmpty() && noArgsConstructor.isEmpty()) {
                throw new CItemException("Couldn't find passing behaviour constructor " + clazz.getName()
                        + " for item " + this.identifier);
            }
            this.behaviour = constructor.isPresent()
                    ? constructor.get().newInstance(this)
                    : noArgsConstructor.get().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Finds a certain constructor in a class.
     *
     * @param clazz Class to look for the constructor
     * @param parameterTypes Parameter types of the constructor
     * @return {@code Optional.empty()}, if constructor is not found
     * @param <T> Type of the class
     */
    private <T> Optional<Constructor<T>> findConstructor(Class<T> clazz, Class<?>... parameterTypes) {
        try {
            Constructor<T> constructor = clazz.getConstructor(parameterTypes);
            return Optional.of(constructor);
        } catch (NoSuchMethodException e) {
            return Optional.empty();
        }
    }

    /**
     * Marks item as a result of Cinnamon recipe.
     *
     * @param itemStack Item stack to be marked
     * @return Marked item stack
     *
     * @see #RECIPE_MARK_KEY
     */
    public static ItemStack markCustomRecipeResult(ItemStack itemStack) {
        itemStack.editMeta(meta -> {
            PersistentDataContainer persistentDataContainer = meta.getPersistentDataContainer();
            if (!persistentDataContainer.has(CItem.IDENTIFIER_KEY)) {
                persistentDataContainer.set(CItem.IDENTIFIER_KEY,
                        PersistentDataType.STRING, itemStack.getType().getKey().toString());
            }
            persistentDataContainer.set(RECIPE_MARK_KEY, PersistentDataType.BOOLEAN, true);
        });
        return itemStack;
    }

    /**
     * Checks, if provided {@link ItemStack} is of custom items.
     *
     * @param itemStack {@link ItemStack} to be checked.
     * @return {@code true}, if {@code itemStack} is custom.
     */
    public static boolean isCustom(ItemStack itemStack) {
        return itemStack != null
                && itemStack.hasItemMeta()
                && itemStack.getItemMeta().getPersistentDataContainer().has(IDENTIFIER_KEY);
    }

    /**
     * Returns {@link CItem} with the certain {@link CItem#identifier}.
     * Empty optional will be returned, if item was not found.
     *
     * @param identifier Item's identifier
     * @return Optional {@link CItem} (Empty, if item was not found)
     */
    public static Optional<CItem> of(NamespacedKey identifier) {
        return Cinnamon.getInstance().getResourcesManager().getCItem(identifier);
    }

    /**
     * Returns {@link CItem} of the certain {@link ItemStack}, if it
     * contains custom items, otherwise empty {@link Optional}.
     *
     * @param itemStack Custom item stack
     * @return Optional {@link CItem} (Empty, if was not find)
     */
    public static Optional<CItem> of(ItemStack itemStack) {
        if (!CItem.isCustom(itemStack)) return Optional.empty();
        String identifier = itemStack.getItemMeta().getPersistentDataContainer()
                .get(IDENTIFIER_KEY, PersistentDataType.STRING);
        assert identifier != null;
        return CItem.of(NamespacedKey.fromString(identifier));
    }
}
