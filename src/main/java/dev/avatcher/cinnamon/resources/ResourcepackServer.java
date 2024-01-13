package dev.avatcher.cinnamon.resources;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import dev.avatcher.cinnamon.Cinnamon;
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
public class ResourcepackServer implements HttpHandler, Listener {
    private final Logger log;
    private final ExecutorService executor;
    private final HttpServer server;
    @Getter
    private final URL url;

    @Getter
    private boolean active;
    @Getter
    private byte[] resourcePack;
    @Getter
    private byte[] resourcePackSHA1;
    @Getter
    @Setter
    private Component message;

    /**
     * Creates a {@link ResourcepackServer}.
     *
     * @param port The port of the http server
     * @param url URL link to download the resourcepack
     * @param message Message for when player is suggested to install resourcepack
     */
    public ResourcepackServer(int port, @Nullable URL url, Component message) throws IOException {
        this.log = Cinnamon.getInstance().getLogger();

        this.url = url == null
                ? new URL("http://localhost:%s".formatted(port))
                : url;
        this.executor = Executors.newFixedThreadPool(3);
        this.server = HttpServer.create(new InetSocketAddress(port), 0);
        this.server.setExecutor(this.executor);
        this.server.createContext("/", this);

        this.setResourcePack(resourcePack);
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
        exchange.sendResponseHeaders(200, this.resourcePack.length);
        try (var out = exchange.getResponseBody()) {
            out.write(this.resourcePack);
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
            player.setResourcePack(this.url.toString(), this.resourcePackSHA1, this.message, true);
            return;
        }
        if (player.isOp()) {
            player.sendMessage(Component.text("Cinnamon: Resourcepack transmitting is ")
                    .color(NamedTextColor.RED)
                    .append(Component.text("OFF").decorate(TextDecoration.BOLD)));
        }
    }

    /**
     * Sets {@link #resourcePack} and calculates its SHA1 hash.
     *
     * @param resourcePack Resourcepack
     */
    public void setResourcePack(byte[] resourcePack) {
        if (Arrays.equals(this.resourcePack, resourcePack)) return;
        this.resourcePack = resourcePack;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA1");
            this.resourcePackSHA1 = digest.digest(this.resourcePack);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        log.info("Provided resourcepack of size " + this.resourcePack.length / 1024.0 + "kB to transmitting server");
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

    /**
     * Gets resourcepack's SHA1 code as a string.
     *
     * @return Resourcepack's SHA1 string
     */
    public String getResourcepackSHA1String() {
        Formatter formatter = new Formatter();
        for (byte b : this.resourcePackSHA1) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }
}
