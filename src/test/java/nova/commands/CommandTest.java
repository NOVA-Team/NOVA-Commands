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
import nova.core.event.bus.Event;
import nova.core.event.bus.EventBus;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static nova.testutils.NovaAssertions.assertThat;

/**
 * Used to test {@link Command commands}.
 *
 * @author ExE Boss
 */
public class CommandTest {

	public TestCommand command;

	@Before
	public void setUp() {
		command = new TestCommand("Test_Command");
	}

	@Test
	public void testHandle() {
		EventBus<Event> events = new EventBus<>();
		Args args = new ArgsParser("test").args(String.class).parse();
		events.on(TestCommandHandleEvent.class).bind(evt -> {
			assertThat(evt.player).isNull();
			assertThat(evt.command.getCommandName()).isEqualTo(command.getCommandName());
			assertThat(evt.argsObj).isEqualTo(Optional.of(args));
		});
		command.handle(null, true, new String[]{"test"}, Optional.of(events));
	}

	@Test(expected = CommandException.class)
	public void testHandleException() {
		command.handleError(null, true, new String[0], "Message");
	}

	@Test
	public void testGetAutocompleteList() {
		assertThat(command.getAutocompleteList(Optional.empty(), true, new String[0], Optional.empty())).isEmpty();
	}

	@Test
	public void testGetCommandName() {
		assertThat(command.getCommandName()).isEqualTo("Test_Command");
	}

	@Test
	public void testGetID() {
		assertThat(command.getID()).isEqualTo("test_command");
	}
}
