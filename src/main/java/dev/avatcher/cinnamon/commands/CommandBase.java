package dev.avatcher.cinnamon.commands;

import dev.jorel.commandapi.CommandAPICommand;

public interface CommandBase {
    /**
     * Gets the name of the command.
     *
     * @return Name of the command
     */
    String getName();

    /**
     * Constructs a {@link dev.jorel.commandapi.CommandAPICommand} instance
     * for the command, that can be used to register it.
     *
     * @return A {@link CommandAPICommand} instance
     */
    CommandAPICommand getCommandApiCommand();
}
