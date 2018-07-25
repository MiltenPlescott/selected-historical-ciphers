/*
 * selected-historical-ciphers
 *
 * Copyright (c) 2018, Milten Plescott. All rights reserved.
 *
 * SPDX-License-Identifier:    BSD-3-Clause
 */
package shc.fersen.attack;

import shc.Dictionary;
import shc.FersenGui;
import shc.FersenPasswordLengthAnalyzer;
import shc.TextStatistics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Class performs brute force attack on the Antoinette-Fersen cryptogram.
 *
 */
public class FersenAttack implements ActionListener {

	/**
	 * Minimal score from the dictionary of the first cycle to pass.
	 */
	double DICT_EVAL_MIN_INIT = 0.8;

	/**
	 * Minimal score from the dictionary of the other cycles to pass.
	 */
	double DICT_EVAL_MIN_REST_OF_PARA = 0.95;

	/**
	 * Minimal avarage length of words of the first cycle to pass.
	 */
	double DICT_EVAL_MIN_LEN = 2.4;

	/**
	 * Minimal avarage length of words of the other cycles to pass.
	 */
	double DICT_EVAL_MIN_LEN_REST_OF_PARA = 3.0;

	/**
	 * Frequency coefficient.
	 */
	public static final double COEF_FREQ_COSNT_INIT = 0.035;  // +/- 3%
	//public static double COEF_FREQ_COSNT_REST_OF_PARA = 0.0455;  // +/- 3%

	/**
	 * Number of cycles.
	 */
	int BE = 7;

	int BRUTE_END = BE * (2 * MAX_BRUTE_LENGTH);

	private static FersenGui gui;

	/**
	 * LGuessed length of the used password.
	 */
	public static int PSWD_LENGTH;  // = new FersenPasswordLengthAnalyzer(gui).getPswdLengthOfBestScore();

	/**
	 * Dictionary used to evaluate decrypted ciphertext.
	 */
	private static Dictionary dic;

	/**
	 * Name of the serialized corpus.
	 */
	private static String corpusFileName = "corpus.ser";

	/**
	 * Corpus relative frequencies.
	 */
	protected static Map<Character, Double> mapCorpus;

	/**
	 * Relative frequency of CT characters for each index of the password.
	 */
	protected static Map<Integer, Map<Character, Double>> mapRelFreqCT;

	/**
	 * List of branches.
	 */
	private static List<Branch> branches;

	/**
	 * Maximal length of the generated strings.
	 */
	public static final int MAX_BRUTE_LENGTH = 3;

	/**
	 * Name of the dictionary file.
	 */
	private final String dicName = "dictionary.dic";

	/**
	 * Minimal length of words added to tree from dictionary.
	 */
	public static final int MIN_WORD_LEN = 2;

	/**
	 * Dictionary scaling.
	 */
	Dictionary.Scale dictType = Dictionary.Scale.LINEAR;

	/**
	 * Initializes the resources used during the attack.
	 *
	 * @param gui gui object
	 */
	public FersenAttack(FersenGui gui) {
		FersenAttack.gui = gui;
		PSWD_LENGTH = new FersenPasswordLengthAnalyzer(gui).getPswdLengthOfBestScore();
		Helper.gui = gui;

		dic = new Dictionary(dicName, true);
		mapCorpus = TextStatistics.getCorpusFromSer(corpusFileName);
		mapRelFreqCT = Helper.getMapRelFreqCT(FersenGui.loadOriginalTextParagraphs(FersenGui.Case.UPPER, FersenGui.TextType.CT), PSWD_LENGTH);

		createOrigTable(); // creates original - correct table for result evaluation

		Table.pswdLengthGuess = PSWD_LENGTH;
		Table.alphabet = initAlphabetSet(FersenGui.alphabetUpperPTCT);

		StringGenerator.generate(FersenGui.alphabetUpperPTCT, MAX_BRUTE_LENGTH);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		branches = new ArrayList<>();

		long ts = System.currentTimeMillis();
		firstBruteForcePart();
		long te = System.currentTimeMillis();

		if (1 == 1) {
			//System.out.println("first part DONE!");
			//System.out.println("********* time of first part: " + (te - ts) / 1000.0 + "s");
			//System.out.println("branches: " + branches.size());
		}
		int i;
		for (i = 2 * MAX_BRUTE_LENGTH; i < BRUTE_END; i += (2 * MAX_BRUTE_LENGTH)) {
			long timeS = System.currentTimeMillis();
			//System.out.println("Position in first para: " + i);

			List<Branch> newBranches = new ArrayList<>();
			int bTotal = branches.size();
			int cCnt = 0;

			for (Branch branch : branches) {

				cCnt++;
				long tsi = System.currentTimeMillis();

				int countCT = 0; // number of CTs in this part of first paragraph;  can have value from <0, MAX_BRUTE_LENGTH> ~ <0, 4>

				for (int j = i; j < i + 2 * MAX_BRUTE_LENGTH; j += 2) { // for next 4 odd indices starting from i
					if (branch.text.mask.getItems().get(j).getEncryption() == Mask.Encryption.CT) {
						countCT++;
					}
				}
				if (countCT == 0) { // from these 6 chars (out of which 3 were originally encrypted) they are all solved in this branch
					continue;
				}

				List<Branch> b = bruteForce(branch, i, countCT);
				long tei = System.currentTimeMillis();

				if (b.size() > 0) {
					newBranches.addAll(b);
					//System.out.println("added: " + b.size() + " branches, total branches: " + newBranches.size());
					//System.out.println((cCnt) + " of " + bTotal);

					//System.out.println("time (s): " + (tei - tsi) / 1000.0);
				}

			}
			branches = newBranches;
			//System.out.println("done for i: " + i);
			//System.out.println("branches: " + branches.size());
			evalBranches(branches);
		}

		printResults((System.currentTimeMillis() - ts) / 1000, ((i / 6) + 1));
	}

