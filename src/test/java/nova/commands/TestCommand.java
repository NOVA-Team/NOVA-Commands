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

import java.util.Optional;

/**
 * @author ExE Boss
 */
public class TestCommand extends Command {

	public TestCommand() {
		this("Test_Command");
	}

	public TestCommand(String command) {
		super(command);
	}

	public void handle(Optional<Player> player, boolean prejoined, String[] args, Optional<EventBus<Event>> events) throws CommandException {
		Args argsObj = new ArgsParser(args).args(String.class).parse();
		events.ifPresent(evt -> evt.publish(new TestCommandHandleEvent(player, this, argsObj, true, args)));
	}

	public void handleError(Optional<Player> player, boolean prejoined, String[] args, String message) throws CommandException {
		throw new CommandException(message);
	}

	@Override
	public void handle(Optional<Player> player, boolean prejoined, String... args) throws CommandException {
		this.handle(player, prejoined, args, Optional.empty());
	}

	@Override
	public String getCommandUsage(Optional<Player> player) throws CommandException {
		return "";
	}
}
