package dev.avatcher.cinnamon.core.json;

import com.google.gson.*;
import dev.avatcher.cinnamon.api.items.CustomItem;
import dev.avatcher.cinnamon.api.items.ItemBehaviour;
import dev.avatcher.cinnamon.core.CinnamonPlugin;
import dev.avatcher.cinnamon.core.item.CustomItemImpl;
import dev.avatcher.cinnamon.core.item.behaviour.DefaultItemBehaviour;
import dev.avatcher.cinnamon.core.resources.CinnamonResources;
import dev.avatcher.cinnamon.core.resources.CustomModelData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Type;
import java.util.logging.Logger;

/**
 * Deserializer of JSON {@link CustomItem} instance
 * inside {@link CinnamonResources}
 */
public class CItemDeserializer implements JsonDeserializer<CustomItem> {
    /**
     * Owner plugin of the item
     */
    private final Plugin plugin;

    private final Logger log;

    /**
     * Creates a new JSON deserializer for
     * custom items owned by certain plugin.
     *
     * @param plugin Plugin owning the items
     */
    public CItemDeserializer(Plugin plugin) {
        this.plugin = plugin;
        this.log = CinnamonPlugin.getInstance().getLogger();
    }

    @SuppressWarnings("unchecked")
    @Override
    public CustomItem deserialize(JsonElement jsonElement, Type type,
                                      JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jObject = jsonElement.getAsJsonObject();

        NamespacedKey identifier = new NamespacedKey(plugin, jObject.get("identifier").getAsString());
        NamespacedKey modelKey = NamespacedKey.fromString(jObject.get("model").getAsString());
        CustomModelData model = CustomModelData.of(modelKey)
                .orElseGet(() -> {
                    log.warning("Couldn't find model '" + modelKey + "' for item " + identifier);
                    return new CustomModelData(CustomItemImpl.DEFAULT_MATERIAL.getKey(), 0);
                });
        Material material = CustomItemImpl.DEFAULT_MATERIAL;
        if (jObject.has("material")) {
            String materialName = jObject.get("material").getAsString();
            material = Material.matchMaterial(materialName);
            if (material == null) {
                log.warning("Couldn't find item material: " + materialName);
                material = CustomItemImpl.DEFAULT_MATERIAL;
            }
        }
        Component name = (jObject.get("name").isJsonObject()
                ? Component.translatable(jObject.get("name").getAsJsonObject().get("translation").getAsString())
                : Component.text(jObject.get("name").getAsString()))
                .decoration(TextDecoration.ITALIC, false);

        CustomItemImpl cItem = CustomItemImpl.builder()
                .identifier(identifier)
                .model(model)
                .material(material)
                .name(name)
                .build();
        Class<? extends ItemBehaviour> behaviourClazz;
        if (jObject.has("class")) {
            try {
                Class<?> clazz = Class.forName(jObject.get("class").getAsString());
                if (!ItemBehaviour.class.isAssignableFrom(clazz)) {
                    throw new JsonParseException("Custom item behaviour class '" + clazz.getName()
                            + "' does not implement '" + ItemBehaviour.class.getName() + "'");
                }
                behaviourClazz = (Class<? extends ItemBehaviour>) clazz;
            } catch (ClassNotFoundException e) {
                throw new JsonParseException(e);
            }
        } else {
            behaviourClazz = DefaultItemBehaviour.class;
        }
        cItem.setBehaviour(behaviourClazz);
        return cItem;
    }
}