	/**
	 * Prints the attack results.
	 *
	 * @param totalTime total time of the attack
	 * @param cycle the last completed cycle
	 */
	private void printResults(long totalTime, int cycle) {
		System.out.println("Attack duration: " + totalTime + " seconds.");
		System.out.println("End of " + cycle + ". cycle");
		System.out.println("Number of branches: " + branches.size());
		System.out.println("=============================");
		System.out.println("So far decrypted text and encryption table for each branch:");
		System.out.println("=============================");
		for (int i = 0; i < branches.size(); i++) {
			System.out.println(String.format("%3d", (i + 1)) + " - " + branches.get(i).text.getSolvedSoFar());
			for (int j = 0; j < branches.get(i).table2.size(); j++) {
				System.out.println("\t" + (j + 1) + ": " + branches.get(i).table2.get(j));
			}
			System.out.println("-  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -");
		}
	}

	/**
	 * Brute force starting from the second cycle.
	 *
	 * @param branch current branch to work on
	 * @param from index of the first char in the 6 char long part we are currently processing
	 * @param countCT number of CTs to bruteforce
	 * @return list of branches
	 */
	private List<Branch> bruteForce(Branch branch, int from, int countCT) {
		int pswdIndex = (from / 2) % PSWD_LENGTH; // pswd index at the from position
		List<Branch> retVal = new ArrayList<>();

		for (String generated : StringGenerator.getSet(countCT)) {

			String actualPTPart = Text.FIRST_PARA.substring(from, from + 2 * MAX_BRUTE_LENGTH);
			StringBuilder builder = new StringBuilder(actualPTPart);

			int generatedIndex = 0;

			boolean flag = false;

			for (int i = 0; i < MAX_BRUTE_LENGTH; i += 1) { // i = 0, 1, 2, 3

				if (branch.text.mask.getItems().get(from + 2 * i).getEncryption() == Mask.Encryption.CT) { // char at i (or from + i) is CT (not solved yet)

					if (builder.charAt(2 * i) == generated.charAt(generatedIndex)) { // if there would be two chars in same cell
						flag = true;
						break;
					}

					if (branch.getAllCharsFromTableRow((pswdIndex + i) % PSWD_LENGTH).contains(generated.charAt(generatedIndex))) { // char from generated is already in table paired with char other than one I'm about to pair him with here -> continue
						flag = true;
						break;
					}

					builder.setCharAt(2 * i, generated.charAt(generatedIndex));
					generatedIndex++;
				}
			}

			if (flag) {
				continue;
			}

			// put chars marked as ST in mask into builder
			for (int i = 0; i < 2 * MAX_BRUTE_LENGTH; i += 2) {
				if (branch.text.mask.getItems().get(from + i).getEncryption() == Mask.Encryption.ST) {

					char solved = builder.charAt(i);

					for (Map.Entry<Character, Character> entry : branch.table2.get((pswdIndex + i / 2) % PSWD_LENGTH).entrySet()) {
						if (solved == entry.getKey()) {
							builder.setCharAt(i, entry.getValue());
							continue;
						}
						else if (solved == entry.getValue()) {
							builder.setCharAt(i, entry.getKey());
							continue;
						}
					}
				}
			}

			String toEvalEnd = builder.toString();
			String toEvalWhole = branch.text.getSolvedSoFar() + toEvalEnd;
			double score = dic.evaluate(toEvalWhole, dictType, MIN_WORD_LEN);
			String scoreW = dic.evaluateAndReturnWords(toEvalWhole, dictType, MIN_WORD_LEN);
			String[] split = scoreW.split(",");
			double sc2 = 0; // avarage word length
			for (String s : split) {
				sc2 += s.length() - 1;
			}
			sc2 /= split.length;

			if (score >= DICT_EVAL_MIN_REST_OF_PARA && sc2 > DICT_EVAL_MIN_LEN_REST_OF_PARA && Helper.hasGoodFreq(actualPTPart, toEvalEnd, pswdIndex, PSWD_LENGTH, COEF_FREQ_COSNT_INIT)) {
				Branch newBranch = branch.createCopy();
				for (int i = 0; i < MAX_BRUTE_LENGTH; i++) {
					// put PT-CT pairs to table
					Map<Character, Character> map = newBranch.table2.get((pswdIndex + i) % PSWD_LENGTH);

					char a = toEvalEnd.charAt(2 * i);
					char b = actualPTPart.charAt(2 * i);
					map.put(a, b);

					// update mask
					newBranch.text.mask.getItems().get(from + 2 * i).setEncryption(Mask.Encryption.ST); // update mask - update every 2nd odd item

					for (int j = from + 2 * i; j < Text.FIRST_PARA.length(); j += 2 * PSWD_LENGTH) {
						if ((Text.FIRST_PARA.charAt(j) == a) || (Text.FIRST_PARA.charAt(j) == b)) {
							newBranch.text.mask.getItems().get(j).setEncryption(Mask.Encryption.ST); // mark the position as solvable
						}
					}

				}

				newBranch.text.addNextSolvedPart(toEvalEnd);
				retVal.add(newBranch);
				//System.out.println("ADDING: " + scoreW + " with score: " + score + ",  sc2 (avg word len): " + sc2);

			}
			else {
			}

		}

		return retVal;
	}

