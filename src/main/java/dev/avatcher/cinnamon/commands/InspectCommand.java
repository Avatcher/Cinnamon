package dev.avatcher.cinnamon.commands;

import dev.avatcher.cinnamon.Cinnamon;
import dev.avatcher.cinnamon.item.CItem;
import dev.avatcher.cinnamon.resources.CustomModelData;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.executors.CommandArguments;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.Optional;

public class InspectCommand implements CommandBase {
    public static final String NAME = "inspect";
    public static final String SUBCOMMAND_ITEM = "item";
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

    private void inspectItem(Player player, CommandArguments args) {
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

        if (!CItem.isCustom(itemStack)) {
            player.sendMessage(
                    message.append(Component.text("└ Regular item").color(NamedTextColor.GRAY)));
            return;
        }
        Optional<CItem> cItemOptional = CItem.of(itemStack);
        if (cItemOptional.isPresent()) {
            message = message
                    .append(miniMessage.deserialize("<gray>├ Id: <gray><white>" + cItemOptional.get().getIdentifier() + "</white>\n"))
                    .append(this.inspectItemModel(itemStack, cItemOptional.get()));
        } else {
            String itemId = itemStack.getItemMeta().getPersistentDataContainer().get(CItem.IDENTIFIER_KEY, PersistentDataType.STRING);
            message = message.append(miniMessage.deserialize("<gray>└ Id: </gray><red>" + itemId + "\n   ⚠ Invalid identifier</red>\n"));
        }
        player.sendMessage(message);
    }

    private Component inspectItemModel(ItemStack itemStack, CItem cItem) {
        int actualModelNum = itemStack.getItemMeta().getCustomModelData();
        boolean isRightModel = actualModelNum == cItem.getModel().numeric();

        String messageString;
        if (isRightModel) {
            messageString = """
                    └ <gray>Model:</gray>
                      ├ <gray>Id:</gray> <white>%s</white>
                      └ <gray>Num:</gray> <white>%s</white>
                    """
            .formatted(
                    cItem.getModel().identifier(),
                    cItem.getModel().numeric());
        } else {
            CustomModelData actualModel = Cinnamon.getInstance().getResourcesManager().getCustomModelData().getValues()
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
                    cItem.getModel().identifier(),
                    actualModelNum,
                    cItem.getModel().numeric());
        }
        return MiniMessage.miniMessage().deserialize(messageString);
    }

    private void inspectBlock(Player player, CommandArguments args) {
        player.sendMessage(Component.text("Not implemented"));
    }
}
