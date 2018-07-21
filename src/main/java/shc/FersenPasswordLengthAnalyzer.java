package shc;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Class performs Kasiski examination.
 *
 */
public class FersenPasswordLengthAnalyzer implements ActionListener {

	private final FersenGui gui;

	/**
	 * Paragraphs of the ciphertext.
	 */
	private List<String> CTParagraphs;

	/**
	 * Length of the whole ciphertext.
	 */
	private int lengthOfCT;

	/**
	 * key -- length of duplicated text<br>
	 * value -- list of distances
	 */
	private Map<Integer, List<Integer>> mapLengthToDistance;

	/**
	 * key -- length of duplicate<br>
	 * value -- set of divisors of the key
	 */
	private Map<Integer, Set<Integer>> mapDivisors;

	/**
	 * key -- divisor ~ pswdLength candidate<br>
	 * value -- score ~ sum of lengths of all Duplicates, that have key as divisor
	 */
	private Map<Integer, Integer> mapScore;

	public FersenPasswordLengthAnalyzer(FersenGui gui) {
		this.gui = gui;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		analyze();
	}

	/**
	 * Starts the analysis
	 */
	private void analyze() {
		CTParagraphs = FersenGui.loadOriginalTextParagraphs(FersenGui.Case.UPPER, FersenGui.TextType.WHOLE);

		// puts the whole CT into input text area
		// converts the whole CT from list to a single String with '\n' separating paragraphs
		StringBuilder input = new StringBuilder();
		for (String paragraph : CTParagraphs) {
			input.append(paragraph).append("\n");
		}
		gui.setTextAreaInput(input.toString());

		// counts the number of characters in the whole CT
		for (String str : CTParagraphs) {
			lengthOfCT += str.length();
		}

		// set of ngrams (n > 5), that appear at least twice in a single paragraph
		Set<String> setDuplicates = new HashSet<>();

		// i = 5 -- Kasiski on 5-grams or longer
		for (int i = 5; i < lengthOfCT; i++) {
			Set<String> tmp = getNgramsDuplicates(CTParagraphs, i);
			if (tmp.isEmpty()) {
				break; // there are no n-gram duplicates -- there can't be any (n+1)-gram duplicates or longer
			}
			setDuplicates.addAll(tmp);
		}

		List<Duplicate> listDuplicates = new ArrayList<>(setDuplicates.size());

		// for each String duplicate creates an object Duplicate (that contains that String + additional info about that duplicate)
		for (String str : setDuplicates) {
			Duplicate duplicate = new Duplicate(str, str.length());
			listDuplicates.add(duplicate);
			for (String paragraph : CTParagraphs) {
				int fromIndex = 0;
				int foundIndex;
				while ((foundIndex = paragraph.indexOf(str, fromIndex)) != -1) {
					duplicate.addIndex(foundIndex);
					fromIndex = foundIndex + 1;
				}
			}
		}

		removeDuplicatesAtSamePosition(listDuplicates);
		mapLengthToDistance = calculateDistanceBetweenDuplicates(listDuplicates);
		Set<Integer> setDistances = getSetFromMapOfLists(mapLengthToDistance);
		mapDivisors = getDivisorsLookupMap(setDistances);
		Map<Integer, List<Integer>> mapFoo = evaluate();

		mapScore = new TreeMap<>();

		for (Integer len : mapFoo.keySet()) { // i -- length of dup
			for (Integer div : mapFoo.get(len)) { // j -- one of divisors
				if (mapScore.containsKey(div)) {
					mapScore.put(div, mapScore.get(div) + len);
				}
				else {
					mapScore.put(div, len);
				}
			}
		}

		int sum = 0;
		for (Integer i : mapScore.values()) {
			sum += i;
		}

		StringBuilder output = new StringBuilder();
		output.append("pswd length -- probability\n");

		for (Integer i : mapScore.keySet()) {
			output.append("                 ").append(i).append("  --  ");
			output.append(Math.round((mapScore.get(i) * 100.0 / sum) * 10) / 10.0);
			output.append("%\n");
		}

		gui.setTextAreaOutput(output.toString());

	}

	public int getPswdLengthOfBestScore() {
		analyze();
		Map.Entry<Integer, Integer> maxEntry = new AbstractMap.SimpleEntry<Integer, Integer>(0, 0);
		for (Map.Entry<Integer, Integer> entry : mapScore.entrySet()) {
			if (entry.getValue() > maxEntry.getValue()) {
				maxEntry = entry;
			}
		}
		return maxEntry.getKey();
	}

	/**
	 * Returns map used to lookup the numbers needed to get divisors from
	 *
	 * @param set set of numbers to get divisors from
	 * @return key -- number (distance)<br>values -- its divisors
	 */
	private Map<Integer, Set<Integer>> getDivisorsLookupMap(Set<Integer> set) {
		Map<Integer, Set<Integer>> map = new HashMap<>(set.size());
		for (Integer i : set) {
			map.put(i / 2, MathCustom.getDivisors(i / 2));
		}
		return map;
	}

