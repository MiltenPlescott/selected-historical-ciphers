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
import javax.swing.JOptionPane;

public class FersenCipher implements ActionListener {

	private final FersenGui gui;
	private final boolean skip; // if tue, skips validation checks
	private String[][] tableData;

	public FersenCipher(FersenGui gui, boolean skip) {
		this.gui = gui;
		this.skip = skip;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		gui.setTextAreaOutputFontSans();
		if (skip == false) {
			if (containsJV() && askToCorrectAntoinettesTypos() == false) {
				return;
			}

			this.tableData = gui.getTableData();

			boolean valid;

			valid = FersenAlphabetValidator.validateFersenAlphabet(gui.getAsciiAlphabet(), gui.getTextAreaInput(), gui.getPassword(), this.tableData);

			if (valid == false) {
				// DO NOT PROCEED WITH DENCRYPTING
				return;
			}
		}

		if (pswdIsEmpty()) {
			gui.setTextAreaOutput(gui.getTextAreaInput());
		}
		else {
			dencrypt();
		}

	}

	private boolean pswdIsEmpty() {
		return gui.getPassword().isEmpty();
	}

	private boolean containsJV() {
		return (gui.getTextAreaInput().contains("j")
			|| gui.getTextAreaInput().contains("J")
			|| gui.getTextAreaInput().contains("v")
			|| gui.getTextAreaInput().contains("V"));
	}

	// returns true  -> replace typos
	// returns false -> do not replace typos
	private boolean askToCorrectAntoinettesTypos() {

		int chosenOption = JOptionPane.showConfirmDialog(null, "<html><span style='font-size:18pt; font-family:monospace'>" + "Replace characters?" + "<br>j -> i<br>J -> I<br>v -> u<br>V -> U", "title", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
		//   yes  -->  0 --> replace and proceed with dencrypting
		//    no  -->  1 --> do not replace, but proceed with dencrypting
		// calcel -->  2 --> do nothing (do not replace and do not proceed with dencrypting)
		//    X   --> -1 --> do nothing (do not replace and do not proceed with dencrypting)

		switch (chosenOption) {
			case 0:
				replaceJV();
			case 1:
				return true;
			case 2:
			case -1:
				return false;
		}

		return true;
	}

	private void replaceJV() {
		String input = gui.getTextAreaInput();
		input = input.replace('j', 'i');
		input = input.replace('J', 'I');
		input = input.replace('v', 'u');
		input = input.replace('V', 'U');
		gui.setTextAreaInput(input);
	}

	protected String dencrypt() {
		String[] input = gui.getTextAreaInput().split("\\n");
		String password = gui.getPassword();

		StringBuilder output = new StringBuilder();

		for (int i = 0; i < input.length; i++) {
			output.append(dencrypt(input[i], password));
			if (i != (input.length - 1)) {
				output.append('\n');
			}
		}

		gui.setTextAreaOutput(output.toString());
		return output.toString();
	}

	private String dencrypt(String input, String password) {
		char[] output = new char[input.length()];
		int i, j, k;
		char tableRowChar;
		int tableRowIndex = 0;
		int index = 0;

		for (i = 0; i < input.length(); i++) {
			if (i % 2 == 0) {
				tableRowChar = password.charAt((i / 2) % password.length());
				for (j = 0; j < tableData.length; j++) {
					if (tableData[j][0].equals("" + tableRowChar)) {
						tableRowIndex = j;
						break;
					}
				}
				for (k = 1; k < tableData[tableRowIndex].length; k++) {
					index = tableData[tableRowIndex][k].indexOf(input.charAt(i));
					if (index != -1) {
						break;
					}
				}
				output[i] = tableData[tableRowIndex][k].charAt((index + 1) % 2);
			}
			else {
				output[i] = input.charAt(i);
			}
		}

		return new String(output);
	}

}
