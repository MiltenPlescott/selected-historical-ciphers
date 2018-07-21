/*
 * selected-historical-ciphers
 * 
 * Copyright (c) 2018, Milten Plescott. All rights reserved.
 * 
 * SPDX-License-Identifier:    BSD-3-Clause
 */

package shc.fersen.attack;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class StringGenerator {

	private static String alphabet;
	private static int maxLength; // inclusive
	private static Set<String> tmpSet;
	private static Map<Integer, Set<String>> map; // keys: <1, maxLength>

	public static void generate(String alphabet, int maxLength) {
		StringGenerator.alphabet = alphabet;
		StringGenerator.maxLength = maxLength;
		initMap();
	}

	private static void initMap() {
		map = new HashMap<>(maxLength);
		for (int i = 1; i <= maxLength; i++) {
			try {
				tmpSet = new HashSet<>((int) Math.pow(alphabet.length(), i));
				generateStrings("", i, alphabet.toCharArray());
				map.put(i, tmpSet);
			}
			catch (OutOfMemoryError e) {
				System.out.println("Attack aborted due to insufficient memory: " + (Runtime.getRuntime().totalMemory() / 1048576) + " MiB out of " + (Runtime.getRuntime().maxMemory() / 1048576) + " MiB used ");
				System.exit(1);
			}
		}
		tmpSet = null;
	}

	private static void generateStrings(String currentString, int maxLength, char[] alphabet) {
		if (currentString.length() == maxLength) {
			tmpSet.add(currentString);
			return;
		}
		else {
			for (char c : alphabet) {
				generateStrings(currentString + c, maxLength, alphabet);
			}
		}
	}

	public static Map<Integer, Set<String>> getMap() {
		return map;
	}

	// returns set of all strings of length 'n'
	// so I don't have to call getMap().get(n)
	public static Set<String> getSet(int n) {
		return map.get(n);
	}

}
