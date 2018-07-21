package shc.fersen.attack;

import shc.FersenGui;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Helper {

	public static FersenGui gui;

	private Helper() {
	}

	@Deprecated
	public static int getLastBruteForceIndex(String paragraph, int maxBruteForceLength) {
		String CTpart = getEveryOtherChar(paragraph, EveryOtherCharacter.ODD);
		if (CTpart.length() < maxBruteForceLength) {
			return 0;
		}
		else {
			return 2 * (CTpart.length() - maxBruteForceLength);
		}
	}

	// init a mask for each paragraph in list
	// return list of masks
	public static List<List<Boolean>> initMaskOLD(List<String> paragraphs) {
		List<List<Boolean>> listMasks = new ArrayList<>(paragraphs.size());
		for (int i = 0; i < paragraphs.size(); i++) {
			listMasks.add(initMaskOLD(paragraphs.get(i)));
		}
		return listMasks;
	}

	// returns list of booleans of the same size as the length of paragraph:
	// false for every CT char in paragraph
	// true  for every PT char in paragraph
	// assumes that the first char in paragraph is CT
	public static List<Boolean> initMaskOLD(String paragraph) {
		List<Boolean> list = new ArrayList<>(paragraph.length());

		for (int i = 0; i < paragraph.length() - 1; i += 2) {
			list.add(false);
			list.add(true);
		}
		if (list.size() % 2 == 1) {
			list.add(false);
		}

		return list;
	}

	public static List<Mask> initMasks(List<String> paragraphs) {
		List<Mask> masks = new ArrayList<>(paragraphs.size());
		for (String paragraph : paragraphs) {
			masks.add(new Mask(paragraph.length()));
		}
		return masks;
	}

	// returns star index (included) (return[0]) and end index (excluded) (return[1]) of the substring of the paragraph
	public static int[] getStartEndIndices(Mask mask, int maxBruteForceLength, int currentPos) {
		int[] indices = new int[2];
		indices[0] = getStartIndex(mask, currentPos);
		indices[1] = getEndIndex(mask, maxBruteForceLength, currentPos);
		return indices;
	}

	// the start index is inclusive
	// start index is always CT (known or unknown)
	private static int getStartIndex(Mask mask, int currentPos) {
		if (currentPos == 0) {
			return 0;
		}
		else {
			Mask maskStart = mask.getSubMask(0, currentPos);
			int lastIndexCT = maskStart.getLastIndexOf(Mask.Encryption.CT);
			return lastIndexCT + 2; // +2 to make sure that start index is always CT
		}
	}

	// the end index is exclusive
	private static int getEndIndex(Mask mask, int maxBruteForceLength, int currentPos) {
		int falseCount = 0;
		for (int i = currentPos; i < mask.getLength(); i++) {
			if (mask.getItems().get(i).getEncryption() == Mask.Encryption.CT) {
				falseCount++;
			}
			if (falseCount == (maxBruteForceLength + 1)) {
				return i;
			}
		}

		return mask.getLength();
	}

	// returns number of false in the list
	public static int getCTCount(List<Boolean> mask) {
		int count = 0;

		for (boolean bool : mask) {
			if (bool == false) {
				count++;
			}
		}

		return count;
	}

	// returns number of false in mask from 'from' (included) to the end of mask
	public static int countUnknowns(List<Boolean> mask, int from) {
		int count = 0;
		for (boolean bool : mask.subList(from, mask.size())) {
			if (bool == false) {
				count++;
			}
		}
		return count;
	}

	// returns String made of all characters of the text that have corresponding booleans in mask == toExtract
	public static String extractFromString_OLD(String text, List<Boolean> mask, boolean toExtract) {
		StringBuilder builder = new StringBuilder();

		for (int i = 0; i < mask.size(); i++) {
			if (mask.get(i) == toExtract) {
				builder.append(text.charAt(i));
			}
		}

		return builder.toString();
	}

	// return string with those char from 'text', that their corresponding items in mask have encryption == toExtract
	public static String extractFromString(String text, Mask mask, Mask.Encryption toExtract) {
		return extractFromString(text, mask, toExtract, null);
	}

	// return string with those char from 'text', that their corresponding items in mask have either encryption == toExtract OR toExtractAnother
	public static String extractFromString(String text, Mask mask, Mask.Encryption toExtract, Mask.Encryption toExtractAnother) {
		StringBuilder builder = new StringBuilder();

		for (int i = 0; i < mask.getLength(); i++) {
			if (toExtractAnother == null) {
				if (mask.getItems().get(i).getEncryption() == toExtract) {
					builder.append(text.charAt(i));
				}
			}
			else {
				if ((mask.getItems().get(i).getEncryption() == toExtract) || (mask.getItems().get(i).getEncryption() == toExtractAnother)) {
					builder.append(text.charAt(i));
				}
			}
		}

		return builder.toString();
	}

	// returns number of chars to brute force each loop
	@Deprecated
	public static List<Integer> getBruteForceLengthLoops(int pswdLength, int maxBruteForceLength) {
		List<Integer> bruteForceLengthLoops = new ArrayList<>(); // number of chars to brute force each loop
		while (pswdLength >= maxBruteForceLength) {
			bruteForceLengthLoops.add(maxBruteForceLength);
			pswdLength -= maxBruteForceLength;
		}
		if (pswdLength % maxBruteForceLength != 0) {
			bruteForceLengthLoops.add(pswdLength);
		}
		return bruteForceLengthLoops;
	}

	public static String getEveryOtherChar(String text, EveryOtherCharacter keepCharacters) {
		StringBuilder builder = new StringBuilder(text.length() / 2);
		for (int i = keepCharacters.startingIndex; i < text.length(); i += 2) {
			builder.append(text.charAt(i));
		}
		return builder.toString();
	}

	public static String putGeneratedInAppropriatePlacesByMask_OLD(String generated, String currentPartWhole, List<Boolean> maskCurrentPart) {
		StringBuilder builder = new StringBuilder(currentPartWhole);
		int j = 0; // index going through chars in String 'generated'

		for (int i = 0; i < maskCurrentPart.size(); i++) {
			if (maskCurrentPart.get(i) == false) {
				builder.setCharAt(i, generated.charAt(j));
				j++;
			}
		}

		return builder.toString();
	}

	public static String putGeneratedInAppropriatePlacesByMask(String generated, String textWhole, Mask mask) {
		StringBuilder builder = new StringBuilder(textWhole);
		int j = 0; // index going through chars in String 'generated'

		for (int i = 0; i < mask.getLength(); i++) {
			if (mask.getItems().get(i).getEncryption() == Mask.Encryption.CT) {
				builder.setCharAt(i, generated.charAt(j));
				j++;
			}
		}

		return builder.toString();
	}

	// mergeStrings("ACE", "BDF") returns -> "ABCDEF"
	public static String mergeStrings(String odd, String even) {
		if (odd.length() == 0) {
			return even;
		}
		else if (even.length() == 0) {
			return odd;
		}
		else { // odd.length() > 0 && even.length() > 0
			StringBuilder builder = new StringBuilder(odd.length() + even.length());
			if (odd.length() != even.length()) {
				if (odd.length() > even.length()) {
					return mergeStrings(odd.substring(0, even.length()), even) + odd.substring(even.length());
				}
				else if (even.length() > odd.length()) {
					return mergeStrings(odd, even.substring(0, odd.length())) + even.substring(odd.length());
				}
			}
			for (int i = 0; i < (odd.length() > even.length() ? even.length() : odd.length()); i++) {
				builder.append(odd.charAt(i)).append(even.charAt(i));
			}
			return builder.toString();
		}
	}

	// to be used when both strings are of the same length > 0
	public static String mergeStringsSimple(String odd, String even) {
		StringBuilder builder = new StringBuilder(odd.length() + even.length());
		for (int i = 0; i < odd.length(); i++) {
			builder.append(odd.charAt(i)).append(even.charAt(i));
		}
		return builder.toString();
	}

	// mergeStrings("XBYDZF", "ACE") returns -> "ABCDEF"
	public static String replaceCT(String whole, String odd) {
		StringBuilder builder = new StringBuilder(whole);
		for (int i = 0; i < odd.length(); i++) {
			if (whole.charAt(2 * i) == odd.charAt(i)) {
				return null; // there cannot be a cell containing two same chars in the table
			}
			builder.setCharAt(2 * i, odd.charAt(i));
		}
		return builder.toString();
	}

	// removes candidates from the list of candidates, if they have same PT, CT character at the same position
	// the cipher does not have the same PT, CT in one cell in the table
	public static void removeCandidatesSameCharsPTCT(List<Candidate> listCandidates) {
		for (int i = 0; i < listCandidates.size(); i++) {
			if (listCandidates.get(i).hasSamePTCT()) {
				listCandidates.remove(i);
				i--;
			}
		}
	}

	public static boolean hasGoodFreq(String textA, String textB, int pswdIndexStart, int pswdLen, double coef) {

		for (int i = 0; i < textA.length(); i += 2) {

			// PT-CT pair: c1-c2
			char c1 = textA.charAt(i);
			char c2 = textB.charAt(i);
			int pswdInd = (pswdIndexStart + (i / 2)) % pswdLen; // pswd index of c1, c2

			if (FersenAttack.mapRelFreqCT.get(pswdInd).containsKey(c1) && FersenAttack.mapCorpus.containsKey(c2)) {
				double relC1 = FersenAttack.mapRelFreqCT.get(pswdInd).get(c1); // rel freq of c1

				if (relC1 < (FersenAttack.mapCorpus.get(c2) - coef) || relC1 > (FersenAttack.mapCorpus.get(c2) + coef)) {
					return false;
				}
			}

			if (FersenAttack.mapRelFreqCT.get(pswdInd).containsKey(c2) && FersenAttack.mapCorpus.containsKey(c1)) {
				double relC2 = FersenAttack.mapRelFreqCT.get(pswdInd).get(c2); // rel freq of c2

				if (relC2 < (FersenAttack.mapCorpus.get(c1) - coef) || relC2 > (FersenAttack.mapCorpus.get(c1) + coef)) {
					return false;
				}
			}
		}

		return true;
	}

	public static Map<Integer, Map<Character, Double>> getMapRelFreqCT(List<String> origCTChars, int pswdLen) {
		// key: pswdIndex;  value: (key: CT character;  value: relative number of them)
		Map<Integer, Map<Character, Double>> map = new HashMap<>();

		// key: pswdIndex;  value: sum of CT chars at that pswdIndex
		Map<Integer, Double> sums = new HashMap<>();  // sum of CT chars for each pswd char

		for (int i = 0; i < pswdLen; i++) {
			map.put(i, new HashMap<>());
			sums.put(i, 0.0);
		}

		for (String para : origCTChars) {
			for (int i = 0; i < para.length(); i++) {
				sums.put(i % pswdLen, sums.get(i % pswdLen) + 1.0);
				if (map.get(i % pswdLen).containsKey(para.charAt(i))) {
					map.get(i % pswdLen).put(para.charAt(i), map.get(i % pswdLen).get(para.charAt(i)) + 1.0);
				}
				else {
					map.get(i % pswdLen).put(para.charAt(i), 1.0);
				}
			}
		}

		for (int i = 0; i < pswdLen; i++) {
			for (Character c : map.get(i).keySet()) {
				map.get(i).put(c, map.get(i).get(c) / sums.get(i));
			}
		}

		return map;
	}
}
