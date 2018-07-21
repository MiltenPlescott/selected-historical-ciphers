/*
 * selected-historical-ciphers
 * 
 * Copyright (c) 2018, Milten Plescott. All rights reserved.
 * 
 * SPDX-License-Identifier:    BSD-3-Clause
 */

package shc;

//import shc.DictionaryNormalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;

public class DictionaryNormalizerTest {

	// DictionaryNormalizer.java got some changes that were not reflected in this test file yet
	private static final String ASCII_LETTERS_DIGITS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	private static final String ASCII_LETTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final String ACCENTED = "ÁáÀàÂâÄä ÇçČč Ďď ÉéÈèĚěÊêËë ÍíÎîÏï ĹĺĽľ ÓóÔô ŔŕŘř Šš Ťť ÚúÙùÛûÜüŰűŮů ÝýŸÿ Žž Œœ";
	private static final String ACCENTLESS = "AaAaAaAa CcCc Dd EeEeEeEeEe IiIiIi LlLl OoOo RrRr Ss Tt UuUuUuUuUuUu YyYy Zz OEoe";

	@BeforeClass
	public static void setUpClass() {
		DictionaryNormalizer.initMap();
	}

	@Test
	public void removesAccentsFromAccentedLetters() {
		String normalized = DictionaryNormalizer.removeAccents(ACCENTED);

		assertEquals(ACCENTLESS, normalized);
	}

	@Test
	public void removesEverythingButLettersAndDigits() {
		String symbols = ASCII_LETTERS_DIGITS + " !	\"#$%&\\'()*+,-./:;<=>?@[\\\\]^_`{|}~ªµºßæðøþıłƒȼˆαβγδεηθικλμνοπρςστυφωабвгдежзиклмнопрстуфхцчшщъыьюяіњҫⰰⰲⴭ〵いかすってにはよるをァイウエオケシットハリロンㄽ㤹㨰㱴㵲㸴㹴一么二今令作僕初卯取号司吃問寶戦指接揮改敬敮敺是昼晦柮楳機毛没湯潣潬無猫瑢瑩畳癴直眉睷碇籥系統美脛脟脡脿艙芒茂茅莽袩袬觩锚霉題ﬀﬁﬂ \t\n\r";
		String expected = ASCII_LETTERS;

		String normalized = DictionaryNormalizer.removeSymbols(symbols);

		assertEquals(expected, normalized);
	}

	@Test // converts j,v,J,V to i,u,I,U and deletes w,W
	public void convertsLettersNotUsableInFersenCT() {
		String str = ASCII_LETTERS;
		String expected = "abcdefghiiklmnopqrstuuxyzABCDEFGHIIKLMNOPQRSTUUXYZ";

		String normalized = DictionaryNormalizer.convertUncipherableLetters(str);

		assertEquals(expected, normalized);
	}

	@Test
	public void removeDuplicatesRemovesDuplicatesFromSortedList() {
		Random rnd = new Random(System.currentTimeMillis());
		List<String> list = new ArrayList<>(500);

		for (int i = 0; i < 500; i++) {
			list.add(Character.toString((char) (32 + rnd.nextInt(95))));
		}

		Collections.sort(list);
		DictionaryNormalizer.removeDuplicatesFromSortedList(list);

		assertEquals(ACCENTED, new HashSet<>(list).size(), list.size());
	}

}
