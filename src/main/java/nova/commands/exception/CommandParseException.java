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

import nova.commands.ArgsParser;
import nova.core.util.EnumSelector;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

/**
 * @author ExE Boss
 */
public class CommandParseException extends CommandException {

	private static final long serialVersionUID = 1L;

	private final Set<ArgsParser.Errors> errors;

	public CommandParseException(String message) {
		super(message);
		this.errors = null;
	}

	public CommandParseException(String message, Throwable cause) {
		super(message, cause);
		this.errors = null;
	}

	private CommandParseException(String message, Set<ArgsParser.Errors> errors) {
		super(message + ": " + errors);
		this.errors = errors;
	}

	public CommandParseException(String message, ArgsParser.Errors... errors) {
		this(message, toEnumSet(errors));
	}

	public CommandParseException(String message, EnumSet<ArgsParser.Errors> errors) {
		this(message, Collections.unmodifiableSet(errors));
	}

	public CommandParseException(String message, EnumSelector<ArgsParser.Errors> errors) {
		this(message, errors.toSet());
	}

	public Optional<Set<ArgsParser.Errors>> getErrors() {
		return Optional.ofNullable(errors);
	}

	private static EnumSet<ArgsParser.Errors> toEnumSet(ArgsParser.Errors... errors) {
		EnumSet<ArgsParser.Errors> set = EnumSet.noneOf(ArgsParser.Errors.class);
		set.addAll(Arrays.asList(errors));
		return set;
	}
}
