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

package nova.commands.exception;

import nova.core.util.exception.NovaException;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author ExE Boss
 */
public class CommandException extends NovaException {

	private static final long serialVersionUID = 1L;
	private final Object[] arguments;

	public CommandException() {
		this.arguments = null;
	}

	public CommandException(String message, Object... parameters) {
		super(message, parameters);
		this.arguments = Arrays.copyOf(parameters, parameters.length);
	}

	public CommandException(String message) {
		super(message);
		this.arguments = null;
	}

	public CommandException(String message, Throwable cause) {
		super(message, cause);
		this.arguments = null;
	}

	public CommandException(String message, Throwable cause, Object... parameters) {
		super(message, parameters);
		this.initCause(cause);
		this.arguments = Arrays.copyOf(parameters, parameters.length);
	}

	public CommandException(Throwable cause) {
		super(cause);
		this.arguments = null;
	}

	public Optional<Object[]> getArguments() {
		return Optional.ofNullable(Arrays.copyOf(arguments, arguments.length));
	}
}
