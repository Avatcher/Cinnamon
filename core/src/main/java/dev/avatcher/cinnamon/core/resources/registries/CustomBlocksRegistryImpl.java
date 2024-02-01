package dev.avatcher.cinnamon.core.resources.registries;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import dev.avatcher.cinnamon.api.blocks.CustomBlock;
import dev.avatcher.cinnamon.api.blocks.CustomBlocksRegistry;
import dev.avatcher.cinnamon.api.items.behaviour.CustomBlockPlacingItem;
import dev.avatcher.cinnamon.core.block.NoteblockCustomBlock;
import dev.avatcher.cinnamon.core.block.NoteblockTune;
import dev.avatcher.cinnamon.core.item.CustomItemImpl;
import dev.avatcher.cinnamon.core.json.CBlockDeserializer;
import dev.avatcher.cinnamon.core.resources.CinnamonRegistry;
import dev.avatcher.cinnamon.core.resources.CinnamonResources;
import lombok.Builder;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * A Cinnamon Module storing custom blocks
 *
 * @see CinnamonRegistry
 */
@Getter
public class CustomBlocksRegistryImpl extends AbstractCinnamonRegistry<CustomBlock> implements CustomBlocksRegistry {
    private final NoteblockTuneRegistry noteblockTuneModule;
    private final CustomItemsRegistryImpl itemsModule;
    private final CustomModelDataRegistry customModelDataModule;

    /**
     * Creates a new Custom Blocks Module with a
     * dependency on a certain NoteblockTune Module.
     *
     * @param noteblockTuneModule A NoteblockTune Module dependency
     * @param itemsModule Custom items module
     *
     * @see NoteblockTune
     */
    public CustomBlocksRegistryImpl(NoteblockTuneRegistry noteblockTuneModule, @NotNull CustomItemsRegistryImpl itemsModule) {
        super(CustomBlock.class);
        this.noteblockTuneModule = noteblockTuneModule;
        this.itemsModule = itemsModule;
        this.customModelDataModule = itemsModule.getCustomModelDataModule();
        this.map.put(NoteblockCustomBlock.NOTEBLOCK.getIdentifier(), NoteblockCustomBlock.NOTEBLOCK);
    }

    @Override
    public CustomBlock get(Block block) {
        return NoteblockCustomBlock.of(block).orElse(null);
    }

    @Override
    public void register(NamespacedKey key, CustomBlock block) {
        if (block instanceof NoteblockCustomBlock noteblockCustomBlock
                && !this.noteblockTuneModule.getKeys().contains(key)) {
            this.noteblockTuneModule.register(key, noteblockCustomBlock.getTune());
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
                        NoteblockCustomBlock customBlock = new NoteblockCustomBlock(request.getIdentifier(), request.getModel(), noteblockTune);
                        this.register(customBlock.getIdentifier(), customBlock);
                        if (request.isItemRequested()) {
                            NamespacedKey modelKey = new NamespacedKey(customBlock.getIdentifier().getNamespace(), "block/" + customBlock.getIdentifier().getKey());
                            CustomBlockPlacingItem behaviour = new CustomBlockPlacingItem(resources.getPlugin(), customBlock);
                            CustomItemImpl item = CustomItemImpl.builder()
                                    .identifier(customBlock.getIdentifier())
                                    .name(Component.translatable("block." + customBlock.getIdentifier().getNamespace() + "." + customBlock.getIdentifier().getKey())
                                            .decoration(TextDecoration.ITALIC, false))
                                    .model(customModelDataModule.createAndRegister(modelKey))
                                    .behaviour(behaviour)
                                    .build();
                            this.itemsModule.register(item.getKey(), item);
                        }
                    });
            int loaded = this.map.size() - wasLoaded;
            log.info("[%s] Loaded a total of %d block(s)".formatted(this.clazz.getSimpleName(), loaded));
        }
    }

    @Override
    public boolean isCustom(Block block) {
        return NoteblockCustomBlock.isCustom(block);
    }

    @Override
    public boolean isInteractable(Block block) {
        return NoteblockCustomBlock.isInteractable(block);
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
