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
import java.io.PrintStream;
import javax.swing.SwingUtilities;

/**
 *
 */
public class Main {

	/**
	 * Makes the GUI visible if {@code true}, otherwise runs {@link shc.TextStatistics#freq()} and {@link shc.TextStatistics#createCorpus(boolean)}.
	 */
	private static final boolean SHOW_GUI = true;

	/**
	 * Redirets output to file if {@code true}.
	 */
	private static final boolean REDIRECT_OUTPUT_TO_FILE = true;

	public static void main(String[] args) {

		if (REDIRECT_OUTPUT_TO_FILE) {  // redirects System.out.println() to file
			try {
				PrintStream o = new PrintStream(new File("output.txt"));
				System.setOut(o);
			}
			catch (FileNotFoundException ex) {
			}
		}

		if (SHOW_GUI) {

			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					createAndShowGUI();
				}
			});

		}
		else {
			TextStatistics.freq();
			TextStatistics.createCorpus(true);
		}
	}

	/**
	 * Creates GUI and shows it.
	 */
	private static void createAndShowGUI() {
//		UIManager.put("Menu.font", Fonts.SANS);
//		UIManager.put("RadioButtonMenuItem.font", Fonts.SANS);

		MyFrame frame = new MyFrame();
		Gui gui = new Gui(frame);
		frame.setVisible(true);
	}
}
