package shc.fersen.attack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Branch {

	private static int numberOfBranches;

	public Text text;
	//public Table table;

	public List<Map<Character, Character>> table2; // list containing 7 maps ~ rows. Index of rows corresponds with the pswdIndex

	// to be excluded from brute force
	// key   -> pswd index ~ table row <0,pswdLength)
	// value -> characters already used in the table row, specified by the key
	public Branch() {
		numberOfBranches++;
		this.text = new Text();
		//this.table = new Table();

		this.table2 = new ArrayList<>();
		for (int i = 0; i < FersenAttack.PSWD_LENGTH; i++) {
			table2.add(new HashMap<>());
		}

		//initUsedCharactersMap();
	}

	// this ~ oldBranch
	public Branch createCopy() {
		Branch newBranch = new Branch();

		// copy table2
		newBranch.table2 = new ArrayList<>();
		for (Map<Character, Character> map : this.table2) {
			newBranch.table2.add(copyMap(map));
		}

		// copy text
		newBranch.text = copyText(this.text);

		return newBranch;
	}

	private Map<Character, Character> copyMap(Map<Character, Character> oldMap) {
		if (oldMap == null) {
			return null;
		}
		Map<Character, Character> newMap = new HashMap<>();
		for (Map.Entry entry : oldMap.entrySet()) {
			char key = (char) entry.getKey();
			char value = (char) entry.getValue();
			newMap.put(key, value);
		}
		return newMap;
	}

	private Text copyText(Text oldText) {
		Text newText = new Text();
		newText.setSolvedSoFar(oldText.getSolvedSoFar());

		// set odd items to odd items of old mask
		for (int i = 0; i < oldText.mask.getItems().size(); i += 2) {
			newText.mask.getItems().get(i).setEncryption(oldText.mask.getItems().get(i).getEncryption());
		}

		return newText;
	}

	public Set<Character> getAllCharsFromTableRow(int row) {
		Set<Character> set = new HashSet<>();
		for (char c : table2.get(row).keySet()) {
			set.add(c);
		}
		for (char c : table2.get(row).values()) {
			set.add(c);
		}
		return set;
	}

	public static int getNumberOfBranches() {
		return numberOfBranches;
	}

}
