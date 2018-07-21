/*
 * selected-historical-ciphers
 * 
 * Copyright (c) 2018, Milten Plescott. All rights reserved.
 * 
 * SPDX-License-Identifier:    BSD-3-Clause
 */

package shc;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

/**
 * Class that creates GUI elements for Francis Dana cipher's code book.
 */
public class DanaCodeBook implements ActionListener {

	/**
	 * Reference to the dana gui object.
	 */
	private DanaGui danaGui;

	/**
	 * Frame of the Dana's code book.
	 */
	private JFrame frame;

	/**
	 * panel that contains GUI widgets.
	 */
	private JPanel panel = new JPanel();

	/**
	 * List of all text field widgets.
	 */
	private List<JTextField> textFields = new ArrayList<>();

	/**
	 * Map that map code words to their codes.
	 */
	private Map<Integer, String> mapCodeBook;

	/**
	 * First part of the code book, with codes being incremented by 31.
	 */
	List<String> listCodeWords_1 = new ArrayList<String>() {
		{
			add("Adams");
			add("Administration");
			add("Ally or Allies");
			add("America");
			add("American");
			add("Army");
			add("Austria");
			add("Bacounin M");
			add("Besborodki M");
			add("Berun");
			add("Britain");
			add("British");
			add("Congrefs");
			add("Constantinople");
			add("Copenhagen");
			add("Counsillors");
			add("Counsels");
			add("Country");
			add("Court");
			add("Credit");
			add("Crimea");
			add("Dana");
			add("Danish");
			add("Denmark");
			add("Dutch");
			add("Emperor");
			add("Emprefs");
			add("Europe");
			add("Fishery");
			add("France");
			add("French");
		}
	};

	/**
	 * Second part of the code book, with codes being incremented by 31.
	 */
	List<String> listCodeWords_2 = new ArrayList<String>() {
		{
			add("Franklin");
			add("Government");
			add("Grand Duke");
			add("Hague");
			add("Holland");
			add("Jay");
			add("Khan");
			add("King");
			add("Lisbon");
			add("Loan");
			add("London");
			add("Madrid");
			add("Mediation");
			add("Men");
			add("Minister");
			add("Ministry");
			add("Money");
			add("Navy");
			add("Count Osterman");
			add("Count Panin");
			add("People");
			add("Petersbourg");
			add("Philadelphia");
			add("Poland");
			add("The Port");
			add("Portugal");
			add("Prince Potemkin");
			add("Rufsia");
			add("Rufsian");
			add("Ships");
			add("Spain");
		}
	};

	/**
	 * Third part of the code book, with codes being incremented by 16.
	 */
	List<String> listCodeWords_3 = new ArrayList<String>() {
		{
			add("Spanish");
			add("State");
			add("Stockholm");
			add("Sweden");
			add("Swedish");
			add("Tartars");
			add("Treaty");
			add("Troops");
			add("Turkish");
			add("Turkey");
			add("United Provinces");
			add("United States");
			add("Count Vergennes");
			add("Versailles");
			add("Vienna");
			add("Count Warontryois");
		}
	};

	/**
	 * Last part of the code book, with codes being incremented by 14.
	 */
	List<String> listCodeWords_4 = new ArrayList<String>() {
		{
			add("Code not assigned");
			add("Code not assigned");
			add("Code not assigned");
			add("Code not assigned");
			add("Code not assigned");
			add("Code not assigned");
			add("Code not assigned");
			add("Code not assigned");
			add("Code not assigned");
			add("Code not assigned");
			add("Code not assigned");
			add("Code not assigned");
			add("Code not assigned");
			add("Code not assigned");
		}
	};

	/**
	 * List of 4 parts of the code book.
	 */
	private List<List<String>> listOfLists = new ArrayList<List<String>>() {
		{
			add(listCodeWords_1);
			add(listCodeWords_2);
			add(listCodeWords_3);
			add(listCodeWords_4);
		}
	};

	/**
	 * Calls methods that initialize code book's data and build GUI.
	 *
	 * @param danaGui reference to the dana gui
	 */
	protected void setDanaGui(DanaGui danaGui) {
		this.danaGui = danaGui;
		initMapCodeBook();
		codeBookFrame();
		codeBookGui();
	}

	/**
	 * Puts all code words with their codes into {@link #mapCodeBook} map.
	 */
	private void initMapCodeBook() {
		mapCodeBook = new HashMap<>();
		int codePosition = 30;

		for (List<String> li : listOfLists) {
			for (int i = 0; i < 3; i++) {
				for (String str : li) {
					mapCodeBook.put(codePosition, str);
					codePosition++;
				}
			}
		}
	}

