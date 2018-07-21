package shc;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

/**
 * Class that creates GUI elements for Francis Dana cipher.
 *
 */
public class DanaGui {

	/**
	 * Main application frame.
	 */
	private MyFrame frame;

	/**
	 * Text area used to take input text from the user.
	 */
	private JTextArea textAreaInput;

	/**
	 * Text area used to display results of the encryption/decryption..
	 */
	private JTextArea textAreaOutput;

	/**
	 * Button that encrypts the text in the {@link #textAreaInput}.
	 */
	protected JButton buttonEncrypt;

	/**
	 * Button that decrypts the text in the {@link #textAreaInput}.
	 */
	protected JButton buttonDecrypt;

	/**
	 * Button, that opens a new frame containing code book entries.
	 */
	protected JButton buttonCodebook;

	/**
	 * Array of spinners used to shift the alphabets used for encryption/decryption.
	 */
	private JSpinner[] spinners;

	/**
	 * Letters A through Z and &#38;.
	 */
	private String asciiAlphabet;  // A -> Z &

	/**
	 * Letters a through z.
	 */
	private String asciiLowercase; // a -> z

	/**
	 * Digits 0 through 9.
	 */
	private String asciiNumerals;  // 0 -> 9

	/**
	 * All valid plaintext characters.
	 */
	private String legalCharsPT;   // a -> z  A -> Z  &  LF  CR    (CR should get automatically deleted by JTextArea)

	/**
	 * All ciphertext valid characters.
	 */
	private String legalCharsCT;   // 0 -> 9  .  LF  CR            (CR should get automatically deleted by JTextArea)

	/**
	 * Table, that is used to encrypt a message.
	 */
	private JTable tableEncrypt;

	/**
	 * Table, that is used to decrypt a message.
	 */
	private JTable tableDecrypt;

	/**
	 * Array of column names of {@link #tableEncrypt}
	 */
	private final Character[] columnNamesEncrypt;

	/**
	 * 2D array of data in {@link #tableEncrypt}
	 */
	private final Integer[][] tableDataEncrypt;

	/**
	 * Array of column names of {@link #tableDecrypt}
	 */
	private final Integer[] columnNamesDecrypt;

	/**
	 * 2D array of data in {@link #tableDecrypt}
	 */
	private final Character[][] tableDataDecrypt;

	/**
	 * Divides the <code>panel</code> argument into smaller, more manageable panels.
	 *
	 * @param frame application frame
	 * @param panel Dana tab
	 */
	public DanaGui(MyFrame frame, JPanel panel) {
		this.frame = frame;
		columnNamesEncrypt = initColumnNamesEncrypt();
		tableDataEncrypt = initTableDataEncrypt();
		columnNamesDecrypt = initColumnNamesDecrypt();
		tableDataDecrypt = initTableDataDecrypt();

		buildAsciiChars();
		buildLegalChars();

		panel.setLayout(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 10, 10, 10), 10, 10);

		JPanel panelText = new JPanel();
		JPanel panelButtons = new JPanel();
		JPanel panelBottom = new JPanel();

		// for debugging purposes:
		//panelText.setBackground(Color.yellow);
		//panelButtons.setBackground(Color.orange);
		//panelBottom.setBackground(Color.pink);
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

		buttonEncrypt = new JButton("Encrypt");
		buttonDecrypt = new JButton("Decrypt");
		buttonCodebook = new JButton("Code book");
		buttonEncrypt.setFocusable(false);
		buttonDecrypt.setFocusable(false);
		buttonCodebook.setFocusable(false);

		JButton buttonArrow;