	/**
	 * Brute force the first cycle.
	 */
	private void firstBruteForcePart() {
		String firstParaPart = Text.FIRST_PARA.substring(0, 2 * MAX_BRUTE_LENGTH);

		for (String generated : StringGenerator.getSet(MAX_BRUTE_LENGTH)) {
			String toEval = Helper.replaceCT(firstParaPart, generated);
			if (toEval == null) { // replaceCT returns null when there would be cell with two same chars
				continue;
			}
			double score = dic.evaluate(toEval, dictType, MIN_WORD_LEN);
			String scoreW = dic.evaluateAndReturnWords(toEval, dictType, MIN_WORD_LEN);
			String[] split = scoreW.split(",");
			double sc2 = 0; // avarage word length
			for (String s : split) {
				sc2 += s.length() - 1;
			}
			sc2 /= split.length;
			if (score >= DICT_EVAL_MIN_INIT && sc2 > DICT_EVAL_MIN_LEN && Helper.hasGoodFreq(firstParaPart, toEval, 0, PSWD_LENGTH, COEF_FREQ_COSNT_INIT)) {
				//System.out.println("ADDING: " + scoreW + " with score: " + score + ",  sc2 (avg word len): " + sc2);
				Branch branch = new Branch();

				for (int i = 0; i < MAX_BRUTE_LENGTH; i++) {
					// put PT-CT pairs to table
					Map<Character, Character> map = branch.table2.get(i);
					char a = toEval.charAt(2 * i);
					char b = firstParaPart.charAt(2 * i);
					map.put(a, b);

					// update mask
					branch.text.mask.getItems().get(2 * i).setEncryption(Mask.Encryption.ST);

					for (int j = 2 * i; j < Text.FIRST_PARA.length(); j += 2 * PSWD_LENGTH) {
						if ((Text.FIRST_PARA.charAt(j) == a) || (Text.FIRST_PARA.charAt(j) == b)) {
							branch.text.mask.getItems().get(j).setEncryption(Mask.Encryption.ST);
						}
					}
				}

				branch.text.addNextSolvedPart(toEval);

				branches.add(branch);
			}
		}
		evalBranches(branches);
	}

