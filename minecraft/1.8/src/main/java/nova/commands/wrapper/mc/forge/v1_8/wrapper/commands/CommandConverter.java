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

package nova.commands.wrapper.mc.forge.v1_8.wrapper.commands;

import com.google.common.collect.HashBiMap;
import net.minecraft.command.ICommand;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import nova.commands.Command;
import nova.commands.CommandManager;
import nova.commands.event.CommandEvent;
import nova.commands.wrapper.mc.forge.v1_8.wrapper.commands.backward.BWCommand;
import nova.commands.wrapper.mc.forge.v1_8.wrapper.commands.forward.FWCommand;
import nova.core.nativewrapper.NativeConverter;
import nova.core.wrapper.mc.forge.v18.launcher.ForgeLoadable;
import nova.internal.core.Game;

/**
 * @author ExE Boss
 */
public class CommandConverter implements NativeConverter<Command, ICommand>, ForgeLoadable {

	/**
	 * A map of all commands registered
	 */
	private final HashBiMap<Command, ICommand> map = HashBiMap.create();

	@Override
	public Class<Command> getNovaSide() {
		return Command.class;
	}

	@Override
	public Class<ICommand> getNativeSide() {
		return ICommand.class;
	}

	@Override
	public Command toNova(ICommand command) {
		if (command == null) {
			return null;
		}

		if (command instanceof FWCommand) {
			return ((FWCommand) command).wrapped;
		} else {
			if (map.containsValue(command)) {
				return map.inverse().get(command);
			} else {
				BWCommand wrapper = new BWCommand(command);
				map.put(wrapper, command);
				return wrapper;
			}
		}
	}

	@Override
	public ICommand toNative(Command command) {
		if (command == null) {
			return null;
		}

		if (command instanceof BWCommand) {
			return ((BWCommand) command).wrapped;
		} else {
			if (map.containsKey(command)) {
				return map.get(command);
			} else {
				FWCommand wrapper = new FWCommand(command);
				map.put(command, wrapper);
				return wrapper;
			}
		}
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		//NOTE: There should NEVER be command already registered in preInit() stage of a NativeConverter.
		Game.events().on(CommandEvent.Register.class).bind(evt -> registerNovaCommand(evt.command));
	}

	public void registerMinecraftCommand(ICommand command) {
		if (map.containsValue(command))
			return;

		BWCommand wrapper = new BWCommand(command);
		map.put(wrapper, command);
	}

	private void registerNovaCommand(Command command) {
		if (map.containsKey(command))
			return;

		FWCommand wrapper = new FWCommand(command);
		map.put(command, wrapper);
	}
}
