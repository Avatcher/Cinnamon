package dev.avatcher.cinnamon.core.block.events;

import dev.avatcher.cinnamon.api.blocks.CustomBlockBehaviour;
import dev.avatcher.cinnamon.api.blocks.events.CustomBlockPlaceEvent;
import dev.avatcher.cinnamon.core.block.AbstractCustomBlockBehaviourEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.bukkit.entity.Player;

/**
 * Implementation of {@link CustomBlockPlaceEvent}
 */
@SuperBuilder
@AllArgsConstructor
public class CustomBlockPlaceEventImpl extends AbstractCustomBlockBehaviourEvent implements CustomBlockPlaceEvent {
    @Getter
    private final Player player;

    @Override
    public void fire(CustomBlockBehaviour behaviour) {
        behaviour.onPlace(this);
    }
}
