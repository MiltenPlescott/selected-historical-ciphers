/*
 * selected-historical-ciphers
 * 
 * Copyright (c) 2018, Milten Plescott. All rights reserved.
 * 
 * SPDX-License-Identifier:    BSD-3-Clause
 */

package shc;

import java.awt.ComponentOrientation;
import java.io.File;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JRadioButtonMenuItem;

@Deprecated
public class Menu {

	/*
	 * flag icons are from: http://www.famfamfam.com/lab/icons/flags/
	 */
	public Menu(MyFrame frame) {

		JMenuBar menuBar = new JMenuBar();
		JMenu menuLanguage = new JMenu("Language/Jazyk");

		JRadioButtonMenuItem menuItemEng = new JRadioButtonMenuItem("English", new ImageIcon("icons" + File.separator + "flags" + File.separator + "gb.gif"), true);
		JRadioButtonMenuItem menuItemSvk = new JRadioButtonMenuItem("Slovensky", new ImageIcon("icons" + File.separator + "flags" + File.separator + "sk.gif"), false);

		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(menuItemEng);
		buttonGroup.add(menuItemSvk);

		menuBar.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

		frame.setJMenuBar(menuBar);
		menuBar.add(menuLanguage);

		menuLanguage.add(menuItemEng);
		menuLanguage.add(menuItemSvk);

	}

}
