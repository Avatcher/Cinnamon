package dev.avatcher.cinnamon.core.resources.resourcepack;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 * Configuration of {@link dev.avatcher.cinnamon.api.resources.ResourcepackServer}
 */
@Getter
public final class ResourcepackServerConfig implements ConfigurationSerializable {
    /**
     * The main config's subsection name
     * containing this config.
     */
    public static final String CONFIG_PATH = "resourcepack-server";

    private final boolean enabled;
    private final boolean forceOnJoin;
    private final int port;
    private final URL url;
    private final Component message;

    /**
     * Builds config from given map
     * of configuration values.
     *
     * @param state Values of the config
     */
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

    public ResourcepackServerConfig(ConfigurationSection config) {
        this.enabled = config.getBoolean("enabled", true);
        this.forceOnJoin = config.getBoolean("force-on-join", true);
        this.port = config.getInt("port", 9300);
        try {
            String urlString = config.getString("url", "http://localhost:" + this.port);
            this.url = new URL(urlString);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        String messageJson = config.getString("message", "{\"text\": \"Please install our resourcepack.\\nIt is required for a better server experience.\", \"color\": \"yellow\"}");
        this.message = JSONComponentSerializer.json().deserialize(messageJson);
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
