package dev.avatcher.cinnamon.resources.modules;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import dev.avatcher.cinnamon.block.CBlock;
import dev.avatcher.cinnamon.block.NoteblockTune;
import dev.avatcher.cinnamon.item.CItem;
import dev.avatcher.cinnamon.item.behaviour.CustomBlockPlacingItem;
import dev.avatcher.cinnamon.json.CBlockDeserializer;
import dev.avatcher.cinnamon.resources.CinnamonResources;
import lombok.Builder;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * A Cinnamon Module storing custom blocks
 *
 * @see dev.avatcher.cinnamon.resources.CinnamonModule
 */
@Getter
public class CBlockModule extends AbstractCinnamonModule<CBlock> {
    private final NoteblockTuneModule noteblockTuneModule;
    private final CItemModule itemsModule;
    private final CustomModelDataModule customModelDataModule;

    /**
     * Creates a new Custom Blocks Module with a
     * dependency on a certain NoteblockTune Module.
     *
     * @param noteblockTuneModule A NoteblockTune Module dependency
     *
     * @see NoteblockTune
     */
    public CBlockModule(NoteblockTuneModule noteblockTuneModule, CItemModule itemsModule) {
        super(CBlock.class);
        this.noteblockTuneModule = noteblockTuneModule;
        this.itemsModule = itemsModule;
        this.customModelDataModule = itemsModule.getCustomModelDataModule();
        this.map.put(CBlock.NOTEBLOCK.getIdentifier(), CBlock.NOTEBLOCK);
    }

    @Override
    public void register(NamespacedKey key, CBlock block) {
        if (!this.noteblockTuneModule.getKeys().contains(key)) {
            this.noteblockTuneModule.register(key, block.getTune());
        }
        super.register(key, block);
    }

    @Override
    public void load(@NotNull CinnamonResources resources) throws IOException {
        Path blocksFolder = resources.getBlocksFolder();
        if (!Files.exists(blocksFolder)) return;
        try (var walker = Files.walk(blocksFolder)) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(BlockRegistrationRequest.class,
                            new CBlockDeserializer(resources.getPlugin()))
                    .create();
            int wasLoaded = this.map.size();
            walker.filter(Files::isRegularFile)
                    .map(file -> {
                        try (var in = new InputStreamReader(Files.newInputStream(file))) {
                            return gson.fromJson(in, BlockRegistrationRequest.class);
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    })
                    .forEach(request -> {
                        NoteblockTune noteblockTune = this.noteblockTuneModule.getFreeTone(request.getIdentifier());
                        CBlock cBlock = new CBlock(request.getIdentifier(), request.getModel(), noteblockTune);
                        this.register(cBlock.getIdentifier(), cBlock);
                        if (request.isItemRequested()) {
                            NamespacedKey modelKey = new NamespacedKey(cBlock.getIdentifier().getNamespace(), "block/" + cBlock.getIdentifier().getKey());
                            CustomBlockPlacingItem behaviour = new CustomBlockPlacingItem(cBlock);
                            CItem item = CItem.builder()
                                    .identifier(cBlock.getIdentifier())
                                    .name(Component.translatable("block." + cBlock.getIdentifier().getNamespace() + "." + cBlock.getIdentifier().getKey())
                                            .decoration(TextDecoration.ITALIC, false))
                                    .model(itemsModule.getCustomModelDataModule().createAndRegister(modelKey))
                                    .behaviour(behaviour)
                                    .build();
                            this.itemsModule.register(item.getIdentifier(), item);
                        }
                    });
            int loaded = this.map.size() - wasLoaded;
            log.info("[%s] Loaded a total of %d block(s)".formatted(this.clazz.getSimpleName(), loaded));
        }
    }

    /**
     * A request to register a custom block.
     */
    @Builder
    @Getter
    public static class BlockRegistrationRequest {
        private NamespacedKey identifier;
        private NamespacedKey model;
        @Builder.Default
        @SerializedName("create-item")
        private boolean itemRequested = true;
    }
}
