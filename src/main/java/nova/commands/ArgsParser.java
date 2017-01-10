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

import nova.commands.exception.CommandParseException;
import nova.core.retention.Data;
import nova.core.util.EnumSelector;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A parser for GNU-style arguments.
 *
 * @author ExE Boss, Victorious3
 */
public final class ArgsParser {

	private static final Map<Predicate<Class<?>>, Function<String, ?>> CONVERTERS = new HashMap<>();

	private final String[] args;
	private final List<Object[]> optional = new ArrayList<>();
	private final boolean prejoined;
	private Class<?>[] required = new Class<?>[0];
	private Args parsed;

	/**
	 * Create an ArgParser for the following arguments.
	 *
	 * @param prejoined If strings in the {@code args} parameter can contain spaces and "/' should not be treated as delimiters.
	 * @param args The string arguments.
	 */
	public ArgsParser(boolean prejoined, String... args) {
		this(prejoined, (Object[]) args);
	}

	/**
	 * Create an ArgParser for the following arguments.
	 *
	 * @param prejoined If strings in the {@code args} parameter can contain spaces and "/' should not be treated as delimiters.
	 * @param args The string arguments.
	 */
	public ArgsParser(boolean prejoined, Object... args) {
		this.args = Arrays.stream(args).map(Objects::toString).toArray(String[]::new);
		this.prejoined = prejoined;
	}

	/**
	 * Create an ArgParser for the following arguments.
	 *
	 * @param args The pre-joined string arguments (can contain spaces and "/' should not be treated as delimiters).
	 */
	public ArgsParser(String... args) {
		this(false, args);
	}

	/**
	 * Create an ArgParser for the following arguments.
	 *
	 * @param args The pre-joined string arguments (can contain spaces and "/' should not be treated as delimiters).
	 */
	public ArgsParser(Object... args) {
		this(false, args);
	}

	private void checkWritable() {
		if (parsed != null)
			throw new IllegalStateException("No edits are allowed after ArgParser has been locked.");
	}

	/**
	 * Specify the required classes.
	 *
	 * @param required The required classes.
	 * @return this
	 */
	public ArgsParser args(Class<?>... required) {
		checkWritable();
		Arrays.stream(required).forEach(Objects::requireNonNull);
		int i = this.required.length;
		this.required = Arrays.copyOf(this.required, this.required.length + required.length);
		System.arraycopy(required, 0, this.required, i, required.length);
		return this;
	}

	/**
	 * Specify an optional parameter.
	 *
	 * @param type The class of the argument type
	 * @param shortName The short name (-shortName), should be in lower-case.
	 * @param longName The long name (--longName), should be in lower-hyphen-case.
	 * @return this
	 */
	public ArgsParser opt(Class<?> type, char shortName, String longName) {
		checkWritable();
		optional.add(new Object[]{type, Character.toLowerCase(shortName), longName == null ? longName : longName.toLowerCase()});
		return this;
	}

	/**
	 * Specify an optional parameter.
	 *
	 * @param type The class of the argument type
	 * @param longName The long name (--longName), should be in lower-hyphen-case.
	 * @return this
	 */
	public ArgsParser opt(Class<?> type, String longName) {
		return this.opt(type, '\u0000', longName);
	}

	/**
	 * Specify an optional parameter.
	 *
	 * @param type The class of the argument type
	 * @param shortName The short name (-shortName)
	 * @return this
	 */
	public ArgsParser opt(Class<?> type, char shortName) {
		return this.opt(type, shortName, null);
	}

	/**
	 * Specify an optional flag.
	 *
	 * @param shortName The short name (-shortName)
	 * @param longName The long name (--longName), should be in lower-hyphen-case.
	 * @return this
	 */
	public ArgsParser opt(char shortName, String longName) {
		return this.opt(null, shortName, longName);
	}

	/**
	 * Specify an optional flag.
	 *
	 * @param longName The long name (--longName), should be in lower-hyphen-case.
	 * @return this
	 */
	public ArgsParser opt(String longName) {
		return this.opt('\u0000', longName);
	}

	/**
	 * Specify an optional flag.
	 *
	 * @param shortName The short name (-shortName)
	 * @return this
	 */
	public ArgsParser opt(char shortName) {
		return this.opt(shortName, null);
	}

