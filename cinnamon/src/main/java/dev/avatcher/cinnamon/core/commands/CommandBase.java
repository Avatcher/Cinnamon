package dev.avatcher.cinnamon.core.commands;

import dev.jorel.commandapi.CommandAPICommand;

/**
 * A base of Cinnamon minecraft command
 */
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
