/*
 * selected-historical-ciphers
 * 
 * Copyright (c) 2018, Milten Plescott. All rights reserved.
 * 
 * SPDX-License-Identifier:    BSD-3-Clause
 */

package shc.fersen.attack;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

public class HelperTest {

	private static final String textWhole = "RENONPUNREYUUL";
	private static final String expectedOdd = "RNNURYU";
	private static final String expectedEven = "EOPNEUL";

	private static final boolean T = true;
	private static final boolean F = false;

	/*
	@BeforeClass
	public static void setUpClass() {
	}

	@Before
	public void setUp() {
	}
	 */
//	@Test
//	public void testGetBruteForceLengthLoops() {
//		Random rnd = new Random(System.currentTimeMillis());
//		for (int i = 0; i < 100; i++) {
//			int maxBruteForceLength = 1 + rnd.nextInt(10); // <1, 10>
//			int pswdLength = 1 + rnd.nextInt(20); // <1, 30>
//			List<Integer> listActual = Helper.getBruteForceLengthLoops(pswdLength, maxBruteForceLength);
//			assertEquals(pswdLength, sum(listActual));
//			if (listActual.size() > 2) {
//				listActual.remove(listActual.size() - 1);
//				for (int actual : listActual) {
//					assertEquals(maxBruteForceLength, actual);
//				}
//			}
//		}
//	}
	@Test
	public void testGetEveryOtherOddChar() {
		String actualOdd = Helper.getEveryOtherChar(textWhole, EveryOtherCharacter.ODD);
		assertEquals(expectedOdd, actualOdd);
	}

	@Test
	public void testGetEveryOtherEvenChar() {
		String actualEven = Helper.getEveryOtherChar(textWhole, EveryOtherCharacter.EVEN);
		assertEquals(expectedEven, actualEven);
	}

	@Test
	public void testStringMerging() {
		String expected = "One String empty, the other not should return the one that is not empty";
		assertEquals(expected, Helper.mergeStrings("", expected));
		assertEquals(expected, Helper.mergeStrings(expected, ""));

		assertEquals("", Helper.mergeStrings("", ""));
		assertEquals("abcdef", Helper.mergeStrings("ace", "bdf"));

		assertEquals("abcde", Helper.mergeStrings("ac", "bde"));
		assertEquals("abcde", Helper.mergeStrings("ace", "bd"));

		assertEquals("abcdefgh", Helper.mergeStrings("ace", "bdfgh"));
		assertEquals("abcdefgh", Helper.mergeStrings("acegh", "bdf"));
	}

	public int sum(List<Integer> list) {
		int sum = 0;
		for (int i : list) {
			sum += i;
		}
		return sum;
	}

	@Test
	public void testStartEndIndices() {
		int maxBruteForceLength = 3;

		List<Boolean> mask_1 = Arrays.asList(T, T, T, T, F, T, F, T, T, F, T, T, F);
		int currentPos_1 = 2;
		int[] expected_1 = {0, 12};

		List<Boolean> mask_2 = Arrays.asList(F, T, T, T);
		int currentPos_2 = 0;
		int[] expected_2 = {0, 4};

		List<Boolean> mask_3 = Arrays.asList(F, F, F, F, T, F, F);
		int currentPos_3 = 1;
		int[] expected_3 = {1, 5};

		//assertArrayEquals(expected_1, FersenAttackHelper.getStartEndIndices(mask_1, maxBruteForceLength, currentPos_1));
		//assertArrayEquals(expected_2, FersenAttackHelper.getStartEndIndices(mask_2, maxBruteForceLength, currentPos_2));
		//assertArrayEquals(expected_3, FersenAttackHelper.getStartEndIndices(mask_3, maxBruteForceLength, currentPos_3));
	}

}
