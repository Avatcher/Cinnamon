package dev.avatcher.cinnamon.core.resources.resourcepack;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import dev.avatcher.cinnamon.api.resources.Resourcepack;
import dev.avatcher.cinnamon.api.resources.ResourcepackServer;
import dev.avatcher.cinnamon.core.CinnamonPlugin;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Formatter;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

/**
 * Http server responsible for transmitting Cinnamon
 * resourcepack to players
 */
public class ResourcepackServerImpl implements ResourcepackServer, HttpHandler, Listener {
    private final Logger log;
    private final ExecutorService executor;
    private final HttpServer server;
    @Getter
    private final URL url;

    @Getter
    private boolean active;
    @Getter
    private byte[] resourcepackBytes;
    @Getter
    private byte[] resourcepackSHA1;
    @Getter
    @Setter
    private Component message;

    /**
     * Creates a {@link ResourcepackServerImpl}.
     *
     * @param port The port of the http server
     * @param url URL link to download the resourcepack
     * @param message Message for when player is suggested to install resourcepack
     */
    public ResourcepackServerImpl(int port, @Nullable URL url, Component message) throws IOException {
        this.log = CinnamonPlugin.getInstance().getLogger();

        this.url = url == null
                ? new URL("http://localhost:%s".formatted(port))
                : url;
        this.executor = Executors.newFixedThreadPool(3);
        this.server = HttpServer.create(new InetSocketAddress(port), 0);
        this.server.setExecutor(this.executor);
        this.server.createContext("/", this);

        this.setResourcepackBytes(resourcepackBytes);
        this.message = message;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!canDownload(exchange)) {
            exchange.sendResponseHeaders(403, 0);
            exchange.close();
            log.warning("Rejected resourcepack request: " + exchange.getRemoteAddress().getAddress());
            return;
        }
        Headers headers = exchange.getResponseHeaders();
        headers.set("Content-Type", "application/zip");
        headers.set("Content-Disposition", "attachment; filename=\"resourcepack.zip\"");
        headers.set("Content-Transfer-Encoding", "binary");
        exchange.sendResponseHeaders(200, this.resourcepackBytes.length);
        try (var out = exchange.getResponseBody()) {
            out.write(this.resourcepackBytes);
        }
        exchange.close();

        log.info("Sent resourcepack to " + exchange.getRemoteAddress().getAddress());
    }

    /**
     * Asks players to install the resourcepack, when they join the server.
     *
     * @param event Event
     */
    @EventHandler
    public void onPlayerJoin(@NotNull PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (this.active) {
            this.applyTo(event.getPlayer());
            return;
        }
        if (player.isOp()) {
            player.sendMessage(Component.text("Cinnamon: Resourcepack transmitting is ")
                    .color(NamedTextColor.RED)
                    .append(Component.text("OFF").decorate(TextDecoration.BOLD)));
        }
    }

    /**
     * Sets {@link #resourcepackBytes} and calculates its SHA1 hash.
     *
     * @param resourcepackBytes Resourcepack
     */
    public void setResourcepackBytes(byte[] resourcepackBytes) {
        if (Arrays.equals(this.resourcepackBytes, resourcepackBytes)) return;
        this.resourcepackBytes = resourcepackBytes;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA1");
            this.resourcepackSHA1 = digest.digest(this.resourcepackBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        log.info("Provided resourcepack of size " + this.getSizeString(this.resourcepackBytes.length) + " to transmitting server");
        log.info("Resourcepack URL: " + this.url);
        log.info("Resourcepack SHA1: " + this.getResourcepackSHA1String());
    }

    /**
     * Checks, if http request for the resource pack is valid.
     *
     * @param exchange Http exchange
     * @return {@code true}, if request is valid
     */
    public boolean canDownload(@NotNull HttpExchange exchange) {
        var exchangeAddress = exchange.getRemoteAddress().getAddress();
        try {
            if (exchangeAddress.equals(InetAddress.getLocalHost())) return true;
        } catch (UnknownHostException ignored) { }
        return Bukkit.getServer().getOnlinePlayers().stream()
                .map(Player::getAddress)
                .filter(Objects::nonNull)
                .map(InetSocketAddress::getAddress)
                .anyMatch(exchangeAddress::equals);
    }

    @Override
    public boolean isStarted() {
        return this.active;
    }

    /**
     * Starts transmission of the resourcepack.
     */
    public void start() {
        log.info("Starting resourcepack transmitting server...");
        this.server.start();
        this.active = true;
    }

    /**
     * Stops the transmission of the resourcepack.
     */
    public void stop() {
        log.info("Stopping resourcepack transmitting server...");
        this.server.stop(1);
        this.executor.shutdownNow();
        this.active = false;
    }

    @Override
    public void applyTo(Player player) {
        this.applyTo(player, this.getMessage());
    }

    @Override
    public void applyTo(Player player, Component message) {
        player.setResourcePack(this.getDownloadLink().toString(), this.getResourcepackSHA1(), message, true);
    }

    @Override
    public URL getDownloadLink() {
        return this.url;
    }

    @Override
    public Resourcepack getResourcepack() {
        return new ResourcepackImpl(this);
    }

    /**
     * Gets resourcepack's SHA1 code as a string.
     *
     * @return Resourcepack's SHA1 string
     */
    public String getResourcepackSHA1String() {
        Formatter formatter = new Formatter();
        for (byte b : this.resourcepackSHA1) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }

    private String getSizeString(double size) {
        final String[] sizes = { "B", "KB", "MB" };
        int order = 0;
        while (size >= 1024.0 && order < sizes.length - 1) {
            order++;
            size = size / 1024.0;
        }
        return "%.2f%s".formatted(size, sizes[order]);
    }
}
