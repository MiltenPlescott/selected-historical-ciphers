/*
 * selected-historical-ciphers
 * 
 * Copyright (c) 2018, Milten Plescott. All rights reserved.
 * 
 * SPDX-License-Identifier:    BSD-3-Clause
 */

package shc;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.swing.JOptionPane;

public class DanaCipher implements ActionListener {

	private final DanaGui gui;
	private final boolean skip;  // if true, skips validation checks
	private final Random rnd;
	private String[][] tableEncryptData;  // 3 rows, 27 cols, table header NOT included
	private String[][] tableDecryptData;  // 3 rows, 27 cols, table header NOT included

	public DanaCipher(DanaGui gui, boolean skip) {
		this.gui = gui;
		this.skip = skip;
		rnd = new Random(System.currentTimeMillis());
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		DanaCodeBook.getInstance().putCustomCodesToMap();
		if (skip == false) {
			if (e.getSource() == gui.buttonEncrypt && validatePT() == false) {
				return;
			}
			else if (e.getSource() == gui.buttonDecrypt && validateCT() == false) {
				return;
			}
		}

		if (e.getSource() == gui.buttonEncrypt) {
			tableEncryptData = gui.getTableEncryptData();
			encrypt();
		}
		else if (e.getSource() == gui.buttonDecrypt) {
			tableDecryptData = gui.getTableDecryptData();
			decrypt();
		}
	}

	// returns true, if the PT is valid
	private boolean validatePT() {
		return DanaAlphabetValidator.validateDanaInputText(gui, gui.getTextAreaInput(), gui.getLegalCharsPT());
	}

	// returns true if the CT is valid
	private boolean validateCT() {
		return DanaAlphabetValidator.validateDanaInputText(gui, gui.getTextAreaInput(), gui.getLegalCharsCT());
	}

	// encrypts 'gui.getTextAreaInput()', and sets the textAreaOutput to the resulting String of this encryption
	private void encrypt() {
		StringBuilder cipherText = new StringBuilder(gui.getTextAreaInput().length());
		int pswdCount = 0;  // whether to use 0th, 1st or 2nd table row

		Map<Integer, String> mapCodeWords = findCodeWords(); // key -> index in the input text, where starts keyword - the associated value
		if (mapCodeWords.size() > 1) {
			mapCodeWords = removeOverlappingCodeWords(mapCodeWords);
		}

		//for (Character c : gui.getTextAreaInput().toCharArray()) {  // possible chars to deal with here (CR should get automatically deleted by JTextArea): a -> z  A -> Z  &  LF  CR
		for (int i = 0; i < gui.getTextAreaInput().length(); i++) {
			Character c = gui.getTextAreaInput().charAt(i);

			if (mapCodeWords.containsKey(i)) {
				int column = rnd.nextInt(3); // which of the 3 numbers associated with the codeword is gonna get chosen to be CT
				int code = getCodeWordNumber(mapCodeWords.get(i), column);
				cipherText.append(code);
				cipherText.append('.');
				pswdCount = 0; // resetting pswd after using a code word
				i += mapCodeWords.get(i).length() - 1;
			}
			else {
				c = Character.toUpperCase(c);
				if (c == '\n') {                // newlines are only preserved, not encrypted, so they don't take a 'rotation' of password (table row)
					cipherText.append('\n');    // given PT: 'X\nY', if the X is encrypted using the 1st row of table, the Y is encrypted using the 2nd row
				}
				else {
					try {
						cipherText.append(tableEncryptData[pswdCount % tableEncryptData.length][c - 'A']);
					}
					catch (ArrayIndexOutOfBoundsException e) {
						showErrorDialog("Input contains not allowed character '" + c + "' with ASCII value  "
							+ (int) c + ", 0x" + Integer.toHexString((int) c).toUpperCase() + "<br>" + e.toString(),
							"ArrayIndexOutOfBoundsException");
					}
					cipherText.append('.');
					pswdCount++;
				}
			}
		}

		gui.setTextAreaOutput(cipherText.toString());
	}

