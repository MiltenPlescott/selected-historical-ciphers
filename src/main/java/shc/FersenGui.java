/*
 * selected-historical-ciphers
 * 
 * Copyright (c) 2018, Milten Plescott. All rights reserved.
 * 
 * SPDX-License-Identifier:    BSD-3-Clause
 */

package shc;

import shc.fersen.attack.EveryOtherCharacter;
import shc.fersen.attack.FersenAttack;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;

/**
 * Class that creates GUI elements for Marie Antoinette, Axel von Fersen cipher.
 *
 */
public class FersenGui {

	/**
	 * Letter case.
	 */
	public enum Case {

		/**
		 * Lowercase.
		 */
		LOWER,
		/**
		 * Uppercase.
		 */
		UPPER
	}

	/**
	 * Type of the text.
	 */
	public enum TextType {

		/**
		 * The whole text.
		 */
		WHOLE,
		/**
		 * Only characters, that are never encrypted.
		 */
		PT,
		/**
		 * Only characters, that are encrypted.
		 */
		CT
	}

	/**
	 * Main application frame.
	 */
	MyFrame frame;

	/**
	 * Minimum table row height.
	 */
	private int MIN_ROW_HEIGHT;

	/**
	 * Minimum font size to be used for the text in the table.
	 */
	private static final int MIN_TABLE_FONT_SIZE = 18;

	/**
	 * Maximum font size to be used for the text in the table.
	 */
	private static final int MAX_TABLE_FONT_SIZE = 30;

	/**
	 * Text area used to take input text from the user.
	 */
	private JTextArea textAreaInput;

	/**
	 * Text area used to display results of the encryption/decryption..
	 */
	private JTextArea textAreaOutput;

	/**
	 * Text field representing password of the cipher.
	 */
	private JTextField textFieldPswd;

	/**
	 * Spinner used to change table rows.
	 */
	private JSpinner spinnerRows;

	/**
	 * Spinner used to change table cols.
	 */
	private JSpinner spinnerCols;

	@Deprecated
	private DefaultTableModel defaultTableModel;

	/**
	 * All ASCII characters that are possible to be used in the cipher.
	 */
	private String asciiAlphabet;

	/**
	 * Cipher table.
	 */
	private JTable table;

	/**
	 * Pane that enables scroll functionality of the cipher table.
	 */
	private JScrollPane scrollPane;

	/**
	 * Column labels of cipher table {@link #tableData}
	 */
	private final Object[] columnNames = {"Password", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};

