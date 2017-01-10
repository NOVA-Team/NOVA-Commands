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

import nova.core.retention.Data;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Iterator;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author ExE Boss
 */
public class ArgsParserTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testArgsParser() {
		ArgsParser parser1 = new ArgsParser("String", true, 1, 2, 3, 4, 5.0, 6.0, "-a", "--fun")
				.args(String.class, boolean.class, byte.class, short.class, int.class, long.class, float.class, double.class)
				.opt('a').opt("fun");
		ArgsParser parser2 = new ArgsParser("String").args(String.class);
		ArgsParser parser3 = new ArgsParser(false, new Object[]{"String", "--other", "\"a" + "b\"", "-x", "y"})
				.args(String.class).opt(String.class, "other").opt(char.class, 'x');
		ArgsParser parser4 = new ArgsParser('c', Integer.MAX_VALUE * 2L, BigInteger.valueOf(Long.MAX_VALUE).multiply(BigInteger.valueOf(2)), BigInteger.ONE, BigDecimal.TEN)
				.args(char.class, int.class, long.class, BigInteger.class, BigDecimal.class);
		ArgsParser parser5 = new ArgsParser(false, new Object[]{"{\"Key\":", "\"Value\"}"}).args(Data.class);

		assertThat(parser1.parse()).isNotNull();
		assertThat(parser2.parse()).isEqualTo(new Args("String"));
		assertThat(parser3.parse()).isNotNull();
		assertThat(parser4.parse()).isNotNull();
		assertThat(parser5.parse()).isNotNull();

		assertThat(parser1.locked()).isTrue();

		assertThat((String)parser2.parse().get(0)).isEqualTo("String");

		assertThat(parser1.parse().get('a')).isPresent();
		assertThat(parser1.parse().get('a').get()).isEqualTo(true);

		assertThat(parser1.parse().get("fun")).isPresent();
		assertThat(parser1.parse().get("fun").get()).isEqualTo(true);

		assertThat(parser2.parse().get("a")).isEmpty();
		assertThat(parser2.parse().get('a')).isEmpty();

		Iterator<Object> iterator = parser2.parse().iterator();
		assertThat(iterator.hasNext()).isTrue();
		assertThat(iterator.next()).isEqualTo("String");
		assertThat(iterator.hasNext()).isFalse();
	}

	@Test
	public void testCannotWrite() {
		ArgsParser parser = new ArgsParser("String").args(String.class);
		parser.parse();

		thrown.expect(IllegalStateException.class);
		thrown.expectMessage("No edits are allowed after ArgParser has been locked.");
		parser.args(String.class);
	}
}