	private Map<Integer, String> findCodeWords() {
		Map<Integer, String> map = new HashMap<>();
		String input = gui.getTextAreaInput().toUpperCase();

		Set<String> set = new HashSet<>(DanaCodeBook.getInstance().getMapCodeBook().values());

		//for (String codeWord : DanaCodeBook.getInstance().getMapCodeBook().values()) {
		for (String codeWord : set) {
			int from = 0;
			int index;

			while ((index = input.indexOf(codeWord.toUpperCase(), from)) != -1) {
				map.put(index, codeWord);
				from += codeWord.length();
			}
		}

		return map;
	}

	private Map<Integer, String> removeOverlappingCodeWords(Map<Integer, String> map) {
		//System.out.println("keys should be in ascending order (treemap)");

		List<Integer> keys = new ArrayList<>(map.keySet());
		Collections.sort(keys);

		for (int i = 0; i < keys.size(); i++) {
			//System.out.println("key: " + keys.get(i));
		}

		for (int i = 0; i < keys.size() - 1; i++) {
			int currentKey = keys.get(i);
			int nextKey = keys.get(i + 1);

			if (nextKey >= currentKey && nextKey < (currentKey + map.get(currentKey).length())) {
				map.remove(nextKey);
				keys.remove(i + 1);
				i--;
			}
		}

		return map;
	}

	// column can be 0, 1 or 2
	private int getCodeWordNumber(String str, int column) {
		List<Integer> candidates = new ArrayList<>();

		for (int key : DanaCodeBook.getInstance().getMapCodeBook().keySet()) {
			if (str.toUpperCase().equals(DanaCodeBook.getInstance().getMapCodeBook().get(key).toUpperCase())) {
				candidates.add(key);
			}
		}

		return candidates.get(column);
	}

	// decrypts 'gui.getTextAreaInput()', and sets the textAreaOutput to the resulting String of this decryption
	private void decrypt() {
		StringBuilder plainText = new StringBuilder(gui.getTextAreaInput().length());
		int pswdCount = 0;  // whether to use 0th, 1st or 2nd table row
		int i = 0;

		List<Integer> cipherNumbers = new ArrayList<>();
		String[] splitCipherText = gui.getTextAreaInput().split("\\.|\n");  // splits on either . OR \n

		for (String str : splitCipherText) {    // possible chars to deal with here (CR should get automatically deleted by JTextArea): 0 -> 9  .  LF  CR
			//System.out.println("str: " + str);
			if (str.isEmpty()) {
				plainText.append('\n');
			}
			else {
				try {
					i = Integer.parseInt(str);
					//System.out.println("i: " + i);
					plainText.append(tableDecryptData[pswdCount % tableDecryptData.length][i - 1]);
				}
				catch (NumberFormatException e) {
					showErrorDialog("Input contains not allowed number '" + i + "'<br>" + e.toString(),
						"NumberFormatException");
				}
				catch (ArrayIndexOutOfBoundsException e) {
					if (DanaCodeBook.getInstance().getMapCodeBook().containsKey(i)) {
						plainText.append(DanaCodeBook.getInstance().getMapCodeBook().get(i).toUpperCase());
						pswdCount = -1;  // resetting pswd after using a code word (it is gonna get incremented later so it will be 0)
					}
					else {
						showErrorDialog("Input contains not allowed number '" + i + "'<br>" + e.toString(),
							"ArrayIndexOutOfBoundsException");
					}
				}
				pswdCount++;
			}
		}

		gui.setTextAreaOutput(plainText.toString());
	}

	private static void showErrorDialog(String message, String title) {
		JOptionPane.showMessageDialog(null,
			"<html><span style='font-size:18pt'>" + message,
			title,
			JOptionPane.ERROR_MESSAGE);
	}

}