	/**
	 * Evaluates branches.
	 *
	 * @param bran list of branches to evaluate
	 */
	public void evalBranches(List<Branch> bran) {
		Map<Integer, Integer> map = new TreeMap<>();
		Map<List<Integer>, Integer> mapCount = new HashMap<>();

		Map<Branch, List<Integer>> mapScoreToBran = new HashMap<>();
		List<Integer> highestScore = Arrays.asList(-1, -1);

		for (Branch b : bran) {

			int[] branScore = getBranchScore(b);
			map.put(branScore[0], branScore[1]);

			mapScoreToBran.put(b, Arrays.asList(branScore[0], branScore[1]));

			List<Integer> listKey = new ArrayList<>(2);
			listKey.add(branScore[0]);
			listKey.add(branScore[1]);

			if (mapCount.containsKey(listKey)) {
				int count = mapCount.get(listKey);
				mapCount.put(listKey, count + 1);
			}
			else {
				mapCount.put(listKey, 1);
			}

		}
		for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
			List<Integer> listKey = Arrays.asList(entry.getKey(), entry.getValue());
			Integer count = mapCount.get(listKey);
			//System.out.println("" + String.format("% 4d", count) + " branches scored: " + entry.getKey() + " good;  " + entry.getValue() + " bad;  " + (entry.getKey() + entry.getValue()) + " solved out of 84");

			if (entry.getKey() > highestScore.get(0)) {
				highestScore.set(0, entry.getKey());
				highestScore.set(1, entry.getValue());
			}
		}

		for (Map.Entry<Branch, List<Integer>> entry : mapScoreToBran.entrySet()) {
			if (entry.getValue().get(0).equals(highestScore.get(0)) && entry.getValue().get(1).equals(highestScore.get(1))) {
				//System.out.println("Solved so far for best branch: " + entry.getKey().text.getSolvedSoFar());
			}
		}
	}

	/**
	 * Original table as used by Marie Antoinette.
	 */
	private static List<Map<Character, Character>> origTable;

	/**
	 * Indices of the original password used by Marie Antoinette.
	 */
	private int[] pswdIndices = {2, 13, 19, 16, 0, 6, 4}; // COURAGE mapped to the 22- origTable

	/**
	 * Evaluates a single branch and returns its score.
	 *
	 * @param bran branch to evaluate
	 * @return branch score
	 */
	private int[] getBranchScore(Branch bran) {
		int[] branScoreArray = new int[2];

		for (int i = 0; i < bran.table2.size(); i++) {
			int branScore = 0;

			for (Map.Entry<Character, Character> entry : bran.table2.get(i).entrySet()) {
				boolean found = false;

				if (isKeyValuePairInMap(origTable.get(pswdIndices[i]), entry.getKey(), entry.getValue())) {
					found = true;

				}

				if (isKeyValuePairInMap(origTable.get(pswdIndices[i]), entry.getValue(), entry.getKey())) {
					found = true;
				}

				if (found) {
					branScore++;
				}

			}
			branScoreArray[0] += branScore;
			branScoreArray[1] += bran.table2.get(i).size() - branScore;
		}

		if (branScoreArray[0] == MAX_BRUTE_LENGTH) {
			//System.out.println("All " + MAX_BRUTE_LENGTH + " table cells correct: " + bran.text.getSolvedSoFar().substring(bran.text.getSolvedSoFar().length() - 2 * MAX_BRUTE_LENGTH));
		}

		return branScoreArray;
	}

	/**
	 * Checks if theere is a pair in the map that contains <code>key</code> and <code>value</code> characters.
	 *
	 * @param map map to search for the character pair
	 * @param key first character
	 * @param value second character
	 * @return true if the pair is in the map
	 */
	private boolean isKeyValuePairInMap(Map<Character, Character> map, Character key, Character value) {
		if (map == null) {
			return false;
		}
		else if (map.get(key) == null) {
			return false;
		}
		else if (map.get(key).equals(value)) {
			return true;
		}
		return false;
	}

	/**
	 * Creates the original table as used by Marie Antoinette.
	 */
	private static void createOrigTable() {
		String[][] origDataArray = gui.tableData; // first column is pswd column
		origTable = new ArrayList<>();

		for (int row = 0; row < origDataArray.length; row++) {
			origTable.add(new HashMap<>());
			for (int col = 1; col < origDataArray[row].length; col++) {
				origTable.get(row).put(origDataArray[row][col].charAt(0), origDataArray[row][col].charAt(1));
			}
		}
	}

	private Set<Character> initAlphabetSet(String alphabet) {
		Set<Character> set = new HashSet<>(alphabet.length());
		for (Character c : alphabet.toCharArray()) {
			set.add(c);
		}
		return set;
	}

}
