package dev.avatcher.cinnamon.json;

import com.google.common.base.Preconditions;
import com.google.gson.*;
import dev.avatcher.cinnamon.item.CItem;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Type;

/**
 * JSON deserializer for {@link ItemStack}
 */
public class ItemStackDeserializer implements JsonDeserializer<ItemStack> {
    /**
     * JSON field containing item's identifier
     */
    public static final String IDENTIFIER_FIELD = "item";

    /**
     * JSON field containing item's amount
     */
    public static final String AMOUNT_FIELD = "amount";

    @Override
    public ItemStack deserialize(
            JsonElement jsonElement,
            Type type,
            JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jObject = jsonElement.getAsJsonObject();
        Preconditions.checkNotNull(jObject.get(IDENTIFIER_FIELD), "Missing JSON field '"
                + IDENTIFIER_FIELD + "'");
        int amount = 1;
        if (jObject.has(AMOUNT_FIELD)) {
            amount = jObject.get(AMOUNT_FIELD).getAsInt();
        }
        NamespacedKey key = NamespacedKey.fromString(jObject.get(IDENTIFIER_FIELD).getAsString());
        Preconditions.checkNotNull(key, "Invalid item identifier: " + jObject.get(IDENTIFIER_FIELD).getAsString());
        ItemStack itemStack;
        if (key.getNamespace().equals(NamespacedKey.MINECRAFT_NAMESPACE)) {
            Material material = Material.getMaterial(key.getKey().toUpperCase());
            Preconditions.checkNotNull(material, "Couldn't find material: " + key);
            itemStack = new ItemStack(material, amount);
        } else {
            CItem cItem = CItem.of(key).orElseThrow();
            itemStack = cItem.getItemStack();
            itemStack.setAmount(amount);
        }
        return itemStack;
    }
}
