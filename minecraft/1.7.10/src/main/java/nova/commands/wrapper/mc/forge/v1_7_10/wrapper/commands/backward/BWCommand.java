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

package nova.commands.wrapper.mc.forge.v1_7_10.wrapper.commands.backward;

import net.minecraft.command.ICommand;
import nova.commands.Command;
import nova.commands.exception.CommandException;
import nova.core.entity.component.Player;
import nova.core.wrapper.mc.forge.v17.util.WrapUtility;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.util.List;
import java.util.Optional;

/**
 * @author ExE Boss
 */
public class BWCommand extends Command {

	public final ICommand wrapped;

	public BWCommand(ICommand wrapped) {
		super(wrapped.getCommandName());
		this.wrapped = wrapped;
	}

	@Override
	public void handle(Optional<Player> player, boolean prejoined, String... args) throws CommandException {
		try {
			this.wrapped.processCommand(WrapUtility.getMCPlayer(player), args);
		} catch (net.minecraft.command.CommandException ex) {
			throw new CommandException(ex.getMessage(), ex, ex.getErrorOjbects());
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<String> getAutocompleteList(Optional<Player> player, boolean prejoined, String[] args, Optional<Vector3D> pos) {
		return this.wrapped.addTabCompletionOptions(WrapUtility.getMCPlayer(player), args);
	}

	@Override
	public String getCommandUsage(Optional<Player> player) {
		return wrapped.getCommandUsage(WrapUtility.getMCPlayer(player));
	}
}
