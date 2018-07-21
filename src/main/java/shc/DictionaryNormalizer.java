/*
 * selected-historical-ciphers
 * 
 * Copyright (c) 2018, Milten Plescott. All rights reserved.
 * 
 * SPDX-License-Identifier:    BSD-3-Clause
 */

package shc;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import javax.swing.JOptionPane;

public class DictionaryNormalizer {

	protected enum Case {
		LOWER, UPPER
	}
	private static Map<Character, Character> map; // for converting J,V,W,j,v,w to I,U,null,i,u,null
	private static final String ASCII = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

	public static List<String> normalize(File file, Case letterCase) {
		initMap();
		List<String> words = new ArrayList<>();

		try (Scanner scanner = new Scanner(file);) {

			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				String[] splits = line.split("'");

				switch (letterCase) {
					case LOWER:
						for (String str : splits) {
							words.add(normalize(str).toLowerCase());
						}
						break;
					default:
					case UPPER:
						for (String str : splits) {
							words.add(normalize(str).toUpperCase());
						}
						break;
				}
			}

		}
		catch (FileNotFoundException ex) {
			JOptionPane.showMessageDialog(null,
				"<html><span style='font-size:18pt'>" + "Error while opening file:" + "<br>" + file.getAbsolutePath(),
				"Icon Error",
				JOptionPane.ERROR_MESSAGE);
		}

		Collections.sort(words);
		words = removeDuplicatesFromSortedList(words);

		if (words.size() > 0) {
			while (words.get(0).trim().length() == 0) {
				words.remove(0);
			}
		}

		return words; // assumes words list is alphabetically sorted
	}

	// normalizes word for FERSEN cipher usage
	protected static String normalize(String word) {
		String normalizedWord = removeAccents(word);
		normalizedWord = removeSymbols(normalizedWord);
		normalizedWord = convertUncipherableLetters(normalizedWord);
		return normalizedWord;
	}

	// removes accents from letters
	protected static String removeAccents(String accented) {
		return Normalizer.normalize(accented, Normalizer.Form.NFD)
			.replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
			.replace("\u0152", "OE")
			.replace("\u0153", "oe");
	}

	// deletes everything other than letters
	// also removes whitespace and digits
	protected static String removeSymbols(String word) {
		StringBuilder symbolessWord = new StringBuilder(word.length());

		for (char c : word.toCharArray()) {

			if (ASCII.indexOf(c) != -1) { // c is in ASCII
				symbolessWord.append(c);
			}
		}

		return symbolessWord.toString();
	}

	// converts j,v,J,V to i,u,I,U and deletes w,W
	protected static String convertUncipherableLetters(String str) {
		StringBuilder word = new StringBuilder(str.length());

		for (char c : str.toCharArray()) {
			if (map.containsKey(c)) {
				if (map.get(c) != null) {
					word.append(map.get(c));
				}
			}
			else {
				word.append(c);
			}
		}

		return word.toString();
	}

	// assumes that the list is alphabetically sorted
	protected static List<String> removeDuplicatesFromSortedList(List<String> list) {
		for (int i = 0; i < list.size() - 1; i++) {
			if (list.get(i).equals(list.get(i + 1))) {
				list.remove(i);
				i--;
			}
		}
		return list;
	}

	protected static void initMap() {
		map = new HashMap<>();
		map.put('j', 'i');
		map.put('J', 'I');
		map.put('v', 'u');
		map.put('V', 'U');
		map.put('w', null);
		map.put('W', null);
	}

}
