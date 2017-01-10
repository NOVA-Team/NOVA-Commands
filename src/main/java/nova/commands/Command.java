/*
 * Copyright (c) 2017 NOVA, All rights reserved.
 * This library is free software, licensed under GNU Lesser General Public License version 3
 *
 * This file is part of NOVA.
 *
 * NOVA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * NOVA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with NOVA.  If not, see <http://www.gnu.org/licenses/>.
 */

package nova.commands;

import nova.commands.exception.CommandException;
import nova.core.entity.component.Player;
import nova.core.event.bus.Event;
import nova.core.event.bus.EventBus;
import nova.core.util.Identifiable;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author ExE Boss
 */
public abstract class Command implements Identifiable {

	protected final String command;
	public final EventBus<Event> events = new EventBus<>();

	/**
	 * Constructs a new command instance.
	 *
	 * @param command The command name.
	 */
	public Command(String command) {
		this.command = command;
	}

	/**
	 * Get the usage of the command.
	 *
	 * Example: {@code command <necessary1> [--optional] <necessary1> [-o]}
	 *
	 * @param player The player who issued this command.
	 *
	 * @return The usage of the command.
	 */
	public abstract String getCommandUsage(Optional<Player> player);

	/**
	 * Handle the command.
	 *
	 * @param player The player who issued this command.
	 * Won't be present if the command was issued from the server command line.
	 * @param prejoined If strings in the {@code args} parameter contains spaces and "/' should not be treated as delimiters.
	 * Useful if using {@link ArgsParser}.
	 * @param args The arguments that were given by the player.
	 *
	 * @throws CommandException If something goes wrong during command execution.
	 */
	public abstract void handle(Optional<Player> player, boolean prejoined, String... args) throws CommandException;

	/**
	 * Get a list of possible ways the command could auto-complete.
	 *
	 * @param player The player who issued this command.
	 * Won't be present if the command was issued from the server command line.
	 * @param args The already completed arguments, with the
	 * on at index {@code args.length - 1} being the incomplete one.
	 * @param prejoined If strings in the {@code args} parameter contains spaces and "/' should not be treated as delimiters.
	 * Useful if using {@link ArgsParser}.
	 * @param pos The position of the block that the player is looking at, if any.
	 * @return The list. Must not be null.
	 */
	public List<String> getAutocompleteList(Optional<Player> player, boolean prejoined, String[] args, Optional<Vector3D> pos) {
		return Collections.emptyList();
	}

	/**
	 * Returns the command name.
	 *
	 * @return The command name.
	 */
	public final String getCommandName() {
		return command;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @return The ID for a command is the command name in lowercase.
	 */
	@Override
	public final String getID() {
		return command.toLowerCase();
	}
}
