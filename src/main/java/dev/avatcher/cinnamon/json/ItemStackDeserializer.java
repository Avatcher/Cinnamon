package dev.avatcher.cinnamon.json;

import com.google.gson.*;
import dev.avatcher.cinnamon.Cinnamon;
import dev.avatcher.cinnamon.item.CItem;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Type;
import java.util.logging.Logger;

/**
 * JSON deserializer for {@link ItemStack}
 */
public class ItemStackDeserializer implements JsonDeserializer<ItemStack> {
    private final Logger log;

    /**
     * Creates an recipe ingredient json deserializer.
     */
    public ItemStackDeserializer() {
        this.log = Cinnamon.getInstance().getLogger();
    }

    @Override
    public ItemStack deserialize(
            JsonElement jsonElement,
            Type type,
            JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jObject = jsonElement.getAsJsonObject();

        int amount = 1;
        if (jObject.has("amount")) {
            amount = jObject.get("amount").getAsInt();
        }
        if (jObject.has("item")) {
            NamespacedKey key = NamespacedKey.fromString(jObject.get("item").getAsString());
            assert key != null;
            ItemStack itemStack;
            if (key.getNamespace().equals(NamespacedKey.MINECRAFT_NAMESPACE)) {
                Material material = Material.getMaterial(key.getKey().toUpperCase());
                assert material != null;
                itemStack = new ItemStack(material, amount);
            } else {
                CItem cItem = CItem.of(key).orElseThrow();
                itemStack = cItem.getItemStack();
                itemStack.setAmount(amount);
            }
            return itemStack;
        }
        return null;
    }
}
