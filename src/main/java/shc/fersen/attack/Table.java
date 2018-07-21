/*
 * selected-historical-ciphers
 * 
 * Copyright (c) 2018, Milten Plescott. All rights reserved.
 * 
 * SPDX-License-Identifier:    BSD-3-Clause
 */

package shc.fersen.attack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

// pswdIndex ~ row index
public class Table {

	public static int pswdLengthGuess;
	public static Set<Character> alphabet;

	private Set<Row> rows = new TreeSet<>();

	public Table() {
	}

	public void addCell(int pswdIndex, char c1, char c2) {
		if (pswdIndex >= 0 && pswdIndex < pswdLengthGuess && c1 != c2 && alphabet.contains(c1) && alphabet.contains(c2)) { // pswdIndex within range, c1, c2 different and both from alphabet
			Row row = getRow(pswdIndex);
			if (row == null) { // row with pswdIndex does not exist
				rows.add(new Row(pswdIndex, c1, c2));
			}
			else { // row with pswdIndex exists
				if (!(row.containsChar(c1) || row.containsChar(c2))) { // there is no c1 NOR c2 anywhere in the row
					row.addCell(c1, c2);
				}
			}
		}
	}

	// returns row with pswdIndex
	// returns null if the table does not contain any row with pswdIndex
	private Row getRow(int pswdIndex) {
		for (Row row : this.rows) {
			if (row.pswdIndex == pswdIndex) {
				return row;
			}
		}
		return null;
	}

	public int getPswdLengthGuess() {
		return this.pswdLengthGuess;
	}

	public int getNumberOfRows() {
		return this.rows.size();
	}

	// returns number of cells in row specified by 'pswdIndex'
	// returns null, when the pswdIndex is invalid
	public Integer getNumberOfCells(int pswdIndex) {
		try {
			return getRow(pswdIndex).cells.size();
		}
		catch (NullPointerException e) {
			return null;
		}
	}

	// returns number of cells in the whole table
	public int getNumberOfCells() {
		int n = 0;
		for (Row row : rows) {
			n += row.cells.size();
		}
		return n;
	}

	// Set<Set<Character>> -- set of sets of chars ~ row of cells of chars
	public Set<Set<Character>> getCells(int pswdIndex) {
		try {
			return getRow(pswdIndex).getCells();
		}
		catch (NullPointerException e) {
			return null;
		}
	}

	// returns true iff there is cell specified by parameter twoCharacters in the row specified by pswdIndex
	public boolean hasCell(int pswdIndex, List<Character> twoCharacters) {
		assert pswdIndex >= 0 && pswdIndex < pswdLengthGuess : "pswdIndex is not from <0 ; " + pswdLengthGuess + " >";
		assert twoCharacters.size() == 2 : "List 'twoCharacter' does not contain two characters";
		Row row = getRow(pswdIndex);
		if (row == null) {
			return false;
		}
		for (Row.Cell cell : row.cells) {
			if (cell.containsChar(twoCharacters.get(0)) && cell.containsChar(twoCharacters.get(1))) {
				return true;
			}
		}
		return false;
	}

	// if there is a cell containing char 'c' in row 'pswdIndex', returns the other char from that cell
	// returns null if there is no char 'c' in row 'pswdIndex'
	public Character getTheOtherChar(int pswdIndex, Character c) {
		Row row = getRow(pswdIndex);
		if (row != null && row.containsChar(c)) {
			Row.Cell cell = row.getCell(c);
			for (Character ch : cell.characters) {
				if (!ch.equals(c)) {
					return ch;
				}
			}
			return null;
		}
		else {
			return null;
		}
	}

