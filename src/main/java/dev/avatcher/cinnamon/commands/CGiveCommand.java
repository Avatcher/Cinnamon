package dev.avatcher.cinnamon.commands;

import dev.avatcher.cinnamon.Cinnamon;
import dev.avatcher.cinnamon.item.CItem;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.NamespacedKeyArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.arguments.SafeSuggestions;
import dev.jorel.commandapi.executors.CommandArguments;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class CGiveCommand {

    private static final String NAME = "cgive";

    public void executePlayer(Player player, CommandArguments args) {
        player.sendMessage("CINNAMON COMMAND");
        Player target = (Player) args.get("target");
        if (target == null) {
            player.sendMessage(Component.text("Invalid target").color(NamedTextColor.RED));
            return;
        }
        NamespacedKey identifier = (NamespacedKey) args.get("item");
        if (identifier == null) {
            player.sendMessage(Component.text("Invalid item identifier").color(NamedTextColor.RED));
            return;
        }
        Optional<CItem> optionalCustomItem = CItem.of(identifier);
        if (optionalCustomItem.isEmpty()) {
            player.sendMessage(Component.text("Unknown custom item: " + identifier).color(NamedTextColor.RED));
            return;
        }
        CItem cItem = optionalCustomItem.get();

        int amount = (Integer) args.getOptional("amount").orElse(1);
        if (amount < 1 || amount > 64) {
            player.sendMessage(Component.text("Invalid item amount: " + amount + "\nOnly values between 1 and 64 are allowed.").color(NamedTextColor.RED));
            return;
        }

        ItemStack itemStack = cItem.getItemStack();
        itemStack.setAmount((Integer) args.getOptional("amount").orElse(1));
        target.getInventory().addItem(itemStack);

        target.playSound(target, Sound.ENTITY_ITEM_PICKUP, SoundCategory.MASTER, .25f, 2f);
        Bukkit.getServer().broadcast(
                Component.translatable("commands.give.success.single")
                        .args(
                                Component.text(itemStack.getAmount()),
                                Component.translatable("chat.square_brackets")
                                        .args(cItem.getName()),
                                Component.text(target.getName())
                        )
        );
    }

    public CommandAPICommand getCommandAPICommand() {
        return new CommandAPICommand(NAME)
                .withShortDescription("Gives a custom item")
                .withPermission(CommandPermission.OP)
                .withArguments(
                        new PlayerArgument("target"),
                        new NamespacedKeyArgument("item")
                                .replaceSafeSuggestions(SafeSuggestions.suggest(info -> {
//                                    String identifier = info.currentArg();
                                    return Cinnamon.getInstance().getResourcesManager().getCustomItemMap().keySet()
                                            .stream()
//                                            .filter(name -> identifier.isEmpty() || name.asString().contains(identifier))
                                            .toList()
                                            .toArray(new NamespacedKey[0]);
                                }))
                )
                .withOptionalArguments(
                        new IntegerArgument("amount", 1, 64)
                )
                .executesPlayer(this::executePlayer);
    }
}
