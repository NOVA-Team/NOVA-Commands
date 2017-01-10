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

import nova.commands.event.CommandEvent;
import nova.core.entity.component.Player;

import java.util.Optional;

/**
 * @author ExE Boss
 */
public class TestCommandHandleEvent extends CommandEvent.CommandHandleEvent {

	public TestCommandHandleEvent(Optional<Player> player, Command command, Args argsObj, boolean prejoined, String... args) {
		super(player, command, argsObj, prejoined, args);
	}

	public TestCommandHandleEvent(Optional<Player> player, Command command, boolean prejoined, String... args) {
		super(player, command, prejoined, args);
	}
}