	public Set<Integer> getPswdIndices(Set<Character> charsInCell) {
		try {
			if (charsInCell.size() != 2 || !alphabet.containsAll(charsInCell)) {
				return null;
			}
			Set<Integer> set = new TreeSet<>();
			for (Row row : rows) {
				for (Row.Cell cell : row.cells) {
					if (cell.characters.equals(charsInCell)) {
						set.add(row.pswdIndex);
					}
				}
			}
			return set;
		}
		catch (NullPointerException e) {
			return null;
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("pswdIndex - PT-CT pairs");
		for (Row row : rows) {
			builder.append("\n        ").append(row.toString());
		}
		return builder.toString();
	}

	private class Row implements Comparable<Row> {

		private Integer pswdIndex; // <0 ; pswdLength - 1>
		private Set<Cell> cells = new TreeSet<>();

		private Row(int pswdIndex, char c1, char c2) {
			this.pswdIndex = pswdIndex;
			this.cells.add(new Cell(c1, c2));
		}

		public void addCell(char c1, char c2) {
			this.cells.add(new Cell(c1, c2));
		}

		public boolean containsChar(char c) {
			for (Cell cell : cells) {
				if (cell.characters.contains(c)) {
					return true;
				}
			}
			return false;
		}

		public int getNumberOfCells() {
			return this.cells.size();
		}

		private Set<Set<Character>> getCells() {
			Set<Set<Character>> set = new TreeSet<>(new MyComparator());
			for (Cell cell : cells) {
				set.add(cell.characters);
			}
			return set;
		}

		// return cell that contains character 'c'
		// returns null if there is no such cell
		private Cell getCell(Character c) {
			for (Cell cell : cells) {
				if (cell.containsChar(c)) {
					return cell;
				}
			}
			return null;
		}

		@Override
		public int compareTo(Row row) {
			if (this.pswdIndex < row.pswdIndex) {
				return -1; // this < parameter row
			}
			else if (this.pswdIndex > row.pswdIndex) {
				return 1; // this > parameter row
			}
			return 0; // this == parameter row
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder(4 * cells.size() + 7);
			builder.append(pswdIndex).append(" - [");
			for (Cell cell : cells) {
				builder.append(cell.toString()).append(", ");
			}
			builder.delete(builder.length() - 2, builder.length());
			builder.append("]");
			return builder.toString();
		}

		private class Cell implements Comparable<Cell> {

			private final Set<Character> characters = new TreeSet<>(); // 2 chars that are in one cell of this row

			private Cell(char c1, char c2) {
				this.characters.add(c1);
				this.characters.add(c2);
			}

			public boolean containsChar(char c) {
				return this.characters.contains(c);
			}

			@Override
			public String toString() {
				StringBuilder builder = new StringBuilder(2);
				for (Character c : characters) {
					builder.append(c);
				}
				return builder.toString();
			}

			@Override
			public int compareTo(Cell cell) {
				if (Collections.min(this.characters) < Collections.min(cell.characters)) {
					return -1; // this < parameter cell
				}
				else if (Collections.min(this.characters) > Collections.min(cell.characters)) {
					return 1; // this > parameter cell
				}
				return 0; // this == parameter cell
			}

		}

	}

}

class MyComparator implements Comparator<Set<Character>> {

	// returns -1  iff  o1 < o2
	// returns  0  iff  o1 = o2
	// returns +1  iff  o1 > o2
	@Override
	public int compare(Set<Character> o1, Set<Character> o2) {
		if (o1 == null && o2 == null) {
			return 0;
		}
		else if (o1 == null) {
			return -1;
		}
		else if (o2 == null) {
			return 1;
		}
		else if (o1.size() < o2.size()) {
			return -1;
		}
		else if (o1.size() > o2.size()) {
			return 1;
		}
		else { // o1.size() == o2.size()
			List<Character> list1 = new ArrayList<>(o1);
			Collections.sort(list1);
			List<Character> list2 = new ArrayList<>(o2);
			Collections.sort(list2);
			for (int i = 0; i < list1.size(); i++) {
				if (list1.get(i) < list2.get(i)) {
					return -1;
				}
				else if (list1.get(i) > list2.get(i)) {
					return 1;
				}
			}
		}
		return 0;
	}

}
