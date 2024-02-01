package dev.avatcher.cinnamon.api.items.behaviour;

import dev.avatcher.cinnamon.api.items.ItemBehaviour;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface CooldownItem extends ItemBehaviour {
    boolean isCooledDown(@NotNull Player player);

    void coolDown(@NotNull Player player);
}
