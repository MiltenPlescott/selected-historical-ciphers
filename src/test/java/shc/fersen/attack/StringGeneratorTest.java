/*
 * selected-historical-ciphers
 * 
 * Copyright (c) 2018, Milten Plescott. All rights reserved.
 * 
 * SPDX-License-Identifier:    BSD-3-Clause
 */

package shc.fersen.attack;

import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class StringGeneratorTest {

	private static String alphabet;
	private static int maxLength;

	public StringGeneratorTest() {
	}

	@BeforeClass
	public static void setUpClass() {
		StringBuilder builder = new StringBuilder(26);
		for (int i = 0; i < 26; i++) {
			builder.append(((char) ('A' + i)));
		}
		alphabet = builder.toString();
		maxLength = 4;
	}

	@Test
	public void testGenerate() {
		StringGenerator.generate(alphabet, maxLength);
		for (int i = 1; i <= maxLength; i++) {
			int expected = (int) Math.pow(alphabet.length(), i);

			int actualSet = StringGenerator.getSet(i).size();
			int actualMap = StringGenerator.getMap().get(i).size();

			assertEquals(expected, actualSet);
			assertEquals(expected, actualMap);
		}
	}

}
