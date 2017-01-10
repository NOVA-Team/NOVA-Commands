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

import java.util.Optional;

import nova.commands.event.CommandEvent;
import nova.core.util.registry.Manager;
import nova.core.util.registry.Manager.ManagerEvent;
import nova.core.util.registry.Registry;
import nova.internal.core.Game;

/**
 * @author ExE Boss
 */
public final class CommandManager extends Manager<CommandManager> {

	public final Registry<Command> registry;

	public CommandManager() {
		this.registry = new Registry<>();
	}

	/**
	 * Register a command.
	 *
	 * @param command The command to register.
	 * @return The registered command. (May be different)
	 */
	public Command register(Command command) {
		CommandEvent.Register event = new CommandEvent.Register(command);
		Game.events().publish(event);
		registry.register(event.command);
		return event.command;
	}

	/**
	 * Get a registered command.
	 *
	 * @param command The command name/ID
	 * @return The command or an empty Optional, if it isn't registered.
	 */
	public Optional<Command> get(String command) {
		return registry.get(command.toLowerCase());
	}

	@Override
	public void init() {
		Game.events().publish(new Init(this));
	}

	/**
	 * The CommandManger initialization event.
	 *
	 * Event is triggered when a CommandManager is initialized.
	 */
	public class Init extends ManagerEvent<CommandManager> {
		public Init(CommandManager manager) {
			super(manager);
		}
	}
}
