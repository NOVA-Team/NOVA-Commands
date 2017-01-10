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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Spliterator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * A class containing arguments.
 *
 * @author ExE Boss, Victorious3
 */
public final class Args implements Iterable<Object> {
	public static final Args EMPTY_ARGS = new Args();

	private final Object[] requiredArgs;
	private final Map<String, Object> optionalArgsString;
	private final Map<Character, Object> optionalArgsChar;

	/**
	 * Constructs a new empty Args object.
	 */
	public Args() {
		this.requiredArgs = new Object[0];
		this.optionalArgsString = Collections.emptyMap();
		this.optionalArgsChar = Collections.emptyMap();
	}

	/**
	 * Constructs a new Args object for the following indexed arguments.
	 *
	 * @param requiredArgs The required arguments. Requested using {@link #get(int)}.
	 */
	public Args(Object... requiredArgs) {
		this.requiredArgs = Arrays.copyOf(requiredArgs, requiredArgs.length);
		this.optionalArgsString = Collections.emptyMap();
		this.optionalArgsChar = Collections.emptyMap();
	}

	/**
	 * Constructs a new Args object for the following indexed and named arguments.
	 *
	 * @param requiredArgs The required arguments. Requested using {@link #get(int)}.
	 * @param optionalArgsString The optional arguments. Requested using {@link #get(String)}.
	 * @param optionalArgsChar The optional arguments. Requested using {@link #get(char)}.
	 */
	public Args(Object[] requiredArgs, Map<String, Object> optionalArgsString, Map<Character, Object> optionalArgsChar) {
		this.requiredArgs = Arrays.copyOf(requiredArgs, requiredArgs.length);
		this.optionalArgsString = new HashMap<>();
		this.optionalArgsString.putAll(optionalArgsString);
		this.optionalArgsChar = new HashMap<>();
		this.optionalArgsChar.putAll(optionalArgsChar);
	}

	/**
	 * Get the argument at the specified index.
	 *
	 * @param <T> The type.
	 * @param index The index.
	 * @return The argument.
	 */
	@SuppressWarnings("unchecked")
	public <T> T get(int index) {
		return (T) requiredArgs[index];
	}

	/**
	 * Get the named argument at the specified index.
	 *
	 * @param <T> The type.
	 * @param index The index.
	 * @return The argument.
	 */
	@SuppressWarnings("unchecked")
	public <T> Optional<T> get(String index) {
		return optionalArgsString.containsKey(index) ? Optional.of((T) optionalArgsString.get(index)) : Optional.empty();
	}

	/**
	 * Get the named argument at the specified index.
	 *
	 * @param <T> The type.
	 * @param index The index.
	 * @return The argument.
	 */
	@SuppressWarnings("unchecked")
	public <T> Optional<T> get(char index) {
		return optionalArgsChar.containsKey(index) ? Optional.of((T) optionalArgsChar.get(index)) : Optional.empty();
	}

	/**
	 * Get the size of the indexed arguments.
	 *
	 * @return The size of the indexed arguments.
	 */
	public int size() {
		return requiredArgs.length;
	}

	/**
	 * Get an iterator over the indexed arguments.
	 *
	 * @return An iterator over the indexed arguments.
	 */
	@Override
	public Iterator<Object> iterator() {
		return new Iterator<Object>() {
			int index = 0;

			@Override
			public boolean hasNext() {
				return index < requiredArgs.length;
			}

			@Override
			public Object next() {
				return requiredArgs[index++];
			}
		};
	}

	/**
	 * Get a spliterator over the indexed arguments.
	 *
	 * @return A spliterator over the indexed arguments.
	 */
	@Override
	public Spliterator<Object> spliterator() {
		return Arrays.spliterator(requiredArgs);
	}

	/**
	 * Get a stream of the indexed arguments.
	 *
	 * @return A stream of the indexed arguments.
	 */
	public Stream<Object> stream() {
		return StreamSupport.stream(spliterator(), false);
	}

	/**
	 * Get a parallel stream of the indexed arguments.
	 *
	 * @return A stream of the indexed arguments.
	 */
	public Stream<Object> parallelStream() {
		return StreamSupport.stream(spliterator(), true);
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 97 * hash + Arrays.deepHashCode(this.requiredArgs);
		hash = 97 * hash + Objects.hashCode(this.optionalArgsString);
		hash = 97 * hash + Objects.hashCode(this.optionalArgsChar);
		return hash;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) return true;
		if (other == null || getClass() != other.getClass()) return false;
		return Objects.deepEquals(this.requiredArgs, ((Args) other).requiredArgs) &&
				Objects.deepEquals(this.optionalArgsString, ((Args) other).optionalArgsString) &&
				Objects.deepEquals(this.optionalArgsChar, ((Args) other).optionalArgsChar);
	}

	/**
	 * Attempts to reproduce the original String.
	 * However due to the way things are converted,
	 * this will usually not produce the original arguments.
	 *
	 * @return The arguments as a String.
	 */
	@Override
	public String toString() {
		List<String> str = stream().map(Objects::toString).collect(Collectors.toCollection(LinkedList::new));
		str.addAll(optionalArgsString.entrySet().stream()
			.map(entry -> new StringBuilder("--").append(entry.getKey()).append('=').append(entry.getValue()).toString())
			.collect(Collectors.toCollection(LinkedList::new)));
		str.addAll(optionalArgsChar.entrySet().stream()
			.filter(entry -> !optionalArgsString.containsValue(entry.getValue()))
			.map(entry -> new StringBuilder("-").append(entry.getKey()).append(' ').append(entry.getValue()).toString())
			.collect(Collectors.toCollection(LinkedList::new)));
		return String.join(" ", str.toArray(new String[str.size()]));
	}
}
