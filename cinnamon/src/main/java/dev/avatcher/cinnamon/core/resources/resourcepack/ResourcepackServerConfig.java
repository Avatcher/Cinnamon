package dev.avatcher.cinnamon.core.resources.resourcepack;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

@Getter
public final class ResourcepackServerConfig implements ConfigurationSerializable {
    public static final String CONFIG_PATH = "resourcepack-server";

    private final boolean enabled;
    private final boolean forceOnJoin;
    private final int port;
    private final URL url;
    private final Component message;

    public ResourcepackServerConfig(Map<String, Object> state) {
        this.enabled = (boolean) state.get("enabled");
        this.forceOnJoin  = (boolean) state.get("force-on-join");
        this.port = (int) state.get("port");
        try {
            this.url = new URL((String) state.get("url"));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        this.message = JSONComponentSerializer.json().deserialize((String) state.get("message"));
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        return Map.of(
                "enabled", this.enabled,
                "force-on-join", this.forceOnJoin,
                "port", this.port,
                "url", this.url.toString(),
                "message", JSONComponentSerializer.json().serialize(this.message)
        );
    }
}