		try {
			Image icon = ImageIO.read(new File("icons" + File.separator + "icon.bmp"));
			buttonArrow = new JButton(new ImageIcon(icon));
			buttonArrow.setFocusable(false);
		}
		catch (IOException e) {
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
		panel.add(buttonEncrypt).setFont(Fonts.SANS);
		panel.add(buttonDecrypt).setFont(Fonts.SANS);
		panel.add(buttonCodebook).setFont(Fonts.SANS);

		DanaCipher dana = new DanaCipher(this, false);

		buttonEncrypt.addActionListener(dana);
		buttonDecrypt.addActionListener(dana);

		DanaCodeBook singletonDanaCodeBook = DanaCodeBook.getInstance();
		singletonDanaCodeBook.setDanaGui(this);
		buttonCodebook.addActionListener(singletonDanaCodeBook);

		buttonArrow.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				textAreaInput.setText(textAreaOutput.getText());
			}
		});

	}

	/**
	 * Divides lower portion of the application window into two panels.
	 *
	 * @param panel lower portion of the application window
	 */
	private void addBottom(JPanel panel) {
		JPanel panelSpinners = new JPanel();
		JPanel panelTables = new JPanel();

		panel.setLayout(new GridBagLayout());

		GridBagConstraints gc = new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.VERTICAL, new Insets(0, 10, 0, 10), 0, 0);
		panel.add(panelSpinners, gc);

		gc.gridx = 1;
		gc.weightx = 1.0;
		gc.weighty = 1.0;
		gc.anchor = GridBagConstraints.CENTER;
		gc.fill = GridBagConstraints.BOTH;
		panel.add(panelTables, gc);

		addSpinners(panelSpinners);
		addTables(panelTables);
	}

	/**
	 * Adds auxiliary buttons.
	 *
	 * @param panel left panel in lower portion of the application window
	 */
	private void addSpinners(JPanel panel) {
		JLabel label = new JLabel("Encryption of 'A'"); // telling user what are the spinners doing

		JButton buttonDefaultSpinners = new JButton("Default tables");
		JButton buttonCharacterSet = new JButton("Allowed characters");

		label.setFont(Fonts.SANS);
		buttonDefaultSpinners.setFont(Fonts.SANS);
		buttonCharacterSet.setFont(Fonts.SANS);
		buttonDefaultSpinners.setFocusable(false);
		buttonCharacterSet.setFocusable(false);

		spinners = new JSpinner[3];
		for (int i = 0; i < 3; i++) {
			spinners[i] = new JSpinner(new SpinnerNumberModel(1, 1, 27, 1));
			spinners[i].setFont(Fonts.MONO);
			((JSpinner.DefaultEditor) spinners[i].getEditor()).getTextField().setColumns(3);
		}

		setDefaultSpinners();

		JLabel[] labels = new JLabel[3];
		for (int i = 0; i < 3; i++) {
			labels[i] = new JLabel("" + (i + 1) + ". row: ");
			labels[i].setFont(Fonts.SANS);
		}

		panel.setLayout(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints(0, 0, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
		panel.add(label, gc);

		gc.gridwidth = 1;
		gc.gridy = 1;
		panel.add(labels[0], gc);
		gc.gridy = 2;
		panel.add(labels[1], gc);
		gc.gridy = 3;
		panel.add(labels[2], gc);

		gc.gridx = 1;
		gc.gridy = 1;
		panel.add(spinners[0], gc);
		gc.gridy = 2;
		panel.add(spinners[1], gc);
		gc.gridy = 3;
		panel.add(spinners[2], gc);

		gc.gridwidth = 2;
		gc.gridx = 0;
		gc.gridy = 4;
		panel.add(buttonDefaultSpinners, gc);
		gc.gridy = 5;
		panel.add(buttonCharacterSet, gc);

		spinners[0].addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				setTableRow(0, getSpinnerValue(spinners[0]));
			}
		});
		spinners[1].addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				setTableRow(1, getSpinnerValue(spinners[1]));
			}
		});
		spinners[2].addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				setTableRow(2, getSpinnerValue(spinners[2]));
			}
		});

		buttonDefaultSpinners.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setDefaultSpinners();
			}
		});

		buttonCharacterSet.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null,
					"<html><span style='font-size:25pt; font-family:monospace'>" + "ASCII" + "<br>" + "[10]: 10,13,38,46,&lt;48;57&gt;,&lt;65;90&gt;,&lt;97;122&gt;"
					+ "<br>" + "[16]: 0x0A,0x0D,0x26,0x2E,&lt;0x30;0x39&gt;,&lt;0x41;0x5A&gt;,&lt;0x61;0x7A&gt;" + "<br>" + "CR LF.&" + asciiNumerals + asciiAlphabet.substring(0, 26) + asciiLowercase,
					"Allowed characters",
					JOptionPane.INFORMATION_MESSAGE);
			}
		});

	}

	/**
	 * Adds cipher tables.
	 *
	 * @param panel right panel in lower portion of the application window
	 */
	private void addTables(JPanel panel) {
		tableEncrypt = new JTable(new DefaultTableModel(tableDataEncrypt, columnNamesEncrypt));
		tableDecrypt = new JTable(new DefaultTableModel(tableDataDecrypt, columnNamesDecrypt));

		tableEncrypt.setFont(Fonts.MONOT);
		tableDecrypt.setFont(Fonts.MONOT);
		tableEncrypt.getTableHeader().setFont(Fonts.MONOH);
		tableDecrypt.getTableHeader().setFont(Fonts.MONOH);
		tableEncrypt.getTableHeader().setReorderingAllowed(false);
		tableDecrypt.getTableHeader().setReorderingAllowed(false);
		tableEncrypt.setEnabled(false);
		tableDecrypt.setEnabled(false);

		centerTableData(tableEncrypt);
		centerTableData(tableDecrypt);

		JScrollPane scrollPaneEncrypt = new JScrollPane(tableEncrypt, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		JScrollPane scrollPaneDecrypt = new JScrollPane(tableDecrypt, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		scrollPaneEncrypt.setMinimumSize(new Dimension(tableEncrypt.getColumnModel().getColumn(0).getWidth() * 27, 177));
		scrollPaneDecrypt.setMinimumSize(new Dimension(tableDecrypt.getColumnModel().getColumn(0).getWidth() * 27, 177));

		scrollPaneEncrypt.setPreferredSize(new Dimension(tableEncrypt.getColumnModel().getColumn(0).getWidth() * 27, 177));
		scrollPaneDecrypt.setPreferredSize(new Dimension(tableDecrypt.getColumnModel().getColumn(0).getWidth() * 27, 177));

		scrollPaneEncrypt.setMaximumSize(new Dimension(tableEncrypt.getColumnModel().getColumn(0).getWidth() * 27, 177));
		scrollPaneDecrypt.setMaximumSize(new Dimension(tableDecrypt.getColumnModel().getColumn(0).getWidth() * 27, 177));

		tableEncrypt.setRowHeight(38);
		tableEncrypt.getTableHeader().setPreferredSize(new Dimension(tableEncrypt.getColumnModel().getColumn(0).getWidth() * 27, 46));
		tableDecrypt.setRowHeight(38);
		tableDecrypt.getTableHeader().setPreferredSize(new Dimension(tableDecrypt.getColumnModel().getColumn(0).getWidth() * 27, 46));

		/*
		System.out.println("rowHeight: " + tableDecrypt.getRowHeight());
		System.out.println("4 * rowHeight: " + tableDecrypt.getRowHeight() * 4);
		System.out.println("headerrow height: " + tableDecrypt.getTableHeader().getHeight());
		System.out.println("H.w: " + scrollPaneDecrypt.getHorizontalScrollBar().getWidth());
		System.out.println("H.h: " + scrollPaneDecrypt.getHorizontalScrollBar().getHeight());
		System.out.println("V.w: " + scrollPaneDecrypt.getVerticalScrollBar().getWidth());
		System.out.println("V.h: " + scrollPaneDecrypt.getVerticalScrollBar().getHeight());
		System.out.println("uiman: " + UIManager.get("ScrollBar.width"));
		 */
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(Box.createVerticalGlue());
		panel.add(scrollPaneEncrypt);
		panel.add(Box.createVerticalGlue());
		panel.add(scrollPaneDecrypt);
		panel.add(Box.createVerticalGlue());

		tableEncrypt.getParent().addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				if (tableEncrypt.getPreferredSize().width < tableEncrypt.getParent().getWidth()) {
					tableEncrypt.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
					scrollPaneEncrypt.setPreferredSize(new Dimension(tableEncrypt.getColumnModel().getColumn(0).getWidth() * 27, 160));  // without 17px high scrollbar
				}
				else {
					tableEncrypt.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
					scrollPaneEncrypt.setPreferredSize(new Dimension(tableEncrypt.getColumnModel().getColumn(0).getWidth() * 27, 177));  // with 17px high scrollbar
				}
			}
		});

		tableDecrypt.getParent().addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				if (tableDecrypt.getPreferredSize().width < tableDecrypt.getParent().getWidth()) {
					tableDecrypt.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
					scrollPaneDecrypt.setPreferredSize(new Dimension(tableDecrypt.getColumnModel().getColumn(0).getWidth() * 27, 160));  // without 17px high scrollbar
				}
				else {
					tableDecrypt.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
					scrollPaneDecrypt.setPreferredSize(new Dimension(tableDecrypt.getColumnModel().getColumn(0).getWidth() * 27, 177));  // with 17px high scrollbar
				}
			}
		});

		// for debugging purposes:
		//panel.setBackground(Color.red);
		//panelSpinners.setBackground(Color.green);
		//panelTables.setBackground(Color.blue);
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
	 * Sets the text area output to the text specified by the <code>text</code> argument.
	 *
	 * @param text new text
	 */
	public void setTextAreaOutput(String text) {
		textAreaOutput.setText(text);
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
	 * Sets spinners to their default values and thus resets both cipher tables.
	 */
	private void setDefaultSpinners() {
		spinners[0].setValue(15);
		spinners[1].setValue(6);
		spinners[2].setValue(20);
	}

	/**
	 * Returns the value of spinner specified by <code>spinner</code> argument.
	 *
	 * @param spinner spinner
	 * @return spinner value
	 */
	private int getSpinnerValue(JSpinner spinner) {
		try {
			spinner.commitEdit();
		}
		catch (ParseException e) {
		}
		return (int) spinner.getValue();
	}

	/**
	 * Returns the data from cipher table specified by <code>table</code> argument.
	 *
	 * @param table table from which the data is returned
	 * @return data from table as 2D array
	 */
	private String[][] getTableData(JTable table) {
		TableModel defaultModel = table.getModel();
		String[][] tableData = new String[defaultModel.getRowCount()][defaultModel.getColumnCount()];
		for (int i = 0; i < defaultModel.getRowCount(); i++) {
			for (int j = 0; j < defaultModel.getColumnCount(); j++) {
				tableData[i][j] = defaultModel.getValueAt(i, j).toString();
			}
		}
		return tableData;
	}

	/**
	 * Calls {@link #getTableData(javax.swing.JTable)} with encryption table as argument and returns the data of encryption table as 2D array.
	 *
	 * @return data from encryption table
	 */
	protected String[][] getTableEncryptData() {
		return getTableData(this.tableEncrypt);
	}

	/**
	 * Calls {@link #getTableData(javax.swing.JTable)} with decryption table as argument and returns the data of decryption table as 2D array.
	 *
	 * @return data from decryption table
	 */
	protected String[][] getTableDecryptData() {
		return getTableData(this.tableDecrypt);
	}

	/**
	 * Aligns the data in the cipher table, specified by the <code>table</code> argument, to the center of each cell.
	 *
	 * @param table either encryption or decryption cipher table
	 */
	private void centerTableData(JTable table) {
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		for (int i = 0; i < table.getColumnCount(); i++) {
			table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
			table.getColumnModel().getColumn(i).setMinWidth(37);
			table.getColumnModel().getColumn(i).setPreferredWidth(1);
			table.getColumnModel().getColumn(i).setMaxWidth(60);
		}
	}

	// incoming row parameter starts from 0
	/**
	 * Sets a row, specified by the <code>row</code> argument, of both tables to the value specified by the <code>spinnerValue</code> argument.
	 *
	 * @param row index of cipher table row, can be either 0, 1 or 2
	 * @param spinnerValue value of the spinner to which is the row going to be set
	 */
	private void setTableRow(int row, int spinnerValue) {
		Integer[][] tableData = new Integer[3][27];
		for (int i = 0; i < 27; i++) {
			int encryptValue = (i + spinnerValue - 1) % 27 + 1;
			tableEncrypt.getModel().setValueAt(encryptValue, row, i);

			char[] alphabet = asciiAlphabet.toCharArray();
			char decryptValue = alphabet[(alphabet.length - spinnerValue + i + 1) % alphabet.length];

			tableDecrypt.getModel().setValueAt(decryptValue, row, i);
		}
	}

	/**
	 * Initializes an array of characters representing encryption table's column names.
	 *
	 * @return column names
	 */
	private Character[] initColumnNamesEncrypt() {
		Character[] columnNames = new Character[27];
		for (int i = 0; i < 26; i++) {
			columnNames[i] = (char) ('A' + i);
		}
		columnNames[26] = '&';
		return columnNames;
	}

	/**
	 * Initializes data in the encryption table to the values used by Dana.
	 *
	 * @return initialized table data
	 */
	private Integer[][] initTableDataEncrypt() {
		Integer[][] tableData = new Integer[3][27];
		for (int i = 0; i < 27; i++) {
			tableData[0][i] = (i + 14) % 27 + 1;
			tableData[1][i] = (i + 5) % 27 + 1;
			tableData[2][i] = (i + 19) % 27 + 1;
		}
		return tableData;
	}

	/**
	 * Initializes an array of characters representing decryption table's column names.
	 *
	 * @return column names
	 */
	private Integer[] initColumnNamesDecrypt() {
		Integer[] columnNames = new Integer[27];
		for (int i = 0; i < 27; i++) {
			columnNames[i] = i + 1;
		}
		return columnNames;
	}

	/**
	 * Initializes data in the decryption table to the values used by Dana.
	 *
	 * @return initialized table data
	 */
	private Character[][] initTableDataDecrypt() {
		Character[][] tableData = new Character[3][27];
		for (int i = 0; i < 27; i++) {
			tableData[0][i] = (char) (('N' - 'A' + i) % 27 + 'A');
			tableData[1][i] = (char) (('W' - 'A' + i) % 27 + 'A');
			tableData[2][i] = (char) (('I' - 'A' + i) % 27 + 'A');
		}
		tableData[0][13] = '&';
		tableData[1][4] = '&';
		tableData[2][18] = '&';
		return tableData;
	}

	/**
	 * Creates {@link #asciiAlphabet}, {@link #asciiLowercase} and {@link #asciiNumerals} strings that represent ASCII characters that are used in this cipher.
	 */
	private void buildAsciiChars() {
		StringBuilder usableCharacters = new StringBuilder(27);
		for (char c = 'A'; c <= 'Z'; c++) {
			usableCharacters.append(c);
		}
		usableCharacters.append('&');
		asciiAlphabet = usableCharacters.toString();

		StringBuilder lowercase = new StringBuilder(26);
		for (char c = 'a'; c <= 'z'; c++) {
			lowercase.append(c);
		}
		asciiLowercase = lowercase.toString();

		StringBuilder usableNums = new StringBuilder(10);
		for (char c = '0'; c <= '9'; c++) {
			usableNums.append(c);
		}
		asciiNumerals = usableNums.toString();
	}

	/**
	 * Creates {@link #legalCharsPT} and {@link #legalCharsCT} valid alphabets.
	 */
	private void buildLegalChars() {
		legalCharsPT = asciiAlphabet + asciiLowercase + '\n' + '\r';
		legalCharsCT = asciiNumerals + '.' + '\n' + '\r';
	}

	/**
	 * Returns valid plaintext alphabet characters.
	 *
	 * @return valid plaintext alphabet
	 */
	protected String getLegalCharsPT() {
		return legalCharsPT;
	}

	/**
	 * Returns valid ciphertext alphabet characters.
	 *
	 * @return valid ciphertext alphabet
	 */
	protected String getLegalCharsCT() {
		return legalCharsCT;
	}

	/**
	 * Returns application frame.
	 *
	 * @return application frame
	 */
	public MyFrame getFrame() {
		return frame;
	}

}
