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
package nova.commands.wrapper.mc.forge.v1_8.wrapper.commands.forward;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import nova.commands.ArgsParser;
import nova.commands.Command;
import nova.core.wrapper.mc.forge.v18.util.WrapUtility;
import nova.internal.core.Game;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author ExE Boss
 */
public class FWCommand extends CommandBase {

	public final Command wrapped;

	public FWCommand(Command wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public String getCommandName() {
		return wrapped.getCommandName();
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return wrapped.getCommandUsage(sender instanceof EntityPlayer ? WrapUtility.getNovaPlayer((EntityPlayer) sender) : Optional.empty());
	}

	@Override
	public List<String> getCommandAliases() {
		return Collections.emptyList();
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		try {
			this.wrapped.handle(sender instanceof EntityPlayer ? WrapUtility.getNovaPlayer((EntityPlayer) sender) : Optional.empty(), false, args);
		} catch (nova.commands.exception.CommandException ex) {
			throw (CommandException) new CommandException(ex.getMessage(), ex.getArguments().orElse(new Object[0])).initCause(ex);
		}
	}

	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
		return this.wrapped.getAutocompleteList(sender instanceof EntityPlayer ? WrapUtility.getNovaPlayer((EntityPlayer) sender) : Optional.empty(), false, args, pos == null ? Optional.empty() : Optional.of(Game.natives().toNova(pos)));
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return true;
	}
}
