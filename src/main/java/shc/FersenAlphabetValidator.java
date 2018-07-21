package shc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JOptionPane;

public class FersenAlphabetValidator {

	private static Set<Character> setAscii;
	private static Set<Character> setInput;
	private static Set<Character> setPswd;
	private static Set<Character> setPswdColumn;
	private static Set<Character> setTable;  // without first column (AKA the pswd column)
	private static List<Set<Character>> listSetRows;

	public static boolean validateFersenAlphabet(String ascii, String input, String pswd, String[][] table) {
		if (containsEmptyCell(table)) {
			return false;
		}

		createSets(ascii, input, pswd, table);
		Set<Boolean> results = new HashSet<>(9);

		results.add(pswdInAsciiRange());
		results.add(tableInAsciiRange());
		results.add(inputInAsciiRange());
		results.add(inputInTable());
		results.add(pswdInPswdColumn());
		results.add(overflowingTableCells(table));
		results.add(tableRowsSameSet());
		results.add(duplicatesInPswdColumn(table));
		results.add(duplicatesInTableRows(table));

		// return true when everything is OK
		// return true also if something is slightly wrong, but it is only needed to inform the user and can proceed with encyphering
		// {this also applies for all other methods that return boolean in this class)
		// return false when we CANNOT proceed with encyphering
		if (results.contains(false)) {
			return false;
		}
		return true;
	}

	private static boolean containsEmptyCell(String[][] table) {
		for (String[] row : table) {
			for (String cell : row) {
				if (cell.isEmpty()) {
					showErrorDialog("The table cannot contain any empty cells", "Empty cell in the table");
					return true;
				}
			}
		}
		return false;
	}

	private static void createSets(String ascii, String input, String pswd, String[][] table) {
		setAscii = new HashSet<>(ascii.length());
		setInput = new HashSet<>(ascii.length());
		setPswd = new HashSet<>(ascii.length());
		setPswdColumn = new HashSet<>(ascii.length());
		setTable = new HashSet<>(ascii.length());
		listSetRows = new ArrayList<>(table.length);

		for (int i = 0; i < ascii.length(); i++) {
			setAscii.add(ascii.charAt(i));
		}
		for (int i = 0; i < input.length(); i++) {
			setInput.add(input.charAt(i));
		}
		setInput.remove('\n');

		for (int i = 0; i < pswd.length(); i++) {
			setPswd.add(pswd.charAt(i));
		}
		for (int i = 0; i < table.length; i++) {
			listSetRows.add(new HashSet<>(ascii.length()));
			for (int j = 0; j < table[0].length; j++) {
				for (int k = 0; k < table[i][j].length(); k++) {
					if (j == 0) {
						setPswdColumn.add(table[i][0].charAt(k));
					}
					else {
						setTable.add(table[i][j].charAt(k));
						listSetRows.get(i).add(table[i][j].charAt(k));
					}
				}
			}
		}
	}

	private static boolean pswdInAsciiRange() {
		Set<Character> setPswdTmp = new HashSet<>(setPswd);
		setPswdTmp.removeAll(setAscii);
		if (setPswdTmp.isEmpty() == false) {
			showErrorDialog("Number of unique not allowed characters of the password: " + setPswdTmp.size() + "<br>"
				+ "Decimal ASCII value: " + getUnusableCharsInDecimalAscii(setPswdTmp).toString(), "Password error");
			return false;
		}
		return true;
	}

	private static boolean tableInAsciiRange() {
		Set<Character> setWholeTableTmp = new HashSet<>(setTable);
		setWholeTableTmp.addAll(setPswdColumn);
		setWholeTableTmp.removeAll(setAscii);
		if (setWholeTableTmp.isEmpty() == false) {
			showErrorDialog("Number of unique not allowed characters of the table: " + setWholeTableTmp.size() + "<br>"
				+ "Decimal ASCII value: " + getUnusableCharsInDecimalAscii(setWholeTableTmp).toString(), "Table error");
			return false;
		}
		return true;
	}

	private static boolean inputInAsciiRange() {
		Set<Character> setInputTmp = new HashSet<>(setInput);
		setInputTmp.removeAll(setAscii);
		if (setInputTmp.isEmpty() == false) {
			showErrorDialog("Number of unique not allowed characters of the input: " + setInputTmp.size() + "<br>"
				+ "Decimal ASCII value: " + getUnusableCharsInDecimalAscii(setInputTmp).toString(), "Input error");
			return false;
		}
		return true;
	}

