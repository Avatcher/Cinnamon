package dev.avatcher.cinnamon.core.commands;

import dev.avatcher.cinnamon.api.items.CustomItem;
import dev.avatcher.cinnamon.core.CinnamonPlugin;
import dev.avatcher.cinnamon.core.block.NoteblockCustomBlock;
import dev.avatcher.cinnamon.core.item.CustomItemImpl;
import dev.avatcher.cinnamon.core.resources.CustomModelData;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.executors.CommandArguments;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * <p>An inspect command, that prints information
 * about certain custom item or block</p>
 * <p>Created for debugging purposes</p>
 */
public class InspectCommand implements CommandBase {
    /**
     * The name of the command
     */
    public static final String NAME = "inspect";

    /**
     * The name of the subcommand, responsible
     * for printing information about custom items
     */
    public static final String SUBCOMMAND_ITEM = "item";

    /**
     * The name of the subcommand, responsible
     * for printing information about custom blocks
     */
    public static final String SUBCOMMAND_BLOCK = "block";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public CommandAPICommand getCommandApiCommand() {
        return new CommandAPICommand("inspect")
                .withPermission(CommandPermission.OP)
                .withSubcommand(new CommandAPICommand(SUBCOMMAND_ITEM)
                        .executesPlayer(this::inspectItem)
                )
                .withSubcommand(new CommandAPICommand(SUBCOMMAND_BLOCK)
                        .executesPlayer(this::inspectBlock)
                );
    }

    /**
     * Subcommand to print information about custom item
     *
     * @param player Player running the command
     * @param args Command arguments
     */
    private void inspectItem(@NotNull Player player, CommandArguments args) {
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        if (itemStack.getType() == Material.AIR) {
            player.sendMessage(Component.text("No item in hand to inspect").color(NamedTextColor.RED));
            return;
        }
        MiniMessage miniMessage = MiniMessage.miniMessage();
        Component message = Component.empty()
                .color(NamedTextColor.GRAY)
                .append(itemStack.displayName()
                        .color(NamedTextColor.WHITE))
                .append(Component.newline());

        if (!CustomItem.isCustom(itemStack)) {
            player.sendMessage(
                    message.append(Component.text("└ Regular item")));
            return;
        }
        Optional<CustomItem> optionalCustomItem = CustomItem.get(itemStack);
        if (optionalCustomItem.isPresent()) {
            message = message
                    .append(miniMessage.deserialize("<gray>├ Id: <gray><white>" + optionalCustomItem.get().getKey() + "</white>\n"))
                    .append(this.inspectItemModel(itemStack, optionalCustomItem.get()));
        } else {
            String itemId = itemStack.getItemMeta().getPersistentDataContainer().get(CustomItemImpl.IDENTIFIER_KEY, PersistentDataType.STRING);
            message = message.append(miniMessage.deserialize("<gray>└ Id: </gray><red>" + itemId + "\n   ⚠ Invalid identifier</red>\n"));
        }
        player.sendMessage(message);
    }

    /**
     * Constructs a {@link Component} containing information
     * about custom item's model.
     *
     * @param itemStack Itemstack of the custom item
     * @param customItem Custom item
     * @return A new {@link Component}, containing custom item's
     *         model information
     */
    @Contract(value = "_, _ -> new", pure = true)
    private @NotNull Component inspectItemModel(@NotNull ItemStack itemStack, @NotNull CustomItem customItem) {
        if (!(customItem instanceof CustomItemImpl item)) return Component.empty();
        int actualModelNum = itemStack.getItemMeta().getCustomModelData();
        boolean isRightModel = actualModelNum == item.getModel().numeric();

        String messageString;
        if (isRightModel) {
            messageString = """
                    └ <gray>Model:</gray>
                      ├ <gray>Id:</gray> <white>%s</white>
                      └ <gray>Num:</gray> <white>%s</white>
                    """
            .formatted(
                    item.getModel().identifier(),
                    item.getModel().numeric());
        } else {
            CustomModelData actualModel = CinnamonPlugin.getInstance().getResourcesManager().getCustomModelData().getValues()
                    .stream()
                    .filter(model -> model.numeric() == actualModelNum).findFirst()
                    .orElse(null);
            String actualModelName = actualModel == null
                    ? "[not found]"
                    : actualModel.identifier().toString();
            messageString = """
                    ├ <gray>Model:</gray>
                      ├ <gray>Id:</gray> <red>%s</red>
                      │  └ <dark_gray>(exp. %s)</dark_gray>
                      └ <gray>Num:</gray> <red>%s</red>
                         └ <dark_gray>(exp. %s)</dark_gray>
                    """
            .formatted(
                    actualModelName,
                    item.getModel().identifier(),
                    actualModelNum,
                    item.getModel().numeric());
        }
        return MiniMessage.miniMessage().deserialize(messageString);
    }

    /**
     * Subcommand to print information about custom block
     *
     * @param player Player running the command
     * @param args Command arguments
     */
    private void inspectBlock(Player player, CommandArguments args) {
        Block block = player.getTargetBlockExact(5);
        if (block == null) {
            player.sendMessage(Component.text("No block is selected.").color(NamedTextColor.RED));
            return;
        }
        MiniMessage miniMessage = MiniMessage.miniMessage();
        Location blockLocation = block.getLocation().toBlockLocation();
        Component message = Component.empty()
                .color(NamedTextColor.GRAY)
                .append(miniMessage.deserialize("<white>[%.0f %.0f %.0f]</white>"
                        .formatted(blockLocation.x(), blockLocation.y(), blockLocation.z())))
                .append(Component.newline());
        if (block.getType() != Material.NOTE_BLOCK) {
            message = message.append(miniMessage.deserialize(" └ <white>Regular block</white>"));
            player.sendMessage(message);
            return;
        }
        Optional<NoteblockCustomBlock> cBlockOptional = NoteblockCustomBlock.of(block);
        NoteBlock noteBlock = (NoteBlock) block.getBlockData();
        String messageString;
        if (cBlockOptional.isPresent()) {
            messageString = """
                ├ Id: <white>%s</white>
                └ Noteblock Tone:
                  ├ Note: <white>%s</white>
                  └ Instrument: <white>%s</white>
                """.formatted(cBlockOptional.get().getIdentifier(),
                    noteBlock.getNote().getId(),
                    noteBlock.getInstrument().getType());
        } else {
            messageString = """
                ├ Id: <red>Unknown block</red>
                └ Noteblock Tone:
                  ├ Note: <red>%s</red>
                  └ Instrument: <red>%s</red>
                """.formatted(noteBlock.getNote().getId(),
                    noteBlock.getInstrument().getType());
        }
        message = message.append(miniMessage.deserialize(messageString));
        player.sendMessage(message);
    }
}
