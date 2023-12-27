package dev.avatcher.cinnamon.json;

import com.google.gson.*;
import dev.avatcher.cinnamon.item.CItem;
import dev.avatcher.cinnamon.item.CItemBehaviour;
import dev.avatcher.cinnamon.item.behaviour.DefaultItemBehaviour;
import dev.avatcher.cinnamon.resources.CustomModelData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Type;

public class CItemDeserializer implements JsonDeserializer<CItem> {
    private final Plugin plugin;

    public CItemDeserializer(Plugin plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("unchecked")
    @Override
    public CItem deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jObject = jsonElement.getAsJsonObject();

        NamespacedKey identifier = new NamespacedKey(plugin, jObject.get("identifier").getAsString());
        CustomModelData model = new CustomModelData(identifier, 70000);
        Component name = (jObject.get("name").isJsonObject()
                ? Component.translatable(jObject.get("name").getAsJsonObject().get("translation").getAsString())
                : Component.text(jObject.get("name").getAsString()))
                .decoration(TextDecoration.ITALIC, false);
        Class<? extends CItemBehaviour> behaviourClazz;
        if (jObject.has("class")) {
            try {
                Class<?> clazz = Class.forName(jObject.get("class").getAsString());
                if (!CItemBehaviour.class.isAssignableFrom(clazz)) {
                    throw new JsonParseException("Custom item behaviour class '" + clazz.getName() + "' does not implement '" + CItemBehaviour.class.getName() + "'");
                }
                behaviourClazz = (Class<? extends CItemBehaviour>) clazz;
            } catch (ClassNotFoundException e) {
                throw new JsonParseException(e);
            }
        } else {
            behaviourClazz = DefaultItemBehaviour.class;
        }
        return new CItem(
                identifier,
                model,
                name,
                behaviourClazz
        );
    }
}