	private static boolean inputInTable() {
		Set<Character> setInputTmp = new HashSet<>(setInput);
		setInputTmp.removeAll(setTable);
		if (setInputTmp.isEmpty() == false) {
			showErrorDialog("Number of unique characters of the input, that are not in the table: " + setInputTmp.size() + "<br>"
				+ "Decimal ASCII value: " + getUnusableCharsInDecimalAscii(setInputTmp).toString() + "<br>"
				+ "ASCII characters: " + setInputTmp.toString(), "Input out of table range");
			return false;
		}
		return true;
	}

	private static boolean pswdInPswdColumn() {
		Set<Character> setPswdTmp = new HashSet<>(setPswd);
		setPswdTmp.removeAll(setPswdColumn);
		if (setPswdTmp.isEmpty() == false) {
			showErrorDialog("Number of unique characters of the password, that are not in the password column of the table: " + setPswdTmp.size() + "<br>"
				+ "Decimal ASCII value: " + getUnusableCharsInDecimalAscii(setPswdTmp).toString() + "<br>"
				+ "ASCII characters: " + setPswdTmp.toString(), "Password out of table range");
			return false;
		}
		return true;
	}

	private static boolean overflowingTableCells(String[][] table) {
		for (int i = 0; i < table.length; i++) {
			for (int j = 0; j < table[0].length; j++) {
				if (j == 0) { // first column --> 1 char per cell
					if (table[i][j].length() != 1) {
						showErrorDialog("Cell in the " + (i + 1) + ". row and " + (j + 1) + ". column of the table must contain exactly 1 character", "Password column of the table error");
						return false;
					}
				}
				else { // the rest of table --> 2 char per cell
					if (table[i][j].length() != 2) {
						showErrorDialog("Cell in the " + (i + 1) + ". row and " + (j + 1) + ". column of the table must contain exactly 2 characters", "Table error");
						return false;
					}
				}
			}
		}
		return true;
	}

	private static boolean duplicatesInPswdColumn(String[][] table) {
		if (setPswdColumn.size() != table.length) {
			showErrorDialog("Password column of the table cannot contain any duplicate characters", "Duplicate characters in the password column of the table");
			return false;
		}
		return true;
	}

	private static boolean duplicatesInTableRows(String[][] table) {
		Set<Integer> setDuplicateColumns = new HashSet<>();
		StringBuilder errorOutput = new StringBuilder("Rows of the table contain duplicate elements: ");
		int defaultLength = errorOutput.length();

		for (int i = 0; i < table.length; i++) {
			setDuplicateColumns.clear();
			for (int j = 1; j < table[0].length; j++) {
				for (int k = 0; k < table[i][j].length(); k++) {
					for (int l = j + 1; l < table[0].length; l++) {
						for (int m = 0; m < table[i][l].length(); m++) {
							if (table[i][j].charAt(k) == table[i][l].charAt(m)) {
								setDuplicateColumns.add(j);
								setDuplicateColumns.add(l);
							}
						}
					}
				}
			}
			if (setDuplicateColumns.isEmpty() == false) {
				errorOutput.append("<br> Row: ").append(i).append(". Column: ").append(setDuplicateColumns.toString());
			}
		}
		if (errorOutput.length() > defaultLength) {
			showErrorDialog(errorOutput.toString(), "Duplicate characters in the rows of the table");
			return false;
		}
		return true;
	}

	private static boolean tableRowsSameSet() {
		for (int i = 0; i < listSetRows.size(); i++) {
			if (listSetRows.get(0).equals(listSetRows.get(i)) == false) {
				showErrorDialog("The set of characters of 1. row of the table is not equal with the set of characters of " + (i + 1) + ". row of the table."
					+ "<br>" + "(pasword column is not taken into account)", "Table rows error");
				return false;
			}
		}
		return true;
	}

	private static List<Integer> getUnusableCharsInDecimalAscii(Set<Character> set) {
		List<Integer> unusableChars = new ArrayList<>(set.size());
		for (Character c : set) {
			unusableChars.add((int) c);
		}
		return unusableChars;
	}

	private static void showErrorDialog(String message, String title) {
		JOptionPane.showMessageDialog(null,
			"<html><span style='font-size:18pt'>" + message,
			title,
			JOptionPane.ERROR_MESSAGE);
	}

}
