package dev.avatcher.cinnamon.item;

import dev.avatcher.cinnamon.Cinnamon;
import dev.avatcher.cinnamon.item.behaviour.DefaultItemBehaviour;
import dev.avatcher.cinnamon.item.exceptions.CItemException;
import dev.avatcher.cinnamon.resources.CustomModelData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;


/**
 * Cinnamon custom item
 */
@AllArgsConstructor
public class CItem {
    /**
     * Item used as bare material of the most of the custom items
     */
    public static final Material MATERIAL = Material.RABBIT_HIDE;

    /**
     * {@link NamespacedKey} for accessing custom item identifier
     * inside of {@link ItemStack}'s {@link org.bukkit.persistence.PersistentDataContainer}
     */
    public static final NamespacedKey IDENTIFIER_KEY = new NamespacedKey(Cinnamon.getInstance(), "identifier");

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
    @Getter
    private final NamespacedKey identifier;

    /**
     * {@link CustomModelData} that gives item its unique model.
     */
    @Getter
    private final CustomModelData model;

    /**
     * Item's in-game name
     */
    @Getter
    private final Component name;

    /**
     * Class that describes item's behaviour.
     * Examples:
     * <ul>
     *     <li>{@link DefaultItemBehaviour} describes behaviour without any effects</li>
     *     <li>{@link dev.avatcher.cinnamon.item.behaviour.BlockItemBehaviour} describes
     *     block item behaviour, allowing to place it with right click.
     *     </li>
     * </ul>
     */
    private Class<? extends CItemBehaviour> behaviour = DefaultItemBehaviour.class;

    private transient Constructor<? extends CItemBehaviour> behaviourConstructor;

    public CItem(NamespacedKey identifier, CustomModelData model, Component name, Class<? extends CItemBehaviour> behaviour) {
        this.identifier = identifier;
        this.model = model;
        this.name = name;
        this.setBehaviour(behaviour);
    }

    public CItem() {
        this(
                new NamespacedKey(Cinnamon.getInstance(), "unknown"),
                new CustomModelData(new NamespacedKey(Cinnamon.getInstance(), "unknown"), 86000),
                Component.text("Unknown"),
                DefaultItemBehaviour.class
        );
    }

    @Override
    public String toString() {
        return this.getIdentifier().asString();
    }

    /**
     * Constructs {@link ItemStack} of this custom item.
     * To alter item's creating process, implement
     * {@link CItemBehaviour#onCreate(ItemStack)} method
     * in item's behaviour.
     *
     * @return {@link ItemStack}
     */
    public ItemStack getItemStack() {
        var item = new ItemStack(MATERIAL);
        item.editMeta(meta -> {
            meta.displayName(this.name);
            meta.setCustomModelData(model.numeric());
            meta.getPersistentDataContainer().set(
                    IDENTIFIER_KEY,
                    PersistentDataType.STRING,
                    this.getIdentifier().asString()
            );
        });
        CItemBehaviour cBehaviour = this.createBehaviour();
        return cBehaviour.onCreate(item);
    }

    public void setBehaviour(Class<? extends CItemBehaviour> behaviour) {
        if (behaviour == null) {
            this.behaviourConstructor = null;
            return;
        }
        if (!CItemBehaviour.class.isAssignableFrom(behaviour)) {
            throw new CItemException("Custom behaviour class '" + behaviour.getName()
                    + "' does not extend '" + CItemBehaviour.class.getName() + "'");
        }
        try {
            Constructor<? extends CItemBehaviour> constructor = behaviour.getConstructor();
            constructor.setAccessible(true);
            this.behaviour = behaviour;
            this.behaviourConstructor = constructor;
        } catch (NoSuchMethodException e) {
            throw new CItemException("Cannot find behaviour constructor " + behaviour.getName()
                    + "(" + CItem.class.getName() + ")");
        }
    }

    public CItemBehaviour createBehaviour() {
        try {
            return this.behaviourConstructor.newInstance();
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new CItemException(e);
        }
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
        return CItem.of(NamespacedKey.fromString(identifier));
    }
}
