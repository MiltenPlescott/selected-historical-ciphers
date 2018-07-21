/*
 * selected-historical-ciphers
 * 
 * Copyright (c) 2018, Milten Plescott. All rights reserved.
 * 
 * SPDX-License-Identifier:    BSD-3-Clause
 */

package shc;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

/**
 *
 */
public class Gui {

	/**
	 * Creates an object of class {@link JTabbedPane} for each implemented cipher.
	 *
	 * @param frame application frame
	 */
	public Gui(MyFrame frame) {
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
		tabbedPane.setFont(Fonts.SANS);
		frame.add(tabbedPane);

		JPanel panelFersen = new JPanel();
		panelFersen.setBorder(new EmptyBorder(10, 10, 10, 10));
		tabbedPane.addTab("Antoinette-Fersen", null, panelFersen, "Marie Antoinette - Axel von Fersen Cipher");
		FersenGui fersenGui = new FersenGui(frame, panelFersen);

		JPanel panelDana = new JPanel();
		//panelDana.setBorder(new EmptyBorder(10, 10, 10, 10));
		tabbedPane.addTab("Dana", null, panelDana, "Francis Dana Cipher");
		DanaGui danaGui = new DanaGui(frame, panelDana);

		// Menu menu = new Menu(frame);
	}

}
