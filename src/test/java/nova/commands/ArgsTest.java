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

import org.junit.Before;
import org.junit.Test;

import static nova.testutils.NovaAssertions.assertThat;

/**
 * Used to test {@link Args}
 *
 * @author ExE Boss
 */
public class ArgsTest {

	public Args args0;
	public Args args1;
	public Args args2;
	public Args args3;
	public Args args4;

	@Before
	public void setUp() {
		this.args0 = new ArgsParser().args().parse();
		this.args1 = new ArgsParser("Arg1").args(String.class).parse();
		this.args2 = new ArgsParser("Arg1", "Arg2").args(String.class, String.class).parse();
		this.args3 = new ArgsParser("Arg1", "Arg2", Math.PI).args(String.class, String.class, double.class).parse();
		this.args4 = new ArgsParser("Arg1", "Arg2", Math.PI, Math.E).args(String.class, String.class, double.class, double.class).parse();
	}

	@Test
	public void testSize() {
		assertThat(this.args0.size()).isZero();
		assertThat(this.args1.size()).isEqualTo(1);
		assertThat(this.args2.size()).isEqualTo(2);
		assertThat(this.args3.size()).isEqualTo(3);
		assertThat(this.args4.size()).isEqualTo(4);
	}

	@Test
	public void testIterable() {
		assertThat(this.args0.iterator().hasNext()).isFalse();
		assertThat(this.args1.iterator().hasNext()).isTrue();
		assertThat(this.args2.spliterator().getExactSizeIfKnown()).isEqualTo(2);
		assertThat(this.args3.stream().count()).isEqualTo(3);
		assertThat(this.args4.parallelStream().count()).isEqualTo(4);
	}

	@Test
	public void testEquals() {
		assertThat(args0).isEqualTo(args0);
		assertThat(args2).isNotEqualTo(args3);
		assertThat(args1).isEqualTo(new ArgsParser("Arg1").args(String.class).parse());
		assertThat(args4).isNotEqualTo(null);
		assertThat(args4).isNotEqualTo(this);
	}

	@Test
	public void testHashCode() {
		assertThat(args0.hashCode()).isEqualTo(new ArgsParser().args().parse().hashCode());
		assertThat(args1.hashCode()).isEqualTo(new ArgsParser("Arg1").args(String.class).parse().hashCode());
		assertThat(args2.hashCode()).isEqualTo(new ArgsParser("Arg1", "Arg2").args(String.class, String.class).parse().hashCode());
		assertThat(args3.hashCode()).isEqualTo(new ArgsParser("Arg1", "Arg2", Math.PI).args(String.class, String.class, double.class).parse().hashCode());
		assertThat(args4.hashCode()).isEqualTo(new ArgsParser("Arg1", "Arg2", Math.PI, Math.E).args(String.class, String.class, double.class, double.class).parse().hashCode());
	}

	@Test
	public void testToString() {
		assertThat(args0.toString()).isEmpty();
		assertThat(args1.toString()).isEqualTo("Arg1");
		assertThat(args2.toString()).isEqualTo("Arg1 Arg2");
		assertThat(args3.toString()).isEqualTo("Arg1 Arg2 " + Double.toString(Math.PI));
		assertThat(args4.toString()).isEqualTo("Arg1 Arg2 " + Double.toString(Math.PI) + ' ' + Double.toString(Math.E));
	}
}