	/**
	 * Parse the supplied arguments.
	 * After this method is called, no more edits are allowed.
	 *
	 * @return The parsed {@link Args} instance.
	 * @throws CommandParseException The {@link #args(java.lang.Class...)}
	 * hasn't been called and the constructor arguments parameter isn't empty.
	 */
	public Args parse() throws CommandParseException {
		if (parsed != null)
			return parsed;

		if (args.length == 0) { // Args length is zero. It doesn't matter that we didn't specify required arguments.
			parsed = Args.EMPTY_ARGS;
			return parsed;
		}

		if (required.length == 0)
			throw new CommandParseException("Errors with command", Errors.UNSPECIFIED_REQUIRED_ARGUMENTS);

		int requiredPos = 0;
		boolean continous = false;
		boolean optional = false;
		Delimiters continousDelimiter = null;
		int curvyBracketsStack = 0;
		StringBuilder arg = new StringBuilder();
		String currentOpt = null;
		char currentOptC = '\u0000';
		Class<?> currentClass = null;
		Object[] required = new Object[this.required.length];
		Map<Character, Object> optsChar = new HashMap<>();
		Map<String, Object> optsStr = new HashMap<>();

		EnumSelector<Errors> errored = EnumSelector.of(Errors.class).blockAll();

		for (String stringArg : args) {
			if (stringArg.isEmpty())
				continue;

			int substringIndexStart = 0;
			int substringIndexEnd = 0;
			if (!this.prejoined) {
				if (stringArg.charAt(0) == '{')
					curvyBracketsStack++;

				if (!continous) {
					switch (stringArg.charAt(0)) {
						case '\'': {
							continous = true;
							substringIndexStart = 1;
							continousDelimiter = Delimiters.SINGLE_QUOTES;
							break;
						} case '"': {
							continous = true;
							substringIndexStart = 1;
							continousDelimiter = Delimiters.DOUBLE_QUOTES;
							break;
						} case '{': {
							continous = true;
							curvyBracketsStack = 1;
							substringIndexStart = 0;
							continousDelimiter = Delimiters.CURVY_BRACKETS;
							break;
						}
					}
				}

				if (continous) {
					// TODO: Better escaping
					switch (stringArg.charAt(stringArg.length() - 1)) {
						case '\'': {
							if (continousDelimiter != Delimiters.SINGLE_QUOTES)
								break;
							continous = false;
							substringIndexEnd = 1;
							continousDelimiter = null;
							break;
						} case '"': {
							if (continousDelimiter != Delimiters.DOUBLE_QUOTES)
								break;
							continous = false;
							substringIndexEnd = 1;
							continousDelimiter = null;
							break;
						} case '}': {
							curvyBracketsStack--;
							if (continousDelimiter != Delimiters.CURVY_BRACKETS || curvyBracketsStack != 0)
								break;
							continous = false;
							substringIndexEnd = 0;
							continousDelimiter = null;
							break;
						}
					}

					if (!optional && !continous) {
						if (arg.length() > 0) arg.append(' ');
						arg.append(stringArg.substring(substringIndexStart, stringArg.length() - substringIndexEnd));
						optional = false;
					}
				}
			}

			if (optional || continous) {
				if (arg.length() > 0) arg.append(' ');
				arg.append(stringArg.substring(substringIndexStart, stringArg.length() - substringIndexEnd));
				if (!continous) optional = false;
			} else if (stringArg.startsWith("-")) {
				// Argument is optional
				if (stringArg.startsWith("--")) {
					currentOpt = stringArg.substring(2);
					final String optName = currentOpt.substring(0, currentOpt.contains("=") ? currentOpt.indexOf('=') : currentOpt.length());
					Optional<Object[]> dataOp = this.optional.stream().filter(ary -> optName.equalsIgnoreCase((String)ary[2])).findFirst();
					if (!dataOp.isPresent()) {
						errored.apart(Errors.UNKNOWN_OPTIONAL);
						continue;
					}
					arg.append(currentOpt.contains("=") ? currentOpt.substring(currentOpt.indexOf('=') + 1) : "");
					Object[] data = dataOp.get();
					if (data[1] != null) currentOptC = (char)data[1];
					if (data[0] != null) {
						currentClass = (Class<?>) data[0];
						if (arg.length() == 0) optional = true;
					} else {
						currentClass = boolean.class;
						arg.append(true);
					}
					if (arg.length() > 0 && !this.prejoined && (arg.charAt(0) == '"' || arg.charAt(0) == '\'') && !continous) {
						continous = true;
					}
				} else {
					final String optName = stringArg.substring(1);
					Optional<Object[]> dataOp = this.optional.stream().filter(ary -> optName.equalsIgnoreCase(new String(new char[]{(char)ary[1]}))).findFirst();
					if (!dataOp.isPresent()) {
						errored.apart(Errors.UNKNOWN_OPTIONAL);
						continue;
					}
					currentOptC = optName.charAt(0);
					Object[] data = dataOp.get();
					if (data[2] != null) currentOpt = (String)data[2];
					if (data[0] != null) {
						currentClass = (Class<?>) data[0];
						optional = true;
					} else {
						currentClass = boolean.class;
						arg.append(true);
					}
				}
			} else {
				if (requiredPos >= this.required.length) {
					requiredPos++;
					errored.apart(Errors.TOO_LONG); // TODO: Maybe ignore this
					continue;
				}
				Class<?> requiredClass = this.required[requiredPos];
				if (arg.length() > 0) {
					required[requiredPos] = toArg(requiredClass, arg.toString());
				} else {
					required[requiredPos] = toArg(requiredClass, stringArg);
				}
				requiredPos++;
			}
			if (!optional && !continous && currentClass != null && arg.length() > 0) {
				Object argObj = toArg(currentClass, arg.toString());
				if (currentOpt != null) optsStr.put(currentOpt, argObj);
				if (currentOptC != '\u0000') optsChar.put(currentOptC, argObj);

				currentOpt = null;
				currentOptC = '\u0000';
				currentClass = null;

				arg.delete(0, arg.length());
			}
		}

		if (continous)
			errored.apart(Errors.UNENDED_QUOTES);
		if (curvyBracketsStack > 0)
			errored.apart(Errors.UNENDED_CURVY_BRACKETS);

		errored.lock();

		if (!errored.blocksAll())
			throw new CommandParseException("Errors with command", errored);

		parsed = new Args(required, optsStr, optsChar);
		return parsed;
	}

