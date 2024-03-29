package dev.avatcher.cinnamon.core.commands;

import dev.avatcher.cinnamon.api.items.CustomItem;
import dev.avatcher.cinnamon.core.CinnamonPlugin;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.EntitySelectorArgument;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.NamespacedKeyArgument;
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

import java.util.Collection;
import java.util.Optional;

/**
 * A Minecraft command for giving a custom item
 *
 * @see CustomItem
 */
public class CGiveCommand implements CommandBase {
    /**
     * Command's in-game name
     */
    @SuppressWarnings("SpellCheckingInspection")
    private static final String NAME = "cgive";

    /**
     * Execution for when a player runs the command.
     *
     * @param player Player that ran the command
     * @param args   Command arguments
     */
    @SuppressWarnings("SpellCheckingInspection")
    public void executePlayer(Player player, CommandArguments args) {
        Collection<Player> targets = args.getUnchecked("target");
        if (targets == null) {
            player.sendMessage(Component.text("Invalid target").color(NamedTextColor.RED));
            return;
        }
        NamespacedKey identifier = (NamespacedKey) args.get("item");
        if (identifier == null) {
            player.sendMessage(Component.text("Invalid item identifier").color(NamedTextColor.RED));
            return;
        }
        Optional<CustomItem> optionalCustomItem = CustomItem.get(identifier);
        if (optionalCustomItem.isEmpty()) {
            player.sendMessage(Component.text("Unknown custom item: " + identifier).color(NamedTextColor.RED));
            return;
        }
        CustomItem customItem = optionalCustomItem.get();
        int amount = (Integer) args.getOptional("amount").orElse(1);
        if (amount > 6400) {
            player.sendMessage(Component.translatable("commands.give.failed.toomanyitems")
                    .args(Component.text(64000), customItem.createItemStack().displayName())
                    .color(NamedTextColor.RED));
            return;
        }
        ItemStack itemStack = customItem.createItemStack();
        itemStack.setAmount((Integer) args.getOptional("amount").orElse(1));
        for (var target : targets) {
            target.getInventory().addItem(itemStack);
            target.playSound(target, Sound.ENTITY_ITEM_PICKUP, SoundCategory.MASTER, .25f, 2f);
        }
        if (targets.size() > 1) {
            Bukkit.getServer().broadcast(
                    Component.translatable("commands.give.success.multiple")
                            .args(
                                    Component.text(amount),
                                    itemStack.displayName(),
                                    Component.text(targets.size())
                            )
            );
        } else {
            Player target = targets.stream().findFirst().orElseThrow();
            Bukkit.getServer().broadcast(
                    Component.translatable("commands.give.success.single")
                            .args(
                                    Component.text(amount),
                                    itemStack.displayName(),
                                    Component.text(target.getName())
                            )
            );
        }
    }

    public String getName() {
        return NAME;
    }

    @Override
    public CommandAPICommand getCommandApiCommand() {
        return new CommandAPICommand(NAME)
                .withShortDescription("Gives a custom item")
                .withPermission(CommandPermission.OP)
                .withArguments(
                        new EntitySelectorArgument.ManyPlayers("target"),
                        new NamespacedKeyArgument("item")
                                .replaceSafeSuggestions(SafeSuggestions.suggest(info ->
                                        CinnamonPlugin.getInstance().getResourcesManager().getCustomItems().getKeys()
                                            .stream()
                                            .toList()
                                                .toArray(new NamespacedKey[0])
                                ))
                )
                .withOptionalArguments(
                        new IntegerArgument("amount", 1)
                )
                .executesPlayer(this::executePlayer);
    }
}