	/**
	 * Prints out the {@link #mapCodeBook}.
	 */
	protected void printMap() {
		for (int i : mapCodeBook.keySet()) {
			System.out.println("" + i + ": " + mapCodeBook.get(i));
		}
	}

	/**
	 * Creates frame and sets its basic properties - color, position, etc.
	 */
	private void codeBookFrame() {
		frame = new JFrame("Code book");
		frame.setBackground(Colors.GREY);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setMinimumSize(new Dimension(400, 500));
		frame.setBounds(danaGui.getFrame().getX() + danaGui.getFrame().getWidth(), danaGui.getFrame().getY(), 400, danaGui.getFrame().getHeight());
		frame.setPreferredSize(new Dimension(400, danaGui.getFrame().getHeight()));
	}

	/**
	 * Creates scrollpane and calls other GUI creating methods.
	 */
	private void codeBookGui() {
		JScrollPane scrollPane = new JScrollPane(panel);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.getVerticalScrollBar().setUnitIncrement(30);
		frame.add(scrollPane);
		panel.setLayout(new GridLayout(95, 2, 0, 5));
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // top, left, bottom, right

		addLabels(30, 31);
		panel.add(new JLabel(""));
		panel.add(new JLabel(""));
		addLabels(123, 31);
		panel.add(new JLabel(""));
		panel.add(new JLabel(""));
		addLabels(216, 16);
		panel.add(new JLabel(""));
		panel.add(new JLabel(""));

		addCustomCodes(264, 14);

		frame.pack();
	}

	/**
	 * Adds labels to the frame, that represents 3 codes and a codeword.
	 *
	 * @param start starting number of the code
	 * @param step code increments
	 */
	private void addLabels(int start, int step) {
		for (int i = start; i < (start + step); i++) {
			JLabel label = new JLabel("" + String.format("%3d", i) + "  " + String.format("%3d", (i + step)) + "  " + String.format("%3d", (i + 2 * step)));
			label.setFont(Fonts.MONO18B);
			panel.add(label);

			JLabel label2 = new JLabel(mapCodeBook.get(i));
			label2.setFont(Fonts.SANS);
			panel.add(label2);
		}
	}

	/**
	 * Adds labels to the frame, that represents 3 codes and a codeword that are editable by the user.
	 *
	 * @param start starting number of the code
	 * @param step code increments
	 */
	private void addCustomCodes(int start, int step) {
		for (int i = start; i < (start + step); i++) {
			JLabel label = new JLabel("" + String.format("%3d", i) + "  " + String.format("%3d", (i + step)) + "  " + String.format("%3d", (i + 2 * step)));
			label.setFont(Fonts.MONO18B);
			panel.add(label);

			JTextField textField = new JTextField();
			textField.setFont(Fonts.SANS);
			panel.add(textField);
			textFields.add(textField);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		frame.setVisible(true);
	}

	/**
	 * Adds the last 14 codewords and codes, that were left out empty by Dana on purpose.
	 */
	protected void putCustomCodesToMap() {
		int i = 264;
		int j = 278;
		int k = 292;
		for (JTextField tf : textFields) {
			String text = tf.getText();
			if (text.isEmpty()) {
				text = "Code not assigned";
			}
			mapCodeBook.put(i, text);
			mapCodeBook.put(j, text);
			mapCodeBook.put(k, text);
			i++;
			j++;
			k++;
		}
	}

	/**
	 * Returns map that contains all code words with all their appropriate codes.
	 *
	 * @return {@link #mapCodeBook}
	 */
	public Map<Integer, String> getMapCodeBook() {
		return mapCodeBook;
	}

	/**
	 * Singleton constructor used to initialize {@link shc.DanaCodeBook.NewSingletonHolder#INSTANCE}.
	 */
	private DanaCodeBook() {
	}

	/**
	 * Returns object of this singleton class.
	 *
	 * @return {@link shc.DanaCodeBook}
	 */
	public static DanaCodeBook getInstance() {
		return NewSingletonHolder.INSTANCE;
	}

	/**
	 * Contains singleton instance.
	 */
	private static class NewSingletonHolder {

		/**
		 * Singleton instance.
		 */
		private static final DanaCodeBook INSTANCE = new DanaCodeBook();
	}
}