	@SuppressWarnings({"unchecked", "UnnecessaryBoxing"})
	private static <T extends Object> T toArg(final Class<?> clazz, final String str) {
		if (Number.class.isAssignableFrom(clazz) || (clazz.isPrimitive() && clazz != boolean.class && clazz != char.class)) {
			if (clazz == Byte.class || clazz == byte.class)
				return (T) Byte.valueOf(str);
			if (clazz == Short.class || clazz == short.class)
				return (T) Short.valueOf(str);
			if (clazz == Integer.class || clazz == int.class) {
				try {
					return (T) Integer.valueOf(str);
				} catch (NumberFormatException ex) {
					return (T) Integer.valueOf(Integer.parseUnsignedInt(str));
				}
			} if (clazz == Long.class || clazz == long.class) {
				try {
					return (T) Long.valueOf(str);
				} catch (NumberFormatException ex) {
					return (T) Long.valueOf(Long.parseUnsignedLong(str));
				}
			}

			if (clazz == Float.class || clazz == float.class)
				return (T) Float.valueOf(str);
			if (clazz == Double.class || clazz == double.class)
				return (T) Double.valueOf(str);

			if (clazz == BigInteger.class)
				return (T) new BigInteger(str);
			if (clazz == BigDecimal.class)
				return (T) new BigDecimal(str);
		} else {
			if (clazz == String.class)
				return (T) str;
			if (clazz == Character.class || clazz == char.class) {
				if (str.isEmpty() || str.length() > 1) throw new IllegalArgumentException("Argument \"" + str + "\" is too " + (str.isEmpty() ? "short" : "long") + ", it must be exactly 1 character long.");
				return (T) Character.valueOf(str.charAt(0));
			} if (clazz == Boolean.class || clazz == boolean.class)
				return (T) Boolean.valueOf(str);
			if (clazz == Data.class)
				return (T) Data.fromJSON(str);
		}

		Optional<Function<String, ?>> converter = CONVERTERS.entrySet().stream().filter(entry -> entry.getKey().test(clazz)).map(Entry::getValue).findFirst();
		if (converter.isPresent())
			return (T) converter.get().apply(str);

		throw new IllegalArgumentException("Object type " + clazz.getSimpleName() + " is not supported");
	}

	/**
	 * Register an argument converter.
	 *
	 * @param <T> The return type.
	 * @param predicate The checker ({@code clazz -> clazz == SomeClass.class}).
	 * @param converter The converter ({@code SomeClass::parseString}) or something similar.
	 */
	public static <T> void registerArgConverter(Predicate<Class<?>> predicate, Function<String, T> converter) {
		CONVERTERS.put(Objects.requireNonNull(predicate), Objects.requireNonNull(converter));
	}

	/**
	 * Check if the ArgsParser instance has been locked.
	 *
	 * @return The locked status.
	 */
	public boolean locked() {
		return parsed != null;
	}

	public static enum Errors {
		PARSER_IS_LOCKED("Parser is locked"),
		UNSPECIFIED_REQUIRED_ARGUMENTS("Required arguments unspecified"),
		TOO_LONG("Arguments too long"),
		UNENDED_QUOTES("Unended quotes"),
		UNENDED_CURVY_BRACKETS("Unended curvy brackets"),
		UNKNOWN_OPTIONAL("Unknown optional argument");

		private final String displayName;

		private Errors(String displayName) {
			this.displayName = displayName;
		}

		@Override
		public String toString() {
			return this.displayName;
		}
	}

	private static enum Delimiters {
		SINGLE_QUOTES,
		DOUBLE_QUOTES,
		CURVY_BRACKETS
	}
}
