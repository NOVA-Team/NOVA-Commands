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

import nova.commands.TestCommand;
import nova.commands.TestCommandHandleEvent;
import nova.core.event.bus.Event;
import nova.core.event.bus.EventBus;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static nova.testutils.NovaAssertions.assertThat;

/**
 *
 * @author ExE Boss
 */
public class CommandEventTest {

	public TestCommand command;
	public EventBus<Event> events;

	@Before
	public void setUp() {
		events = new EventBus<>();
		command = new TestCommand();
	}

	@Test
	public void testCommandRegister() {
		events.on(CommandEvent.Register.class).bind(evt -> {
			assertThat(evt.command).isEqualTo(command);
		});
		events.publish(new CommandEvent.Register(command));
	}
}
