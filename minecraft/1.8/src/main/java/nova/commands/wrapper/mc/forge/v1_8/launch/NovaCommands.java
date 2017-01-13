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

package nova.commands.wrapper.mc.forge.v1_8.launch;

import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandManager;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import nova.commands.CommandManager;
import nova.commands.wrapper.mc.forge.v1_8.wrapper.commands.CommandConverter;
import nova.core.event.ServerEvent;
import nova.core.event.bus.GlobalEvents;
import nova.core.loader.Mod;
import nova.core.nativewrapper.NativeManager;
import nova.core.wrapper.mc.forge.v18.launcher.ForgeLoadable;
import nova.internal.commands.depmodules.CommandsModule;

/**
 * @author ExE Boss
 */
@Mod(id = "nova-commands-wrapper", name = "NOVA Commands Wrapper", version = "0.0.1", novaVersion = "0.1.0", modules = { CommandsModule.class })
public class NovaCommands implements ForgeLoadable {

	private final NativeManager natives;
	private final CommandManager commands;
	private final GlobalEvents events;

	private final CommandConverter converter;

	public NovaCommands(CommandManager commandManager, GlobalEvents events, NativeManager natives) {
		this.commands = commandManager;
		this.events = events;
		this.natives = natives;

		this.converter = new CommandConverter();
		this.natives.registerConverter(this.converter);
		this.events.on(ServerEvent.Start.class).bind(this::serverStarting);
	}

	@Override
	public void preInit(FMLPreInitializationEvent evt) {
		this.commands.init();
	}

	@SuppressWarnings("unchecked")
	public void serverStarting(ServerEvent.Start evt) {
		ICommandManager mcManager = MinecraftServer.getServer().getCommandManager();
		mcManager.getCommands().values().stream().filter(cmd -> cmd instanceof ICommand).forEach(cmd -> this.converter.registerMinecraftCommand((ICommand) cmd));
		if (mcManager instanceof CommandHandler) {
			this.commands.registry.forEach(command -> ((CommandHandler) mcManager).registerCommand(this.natives.toNative(command)));
		}
	}
}