	private Map<Integer, List<Integer>> evaluate() {

		Map<Integer, List<Integer>> mapFoo = new HashMap<>(); // key -- length of dup
		// value -- all divisors of all occurrences of duplicate of key length

		for (Integer dupLength : mapLengthToDistance.keySet()) {
			List<Integer> list = new ArrayList<>();
			for (Integer distance : mapLengthToDistance.get(dupLength)) {
				list.addAll(mapDivisors.get(distance / 2));
			}
			mapFoo.put(dupLength, list);
		}

		return mapFoo;
	}

	@Deprecated
	private Set<String> getNgramsDuplicates(String text, int n) {
		Set<String> ngrams = new HashSet<>(text.length() - n + 1);
		Set<String> duplicates = new HashSet<>();

		for (int i = 0; i <= text.length() - n; i++) {
			String ngram = text.substring(i, i + n);
			if (ngrams.add(ngram) == false) { // HashSet: add() == false when adding was not successful - the set already contains the element
				duplicates.add(ngram);
			}
		}

		return duplicates;
	}

	//
	/**
	 * Returns duplicates across all Strings (paragraphs) in a List.
	 *
	 * @param paragraphs list of strings to get duplicates from
	 * @param n n-gram
	 * @return duplicates
	 */
	private Set<String> getNgramsDuplicates(List<String> paragraphs, int n) {
		Set<String> ngrams = new HashSet<>(lengthOfCT);
		Set<String> duplicates = new HashSet<>();

		for (String paragraph : paragraphs) {
			for (int i = 0; i <= paragraph.length() - n; i++) {
				String ngram = paragraph.substring(i, i + n);
				if (ngrams.add(ngram) == false) {
					duplicates.add(ngram);
				}
			}
		}

		return duplicates;
	}

	/**
	 * Removes duplicates from listDuplicates, if their listFirstCharacterIndices is empty,
	 *
	 * @param listDuplicates list of duplicates
	 */
	private void removeDuplicatesAtSamePosition(List<Duplicate> listDuplicates) {
		Iterator<Duplicate> it = listDuplicates.iterator();
		while (it.hasNext()) {
			Duplicate currentElement = it.next();
			if (new HashSet<>(currentElement.listFirstCharacterIndices).size() <= 1) {
				it.remove();
			}
		}
	}

	/**
	 * key -- length of duplicate text<br>
	 * value -- list of all distances
	 *
	 * @param listDuplicates list of duplicates
	 * @return map of all distances to the length of the duplicate text
	 */
	private Map<Integer, List<Integer>> calculateDistanceBetweenDuplicates(List<Duplicate> listDuplicates) {
		Map<Integer, List<Integer>> map = new HashMap<>();

		for (Duplicate dup : listDuplicates) {
			List<Integer> listDistances = new ArrayList<>();
			if (map.containsKey(dup.length)) {
				listDistances = map.get(dup.length);
			}
			listDistances.addAll(getDistances(dup.listFirstCharacterIndices));
			map.put(dup.length, listDistances);
		}

		return map;
	}

	/**
	 * Returns distances between elemtns of the list containing indices in the text.
	 *
	 * @param listIndices list of indices
	 * @return list of distances (absolute value of a subtraction) of every two elements from listIndices, length of the returning list would be combination number (n=listIndices.size(), k=2)
	 */
	private List<Integer> getDistances(List<Integer> listIndices) {
		List<Integer> listDistances = new ArrayList<>();

		for (int i = 0; i < listIndices.size() - 1; i++) {
			for (int j = i + 1; j < listIndices.size(); j++) {
				listDistances.add(Math.abs(listIndices.get(i) - listIndices.get(j)));
			}
		}

		return listDistances;
	}

	private Set<Integer> getSetFromMapOfLists(Map<Integer, List<Integer>> map) {
		Set<Integer> set = new HashSet<>();

		for (List<Integer> list : map.values()) {
			set.addAll(list);
		}

		return set;
	}

	/**
	 * Inner class representing text that repeats in the ciphertext.
	 */
	private class Duplicate {

		private final String text; // repeated substring from the CT

		private List<Integer> listFirstCharacterIndices = new ArrayList<>();

		/**
		 * Length of the repeated substring from the ciphertext
		 */
		private final int length;

		private Duplicate(String text, int length) {
			this.text = text;
			this.length = length;
		}

		/**
		 * Adds a new index - new occurrence of a repeated substring from the ciphertext.
		 *
		 * @param start index of the first character
		 */
		private void addIndex(int start) {
			if (start != -1) {
				this.listFirstCharacterIndices.add(start);
			}
		}

	}

}