	/**
	 * Uppercase cipher table as used by Marrie Antoinette and Axel von Fersen.
	 */
	public final String[][] tableData = new String[][]{
		{"A", "AB", "CD", "EF", "GH", "IK", "LM", "NO", "PQ", "RS", "TU", "XY", "Z&"},
		{"B", "AC", "BK", "DU", "EI", "FL", "GN", "HO", "MY", "PS", "QX", "RT", "Z&"},
		{"C", "AD", "BG", "CZ", "EK", "FM", "HT", "IX", "LR", "NP", "OQ", "S&", "UY"},
		{"D", "AE", "BZ", "CT", "DK", "FI", "GS", "HY", "LQ", "MX", "NR", "O&", "PU"},
		{"E", "AF", "BL", "CI", "DH", "EU", "GK", "MT", "NQ", "OR", "P&", "SX", "YZ"},
		{"F", "AH", "BF", "CL", "DG", "EQ", "IY", "KP", "MU", "NS", "O&", "RX", "TZ"},
		{"G", "AG", "BI", "CL", "DN", "ER", "FP", "HT", "KU", "M&", "OX", "QY", "SZ"},
		{"H", "AI", "BT", "CS", "DO", "EL", "F&", "GH", "KM", "NQ", "PR", "UY", "XZ"},
		{"I", "AK", "BT", "CS", "DX", "EI", "FL", "GZ", "HY", "M&", "NP", "OQ", "RU"},
		{"K", "AL", "BO", "CP", "DG", "ER", "FS", "HU", "IX", "KY", "MZ", "N&", "QT"},
		{"L", "AM", "BZ", "CD", "EG", "FI", "HK", "LN", "OR", "PS", "QU", "TY", "X&"},
		{"M", "AN", "BO", "CP", "DQ", "ER", "FS", "GT", "HU", "IX", "KY", "LZ", "M&"},
		{"N", "AO", "BC", "DM", "EP", "FS", "GN", "HY", "IU", "KT", "LQ", "R&", "XZ"},
		{"O", "AP", "BL", "CK", "DQ", "ES", "FU", "GX", "HZ", "I&", "MO", "NR", "TY"},
		{"P", "AQ", "BX", "CU", "DZ", "ES", "FO", "GY", "HT", "IN", "KR", "L&", "MP"},
		{"Q", "AR", "BZ", "CT", "DH", "EU", "FQ", "GO", "IL", "KN", "MP", "SY", "X&"},
		{"R", "AS", "BN", "CQ", "DT", "EU", "FY", "G&", "HO", "IP", "KR", "LX", "MZ"},
		{"S", "AT", "BP", "CQ", "DR", "E&", "FS", "GU", "HX", "IY", "KZ", "LN", "MO"},
		{"T", "AU", "BY", "CM", "DX", "E&", "FH", "GQ", "IR", "KZ", "LS", "NP", "OT"},
		{"U", "AX", "BL", "CO", "DQ", "ES", "FU", "GT", "HY", "IN", "KZ", "M&", "PR"},
		{"X", "AY", "B&", "CZ", "DE", "FX", "GU", "HI", "KT", "LS", "MR", "NP", "OQ"},
		{"Y", "AZ", "BU", "CG", "DH", "EX", "FY", "IO", "K&", "LN", "MP", "QS", "RT"},};

	/**
	 * Uppercase characters, that are allowed to be used in the PT or CT.
	 */
	public static final String alphabetUpperPTCT = "ABCDEFGHIKLMNOPQRSTUXYZ&";

	/**
	 * Uppercase characters, that are allowed to be used in password.
	 */
	public static final String alphabetUpperPSWD = "ABCDEFGHIKLMNOPQRSTUXY";

