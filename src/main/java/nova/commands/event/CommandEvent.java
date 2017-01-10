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

package nova.commands.event;

import nova.commands.Args;
import nova.commands.Command;
import nova.core.entity.component.Player;
import nova.core.event.bus.CancelableEvent;

import java.util.Optional;

/**
 * All events related to the command.
 *
 * @author ExE Boss
 */
public abstract class CommandEvent extends CancelableEvent {

	public final Command command;

	public CommandEvent(Command command) {
		this.command = command;
	}

	/**
	 * Event is triggered when a something happens during command execution.
	 */
	public static abstract class CommandHandleEvent extends CommandEvent {
		public final Optional<Player> player;
		public final Optional<Args> argsObj;
		public final boolean prejoined;
		public final String[] args;

		public CommandHandleEvent(Optional<Player> player, Command command, Args argsObj, boolean prejoined, String... args) {
			super(command);
			this.player = player;
			this.argsObj = Optional.of(argsObj);
			this.prejoined = prejoined;
			this.args = args;
		}

		public CommandHandleEvent(Optional<Player> player, Command command, boolean prejoined, String... args) {
			super(command);
			this.player = player;
			this.argsObj = Optional.empty();
			this.prejoined = prejoined;
			this.args = args;
		}
	}

	/**
	 * Event is triggered when a Command is registered.
	 *
	 * @see Command
	 * @see nova.commands.CommandManager#register(nova.commands.Command)
	 */
	public static class Register extends CancelableEvent {
		public Command command;

		public Register(Command command) {
			this.command = command;
		}
	}
}
