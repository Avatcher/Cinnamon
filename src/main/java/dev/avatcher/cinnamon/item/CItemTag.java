package dev.avatcher.cinnamon.item;

import lombok.Getter;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;

import java.lang.reflect.Field;
import java.util.Map;

@Getter
public class CItemTag {
    private final NamespacedKey identifier;
    private final Field field;
    private final PersistentDataType<?, ?> persistentDataType;

    private static final Map<Class<?>, PersistentDataType<?, ?>> persistentTypes = Map.ofEntries(
            Map.entry(byte.class, PersistentDataType.BYTE),
            Map.entry(short.class, PersistentDataType.SHORT),
            Map.entry(int.class, PersistentDataType.INTEGER),
            Map.entry(long.class, PersistentDataType.LONG),
            Map.entry(double.class, PersistentDataType.DOUBLE),
            Map.entry(float.class, PersistentDataType.FLOAT),
            Map.entry(boolean.class, PersistentDataType.BOOLEAN),
            Map.entry(Byte.class, PersistentDataType.BYTE),
            Map.entry(Short.class, PersistentDataType.SHORT),
            Map.entry(Integer.class, PersistentDataType.INTEGER),
            Map.entry(Long.class, PersistentDataType.LONG),
            Map.entry(Double.class, PersistentDataType.DOUBLE),
            Map.entry(Float.class, PersistentDataType.FLOAT),
            Map.entry(Boolean.class, PersistentDataType.BOOLEAN),

            Map.entry(byte[].class, PersistentDataType.BYTE_ARRAY),
            Map.entry(int[].class, PersistentDataType.INTEGER_ARRAY),
            Map.entry(long[].class, PersistentDataType.LONG_ARRAY),
            Map.entry(Byte[].class, PersistentDataType.BYTE_ARRAY),
            Map.entry(Integer[].class, PersistentDataType.INTEGER_ARRAY),
            Map.entry(Long[].class, PersistentDataType.LONG_ARRAY),

            Map.entry(String.class, PersistentDataType.STRING)
    );

    public CItemTag(NamespacedKey identifier, Field field) {
        this.identifier = identifier;
        this.field = field;
        Class<?> clazz = field.getType();
        this.persistentDataType = persistentTypes.getOrDefault(clazz, PersistentDataType.TAG_CONTAINER);
    }

    public Object get(CItemBehaviour behaviour) {
        try {
            return this.field.get(behaviour);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
