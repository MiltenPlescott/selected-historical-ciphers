package shc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JOptionPane;

public class DanaAlphabetValidator {

	// returns true if the CT or PT is valid and ready for deciphering
	// otherwise returns false
	// input text is plainText or cipherText (works the same way for both)
	public static boolean validateDanaInputText(DanaGui gui, String inputText, String legalChars) {
		if (inputText == null || inputText.isEmpty()) {
			return false;
		}

		Set<Character> illegalChars = getIllegalChars(inputText, legalChars);

		if (illegalChars.isEmpty() == false) {
			int chosenOption = showOptionDialog(illegalChars);
			if (chosenOption == 0) {
				gui.setTextAreaInput(deleteIllegalChars(inputText, illegalChars));
				return true;
			}
			else {
				// abort
				return false;
			}
		}

		return true;
	}

	// returns a set of chars, that ARE in the text but ARE NOT in the legalChars string
	private static Set<Character> getIllegalChars(String text, String legalChars) {
		Set<Character> illegalChars = new HashSet<>();

		for (char c : text.toCharArray()) {
			if (legalChars.indexOf(c) == -1) {
				illegalChars.add(c);
			}
		}

		return illegalChars;
	}

	// removes chars from originalText that are in illegalChars and returns this legalText
	private static String deleteIllegalChars(String originalText, Set<Character> illegalChars) {
		StringBuilder newText = new StringBuilder(originalText.length());

		for (char c : originalText.toCharArray()) {
			if (!illegalChars.contains(c)) {
				newText.append(c);
			}
		}

		return newText.toString();
	}

	// generates option dialog that informs user what illegal chars are used in the input
	// and asks a permission to delete them and continue with enciphering/deciphering OR aborts the enciphering/deciphering process without making any changes
	private static int showOptionDialog(Set<Character> illegalChars) {
		Object[] options = {"Delete not allowed characters and continue with encryption/decryption", "Cancel"};
		return JOptionPane.showOptionDialog(null,
			"<html><span style='font-size:18pt'>" + "Number of unique not allowed characters of the input: " + illegalChars.size() + "<br>"
			+ "Decimal ASCII value: " + toDecimalAscii(illegalChars).toString() + "<br>"
			+ "Hexadecimal ASCII value: " + toHexAscii(illegalChars).toString() + "<br>"
			+ "ASCII characters: " + illegalChars.toString(),
			"Input error",
			JOptionPane.YES_NO_OPTION,
			JOptionPane.QUESTION_MESSAGE,
			null,
			options,
			options[0]);
	}

	// converts chars to their decimal ascii representation
	private static List<Integer> toDecimalAscii(Set<Character> set) {
		List<Integer> outptut = new ArrayList<>(set.size());
		for (Character c : set) {
			outptut.add((int) c);
		}
		return outptut;
	}

	// converts chars to their hexadecimal ascii representation
	private static List<String> toHexAscii(Set<Character> set) {
		List<String> output = new ArrayList<>(set.size());
		for (Character c : set) {
			output.add("0x" + Integer.toHexString((int) c).toUpperCase());
		}
		return output;
	}

}