	/**
	 * Divides the <code>panel</code> argument into smaller, more manageable panels.
	 *
	 * @param frame application frame
	 * @param panel Antoinette-Fersen tab
	 */
	public FersenGui(MyFrame frame, JPanel panel) {  // frame nepotrebujem nakoniec ?????
		this.frame = frame;

		panel.setLayout(new GridBagLayout());

		GridBagConstraints gc = new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 10, 10, 10), 10, 10);

		JPanel panelText = new JPanel();
		JPanel panelButtons = new JPanel();
		JPanel panelBottom = new JPanel();

		// for debugging layout managers:
		//panelText.setBackground(Color.red);
		//panelButtons.setBackground(Color.pink);
		panel.add(panelText, gc);
		gc.gridy = 1;
		gc.weightx = 0.0;
		gc.weighty = 0.0;
		panel.add(panelButtons, gc);
		gc.gridy = 2;
		gc.weightx = 1.0;
		gc.weighty = 1.0;
		//gc.fill = GridBagConstraints.VERTICAL;
		gc.fill = GridBagConstraints.BOTH;
		panel.add(panelBottom, gc);

		addText(panelText);
		addButtons(panelButtons);
		addBottom(panelBottom);
	}

	/**
	 * Adds input and output text areas into <code>panel</code> argument.
	 *
	 * @param panel upper portion of the application window
	 */
	private void addText(JPanel panel) {
		textAreaInput = new JTextArea("INPUT");
		textAreaOutput = new JTextArea("OUTPUT");

		textAreaInput.setFont(Fonts.SANS);
		textAreaInput.setLineWrap(true);
		textAreaInput.setWrapStyleWord(true);
		textAreaInput.setMargin(new Insets(7, 7, 7, 7));

		textAreaOutput.setFont(Fonts.SANS);
		textAreaOutput.setLineWrap(true);
		textAreaOutput.setWrapStyleWord(true);
		textAreaOutput.setMargin(new Insets(7, 7, 7, 7));
		textAreaOutput.setEditable(false);
		textAreaOutput.setBackground(Colors.GREY);

		panel.setLayout(new GridLayout(1, 3, 10, 10));
		panel.add(new JScrollPane(textAreaInput));
		panel.add(new JScrollPane(textAreaOutput));
	}

	/**
	 * Adds main control buttons.
	 *
	 * @param panel middle portion of the application window
	 */
	private void addButtons(JPanel panel) {
		JLabel labelPswd = new JLabel("Password:");
		this.textFieldPswd = new JTextField("ABCD", 15);
		JButton buttonDencrypt = new JButton("Encrypt/Decrypt");
		JButton buttonAttack = new JButton("Attack");
		buttonDencrypt.setFocusable(false);
		buttonAttack.setFocusable(false);

		JButton buttonArrow;
		try {
			Image icon = ImageIO.read(new File("icons" + File.separator + "icon.bmp"));
			buttonArrow = new JButton(new ImageIcon(icon));
			buttonArrow.setFocusable(false);
		}
		catch (IOException e) {
			// radsej po slovensky ???
			JOptionPane.showMessageDialog(null,
				"<html><span style='font-size:18pt'>" + File.separator + "icons" + File.separator + "icon.bmp is missing",
				"Icon Error",
				JOptionPane.ERROR_MESSAGE);
			buttonArrow = new JButton("\u2190");
			buttonArrow.setFont(Fonts.MONOB);
			buttonArrow.setFocusable(false);
		}

		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 0));  // hgap, vgap
		panel.add(buttonArrow);
		panel.add(labelPswd).setFont(Fonts.SANS);
		panel.add(textFieldPswd).setFont(Fonts.MONO);
		panel.add(buttonDencrypt).setFont(Fonts.SANS);
		panel.add(buttonAttack).setFont(Fonts.SANS);
		buttonDencrypt.addActionListener(new FersenCipher(this, false));
		buttonAttack.addActionListener(new FersenAttack(this));

		buttonArrow.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				textAreaInput.setText(textAreaOutput.getText());
			}
		});

		JButton buttonTest = new JButton("Kasiski");
		buttonTest.setFocusable(false);
		panel.add(buttonTest).setFont(Fonts.SANS);
		buttonTest.addActionListener(new FersenPasswordLengthAnalyzer(this));
	}

	/**
	 * Divides lower portion of the application window into two panels.
	 *
	 * @param panel lower portion of the application window
	 */
	private void addBottom(JPanel panel) {
		JPanel panelOptions = new JPanel();
		JPanel panelTable = new JPanel();

		panel.setLayout(new GridBagLayout());

		GridBagConstraints gc = new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.VERTICAL, new Insets(0, 10, 0, 10), 0, 0);
		panel.add(panelOptions, gc);

		gc.gridx = 1;
		gc.weightx = 1.0;
		gc.weighty = 1.0;
		gc.anchor = GridBagConstraints.CENTER;
		gc.fill = GridBagConstraints.BOTH;
		panel.add(panelTable, gc);

		addOptions(panelOptions);
		addTable(panelTable);

		// for debugging layout managers:
		//panel.setBackground(Colors.GREYISH);
		//panelOptions.setBackground(Colors.BLUISH);
		//panelTable.setBackground(Colors.GREENISH);
	}

	/**
	 * Adds auxiliary buttons.
	 *
	 * @param panel left panel in lower portion of the application window
	 */
	private void addOptions(JPanel panel) {
		JLabel labelRows = new JLabel("Number of rows: ");
		JLabel labelCols = new JLabel("Number of columns: ");

		spinnerRows = new JSpinner(new SpinnerNumberModel(22, 1, 94, 1)); // value, min, max, step
		spinnerCols = new JSpinner(new SpinnerNumberModel(12, 1, 94, 1));

		JButton buttonDefaultTable = new JButton("Default table");
		JButton buttonCharacterSet = new JButton("Allowed characters");

		labelRows.setFont(Fonts.SANS);
		labelCols.setFont(Fonts.SANS);
		spinnerRows.setFont(Fonts.MONO);
		spinnerCols.setFont(Fonts.MONO);
		buttonDefaultTable.setFont(Fonts.SANS);
		buttonCharacterSet.setFont(Fonts.SANS);
		buttonDefaultTable.setFocusable(false);
		buttonCharacterSet.setFocusable(false);

		spinnerRows.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				setTable();
				setTableRowHeight();
				adjustTableFontSizeToRowHeight();
			}
		});

		spinnerCols.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				setTable();
				tableResized();
			}
		});

		buttonDefaultTable.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setDefaultTable();
			}
		});

		StringBuilder usableCharacters = new StringBuilder(94);
		for (char i = 33; i < 127; i++) {
			usableCharacters.append(i);
		}
		asciiAlphabet = usableCharacters.toString();

		int start = usableCharacters.indexOf("<=>");
		usableCharacters.replace(start, start + 9, "&lt;=&gt;");

		buttonCharacterSet.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null,
					//"<html><span style='font-size:25pt; font-family:monospace'>" + "ASCII &lt;33;126&gt; &#8744; &lt;0x21;0x7E&gt;:" + "<br>" + usableCharacters,
					"<html><span style='font-size:25pt; font-family:monospace'>" + "ASCII &lt;33;126&gt; alebo &lt;0x21;0x7E&gt;:" + "<br>" + usableCharacters,
					"Allowed characters",
					JOptionPane.INFORMATION_MESSAGE);
			}
		});

		spinnerRows.setToolTipText("Table header row is excluded.");
		labelRows.setToolTipText("Table header row is excluded.");
		spinnerCols.setToolTipText("Password column is excluded.");
		labelCols.setToolTipText("Password column is excluded.");

		((JSpinner.DefaultEditor) spinnerRows.getEditor()).getTextField().setColumns(3);
		((JSpinner.DefaultEditor) spinnerCols.getEditor()).getTextField().setColumns(3);

		panel.setLayout(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);

		panel.add(labelRows, gc);
		gc.gridx = 1;
		//panel.add(textFieldRows, gc);
		panel.add(spinnerRows, gc);
		gc.gridx = 0;
		gc.gridy = 1;
		panel.add(labelCols, gc);
		gc.gridx = 1;
		//panel.add(textFieldCols, gc);
		panel.add(spinnerCols, gc);
		gc.gridx = 0;
		gc.gridy = 2;
		gc.gridwidth = 2;
		panel.add(buttonDefaultTable, gc);
		gc.gridy = 3;
		panel.add(buttonCharacterSet, gc);

	}

	/**
	 * Adds cipher table.
	 *
	 * @param panel right panel in lower portion of the application window
	 */
	private void addTable(JPanel panel) {
		table = new JTable(new DefaultTableModel(tableData, columnNames));

		table.setFont(Fonts.MONO);
		table.getTableHeader().setReorderingAllowed(false);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); // potrebujem toto ?

		MIN_ROW_HEIGHT = table.getRowHeight();

		centerTableData();

		scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

		panel.setLayout(new BorderLayout(0, 100));
		panel.add(scrollPane, BorderLayout.CENTER);

		//table.setPreferredScrollableViewportSize(new Dimension(700, 0));
		//table.setFillsViewportHeight(true);
		table.getParent().addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				tableResized();
			}
		});
	}

	/**
	 * Returns ASCII characters that are possible to be used in the cipher.
	 *
	 * @return alphabet
	 */
	public String getAsciiAlphabet() {
		return asciiAlphabet;
	}

	/**
	 * Returns the text of the text area input.
	 *
	 * @return text
	 */
	public String getTextAreaInput() {
		return textAreaInput.getText();
	}

	/**
	 * Sets the text area input to the text specified by the <code>text</code> argument.
	 *
	 * @param text new text
	 */
	public void setTextAreaInput(String text) {
		textAreaInput.setText(text);
	}

	/**
	 * Sets the text area output to the text specified by the <code>text</code> argument.
	 *
	 * @param text new text
	 */
	public void setTextAreaOutput(String text) {
		textAreaOutput.setText(text);
	}

	/**
	 * Adds the text specified by the <code>text</code> argument to the end of the current text in the text area output.
	 *
	 * @param text text to be appended
	 */
	public void appendTextAreaOutout(String text) {
		textAreaOutput.append(text);
	}

	/**
	 * Returns the text of the text area output.
	 *
	 * @return text
	 */
	public String getTextAreaOutput() {
		return textAreaOutput.getText();
	}

	/**
	 * Sets the font of the output text area to {@link Fonts#MONO}.
	 */
	public void setTextAreaOutputFontMono() {
		textAreaOutput.setFont(Fonts.MONO);
	}

	/**
	 * Sets the font of the output text area to the font specified by <code>font</code> argument.
	 *
	 * @param font new font of the text area output
	 */
	public void setTextAreaOutputFont(Font font) {
		textAreaOutput.setFont(font);
	}

	/**
	 * Sets the font of the output text area to {@link Fonts#SANS}.
	 */
	public void setTextAreaOutputFontSans() {
		textAreaOutput.setFont(Fonts.SANS);
	}

	/**
	 * Returns password from the password text field.
	 *
	 * @return password
	 */
	public String getPassword() {
		return textFieldPswd.getText();
	}

	/**
	 * Sets password to the password text field.
	 *
	 * @param pswd new password
	 */
	public void setPassword(String pswd) {
		this.textFieldPswd.setText(pswd);
	}

	/**
	 * Returns value of the spinner representing the number of table rows.
	 *
	 * @return spinner value
	 */
	public int getSpinnerRows() {
		try {
			this.spinnerRows.commitEdit();
		}
		catch (ParseException e) {
		}
		return (int) this.spinnerRows.getValue();
	}

	/**
	 * Sets spinner representing the number of table rows to the value specified by <code>rows</code> argument.
	 *
	 * @param rows new spinner value
	 */
	public void setSpinnerRows(int rows) {
		this.spinnerRows.setValue(rows);
	}

	/**
	 * Returns value of the spinner representing the number of table columns.
	 *
	 * @return spinner value
	 */
	public int getSpinnerCols() {
		try {
			this.spinnerRows.commitEdit();
		}
		catch (ParseException e) {
		}
		return (int) this.spinnerCols.getValue();
	}

	/**
	 * Sets spinner representing the number of table columns to the value specified by <code>cols</code> argument.
	 *
	 * @param cols new spinner value
	 */
	public void setSpinnerCols(int cols) {
		this.spinnerCols.setValue(cols);
	}

	// pswd column is included
	// table header is not included
	/**
	 * Returns data from the cipher table.
	 *
	 * @return table data
	 */
	public String[][] getTableData() {
		TableCellEditor editor = table.getCellEditor();
		if (editor != null) {
			editor.stopCellEditing();
		}
		TableModel defaultModel = table.getModel();
		String[][] tableData = new String[defaultModel.getRowCount()][defaultModel.getColumnCount()];
		for (int i = 0; i < defaultModel.getRowCount(); i++) {
			for (int j = 0; j < defaultModel.getColumnCount(); j++) {
				if (defaultModel.getValueAt(i, j) == null) {
					tableData[i][j] = "";
				}
				else {
					tableData[i][j] = defaultModel.getValueAt(i, j).toString();
				}
			}
		}
		return tableData;
	}

	/**
	 * Resets cipher table data and dimensions to default values.
	 */
	protected void setDefaultTable() {
		setSpinnerRows(tableData.length);
		setSpinnerCols(columnNames.length - 1);
		table.setModel(new DefaultTableModel(tableData, columnNames));
		centerTableData();
	}

	/**
	 * Sets cipher table data.
	 */
	private void setTable() {
		int rows = getSpinnerRows();
		int cols = getSpinnerCols();
		Object[] columnNames = new String[cols + 1];
		columnNames[0] = "Password";
		for (int i = 1; i < cols + 1; i++) {
			columnNames[i] = "" + i;
		}

		String[][] oldData = preserveTableData(rows, cols + 1);

		if (oldData == null) {
			table.setModel(new DefaultTableModel(new String[rows][cols + 1], columnNames));
		}
		else {
			table.setModel(new DefaultTableModel(oldData, columnNames));
		}

		// table.setModel(new DefaultTableModel(foo, columnNames));
		// table.setModel(new DefaultTableModel(new String[rows][cols + 1], columnNames));
		centerTableData();
	}

	/**
	 * Makes sure that changing the cipher table dimensions will preserve old table data.
	 *
	 * @param newRows number of rows in the new table
	 * @param newCols number of columns in the new table
	 * @return new cipher table's data
	 */
	private String[][] preserveTableData(int newRows, int newCols) {
		String[][] oldData;

		oldData = getTableData();
		String[][] newData;

		newData = new String[newRows][newCols];

		for (int i = 0; i < newData.length; i++) {
			for (int j = 0; j < newData[0].length; j++) {
				try {
					newData[i][j] = oldData[i][j];
				}
				catch (ArrayIndexOutOfBoundsException e) {
					newData[i][j] = "";
				}
			}
		}

		return newData;
	}

	/**
	 * Aligns the data in the cipher table to the center of each cell.
	 */
	private void centerTableData() {
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		for (int i = 0; i < table.getColumnCount(); i++) {
			table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
			table.getColumnModel().getColumn(i).setMinWidth(40);
			table.getColumnModel().getColumn(i).setPreferredWidth(1);
		}
		//table.getColumnModel().getColumn(0).setMinWidth(50);
		table.getColumnModel().getColumn(0).setMinWidth(8 * table.getTableHeader().getFont().getSize());
		//table.getColumnModel().getColumn(0).setMaxWidth(50);
	}

	/**
	 * Changes row height of the cipher table.
	 */
	private void setTableRowHeight() {
		int rowHeight = getRowHeight();
		int lastRowIndex = getSpinnerRows() - 1;
		if (rowHeight > MIN_ROW_HEIGHT) {
			table.setRowHeight(rowHeight);
			table.setRowHeight(lastRowIndex, scrollPane.getHeight() - scrollPane.getHorizontalScrollBar().getHeight() - 3 - (getSpinnerRows() * getRowHeight()));
			table.getTableHeader().setPreferredSize(new Dimension(table.getTableHeader().getWidth(), rowHeight));
		}
		if (rowHeight > 50) {
			table.setRowHeight(50);
			table.getTableHeader().setPreferredSize(new Dimension(table.getTableHeader().getWidth(), 50));
		}
	}

	/**
	 * Changes font size in the cipher table based on the height of the table rows.
	 */
	private void adjustTableFontSizeToRowHeight() {
		//int rowHeight = scrollPane.getHeight() / (getSpinnerRows() + 1);

		//System.out.println("" + rowHeight);
		if (getRowHeight() < MIN_TABLE_FONT_SIZE) {
			table.setFont(new Font(Font.MONOSPACED, Font.PLAIN, MIN_TABLE_FONT_SIZE));
			table.getTableHeader().setFont(new Font(Font.MONOSPACED, Font.PLAIN, MIN_TABLE_FONT_SIZE));
		}
		else if (getRowHeight() > MAX_TABLE_FONT_SIZE) {
			table.setFont(new Font(Font.MONOSPACED, Font.PLAIN, MAX_TABLE_FONT_SIZE));
			table.getTableHeader().setFont(new Font(Font.MONOSPACED, Font.PLAIN, MAX_TABLE_FONT_SIZE));
		}
		else {
			table.setFont(new Font(Font.MONOSPACED, Font.PLAIN, getRowHeight()));
			table.getTableHeader().setFont(new Font(Font.MONOSPACED, Font.PLAIN, getRowHeight()));
		}
	}

	/**
	 * Calculates height of a single row of cipher table.
	 *
	 * @return row height
	 */
	private int getRowHeight() {
		return (scrollPane.getHeight() - scrollPane.getHorizontalScrollBar().getHeight()) / (getSpinnerRows() + 1);
	}

	/**
	 * Calls either {@link #loadOriginalTextParagraphs(shc.FersenGui.Case)}, if <code>textType</code> argument is set to WHOLE to return paragraphs
	 * containing the whole transcription of cryptogram of <code>letterCase</code> case,
	 * or appropriate {@link #loadOriginalTextParagraphs(shc.FersenGui.Case, shc.FersenGui.TextType)}, if <code>textType</code> is set to CT or PT.
	 *
	 * @param letterCase case of the returned paragraphs
	 * @param textType type of the returned paragraphs
	 * @return transcription of cryptogram as paragraphs
	 */
	public static List<String> loadOriginalTextParagraphs(Case letterCase, TextType textType) {
		List<String> listParagraphs;

		switch (textType) {
			case PT:
				listParagraphs = getEveryOtherCharacter(loadOriginalTextParagraphs(letterCase), EveryOtherCharacter.EVEN);
				break;
			case CT:
				listParagraphs = getEveryOtherCharacter(loadOriginalTextParagraphs(letterCase), EveryOtherCharacter.ODD);
				break;
			default:
			case WHOLE:
				listParagraphs = loadOriginalTextParagraphs(letterCase);
		}

		return listParagraphs;
	}

	/**
	 * Extract every other character from paragraphs <code>listWholeText</code>, as specified by <code>listWholeText</code>.
	 *
	 * @param listWholeText paragraphs from which is extracted every other character
	 * @param keepCharacters specifies whether to extract odd ~ CT or even ~ PT characters
	 * @return paragraphs containing only every other character
	 */
	private static List<String> getEveryOtherCharacter(List<String> listWholeText, EveryOtherCharacter keepCharacters) {
		List<String> listEveryOtherCharacter = new ArrayList<>();
		StringBuilder shortenedLine = new StringBuilder();

		for (String line : listWholeText) {
			if (line.trim().equalsIgnoreCase("")) { // newline
				listEveryOtherCharacter.add(line);
			}
			else {
				shortenedLine = new StringBuilder(line.length() / 2);
				for (int i = keepCharacters.startingIndex; i < line.length(); i += 2) {
					shortenedLine.append(line.charAt(i));
				}
				listEveryOtherCharacter.add(shortenedLine.toString());
			}
		}

		return listEveryOtherCharacter;
	}

	// each String in List is a separate paragraph
	// the whole CT is converted to either Case.LOWER or Case.UPPER
	/**
	 * Loads a transcription of original cryptogram sent by Marie Antoinette to Axel von Fersen on 8th july 1791
	 * and returns the cryptogram as paragraphs converted to UPPERCASE or lowercase, as specified by <code>letterCase</code> argument.
	 *
	 * @param letterCase case of the returned paragraphs
	 * @return cryptogram paragraphs as separate elemets of list
	 */
	private static List<String> loadOriginalTextParagraphs(Case letterCase) {
		List<String> paragraphs = null;
		String path = "fersenCipherText.txt"; // the last time I checked the file looked like: each paragraph on one line, with no spaces
		String line;

		try (FileReader fr = new FileReader(path);
			BufferedReader reader = new BufferedReader(fr);) {
			paragraphs = new ArrayList<>(10);
			while ((line = reader.readLine()) != null) {
				switch (letterCase) {
					case LOWER:
						line = line.trim().toLowerCase();
						break;
					default:
					case UPPER:
						line = line.trim().toUpperCase();
						break;
				}
				if (!line.equalsIgnoreCase("")) {
					paragraphs.add(line);
				}
			}

		}
		catch (IOException e) {
			JOptionPane.showMessageDialog(null,
				"<html><span style='font-size:18pt'>" + "Error while opening file: <i>" + path + "</i>" + "<br>Program will exit now...",
				"Icon Error",
				JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}

		return paragraphs;
	}

	/**
	 * Changes cipher table's auto resize model when the table is resized, based on the preferred width of the table.
	 */
	private void tableResized() {
		if (table.getPreferredSize().width < table.getParent().getWidth()) {
			table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		}
		else {
			table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		}

		setTableRowHeight();
		adjustTableFontSizeToRowHeight();
	}

}
