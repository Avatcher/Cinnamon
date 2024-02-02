package dev.avatcher.cinnamon.core.resources;

import com.google.common.base.Preconditions;
import dev.avatcher.cinnamon.api.Cinnamon;
import dev.avatcher.cinnamon.api.items.CustomItem;
import dev.avatcher.cinnamon.core.CinnamonPlugin;
import dev.avatcher.cinnamon.core.block.NoteblockCustomBlock;
import org.bukkit.Material;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

public class ResourcePackBuilder {
    /**
     * Default pack.mcmeta file to be inserted in resourcepack
     */
    private static final String DEFAULT_PACK_MCMETA = readResource("resourcepack/pack.mcmeta");

    /**
     * Template of Minecraft model for item containing model
     * overrides depending on item's CustomModelData
     *
     * @see CustomModelData
     */
    private static final String ITEM_MODEL_OVERRIDE_TEMPLATE = readResource("resourcepack/item_model_override.tjson");

    private static final String BLOCK_MODEL_OVERRIDE_TEMPLATE = readResource("resourcepack/block_model_override.tjson");

    /**
     * Path to Cinnamon's data folder with resource pack
     */
    private static final String RESOURCE_PACK_FOLDER = "resourcepack/";

    private static String readResource(String path) {
        try (var in = CinnamonPlugin.class.getClassLoader().getResourceAsStream(path)) {
            assert in != null;
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("An exception occurred while loading java resources", e);
        }
    }

    private final Path outFolder;
    private final Path outAssets;

    public ResourcePackBuilder(Path outFolder) throws IOException {
        Preconditions.checkNotNull(outFolder);
        this.outFolder = outFolder;
        this.outAssets = outFolder.resolve("assets/");

        Files.createDirectories(outFolder);
    }

    public void registerAssets(CinnamonResources resources) throws IOException {
        Path assets = resources.getAssetsFolder();

        try (var walker = Files.walk(assets)) {
            walker.filter(Files::isRegularFile).forEach(file -> {
                String relative = assets.relativize(file).toString();
                Path fileInResourcePack = outAssets.resolve(relative);
                try {
                    Files.createDirectories(fileInResourcePack.resolve(".."));
                    Files.copy(file, outAssets.resolve(relative));
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
        }
    }

    public void build() throws IOException {
        Preconditions.checkNotNull(this.outFolder);

        this.buildItemModelOverrides();
        this.buildBlockModelOverrides();
        this.buildPackMeta();
    }

    public byte[] buildZip() throws IOException {
        Path zipFile = Files.createTempFile("cinnamon", "resourcepack.zip");
        Files.deleteIfExists(zipFile);
        try (FileSystem fs = FileSystems.newFileSystem(zipFile, Map.of("create", "true"));
             var walker = Files.walk(outFolder)) {
            walker.filter(Files::isRegularFile)
                    .forEach(file -> {
                        Path relative = outFolder.relativize(file);
                        Path inZip = fs.getPath(relative.toString());
                        try {
                            Files.createDirectories(inZip.resolve(".."));
                            Files.copy(file, inZip, StandardCopyOption.REPLACE_EXISTING);
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    });
        }
        byte[] zipArchive = Files.readAllBytes(zipFile);
        Files.deleteIfExists(zipFile);
        return zipArchive;
    }

    public void clear() throws IOException {
        if (!Files.exists(this.outAssets)) return;
        Files.walkFileTree(this.outAssets, new SimpleFileVisitor<>(){
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private void buildItemModelOverrides() {
        Cinnamon.getInstance().getCustomItems().stream()
                .map(CustomItem::getMaterial)
                .collect(Collectors.toSet())
                .forEach(material -> {
                    try {
                        this.buildItemModelOverrides(material);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });
    }

    private void buildItemModelOverrides(Material material) throws IOException {
        Path modelOverridesPath = this.outAssets
                .resolve("minecraft/models/item/")
                .resolve(material.getKey().getKey() + ".json");
        Files.createDirectories(modelOverridesPath.resolve(".."));

        String modelOverridesValues = CinnamonPlugin.getInstance().getResourcesManager().getCustomModelData()
                .stream()
                .sorted(Comparator.comparingInt(CustomModelData::numeric))
                .map(model -> "    { \"predicate\": { \"custom_model_data\": %d }, \"model\": \"%s\" }"
                        .formatted(model.numeric(), model.identifier()))
                .collect(Collectors.joining(",\n"));
        String modelOverrides = ITEM_MODEL_OVERRIDE_TEMPLATE
                .formatted(
                        CustomModelData.HANDHELD_ITEMS.contains(material)
                                ? "minecraft:item/handheld"
                                : "minecraft:item/generated",
                        material.getKey().getNamespace() + ":item/" + material.getKey().getKey(),
                        modelOverridesValues);
        Files.writeString(modelOverridesPath, modelOverrides);
    }

    private void buildBlockModelOverrides() throws IOException {
        Path modelOverridesPath = this.outAssets.resolve("minecraft/blockstates/note_block.json");
        Files.createDirectories(modelOverridesPath.resolve(".."));

        String modelOverridesValues = Cinnamon.getInstance().getCustomBlocks().stream()
                .filter(customBlock -> customBlock instanceof NoteblockCustomBlock)
                .map(customBlock -> (NoteblockCustomBlock) customBlock)
                .filter(b -> b != NoteblockCustomBlock.NOTEBLOCK)
                .map(block -> "    \"note=%d,instrument=%s\": { \"model\": \"%s\" }"
                        .formatted(block.getTune().note(),
                                block.getTune().getInstrumentMcString(),
                                block.getModel().asString())
                )
                .collect(Collectors.joining(",\n"));
        String modelOverrides = BLOCK_MODEL_OVERRIDE_TEMPLATE.formatted(modelOverridesValues);
        Files.writeString(modelOverridesPath, modelOverrides);
    }

    private void buildPackMeta() throws IOException {
        Path packMeta = outFolder.resolve("pack.mcmeta");
        if (Files.exists(packMeta)) return;
        Files.writeString(packMeta, DEFAULT_PACK_MCMETA, StandardOpenOption.CREATE_NEW);
    }
}
