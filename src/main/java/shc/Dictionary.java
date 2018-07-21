package shc;

import shc.fersen.attack.FersenAttack;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that builds dictionary tree and evaluates plaintext.
 *
 */
public class Dictionary {

	/**
	 * Dictionary uses either linear or quadratic evaluation.
	 */
	public enum Scale {

		LINEAR(0), QUADRATIC(1);

		public final int scaleFactor;

		private Scale(int scaleFactor) {
			this.scaleFactor = scaleFactor;
		}
	}

	/**
	 * Root node of the dictionary tree.
	 */
	public final Node root = new Node();

	/**
	 * Longest word in the dictionary.
	 */
	private int longestWordLength = 0;

	/**
	 * Shortest word in the dictionary.
	 */
	private int shortestWordLength = Integer.MAX_VALUE;

	/**
	 * Loads the dicionary.
	 *
	 * @param path path of the dictionary file
	 * @param ignoreEntriesOfLengthOne indicates whether to exclude words of length one from the dictionary or not
	 */
	public Dictionary(String path, boolean ignoreEntriesOfLengthOne) {

		loadDictionary(path, ignoreEntriesOfLengthOne);
	}

	/**
	 * Loads the dictionary, sets the shortest and longest word length ({@link #shortestWordLength}, {@link #longestWordLength})
	 * and calls {@link #addWord(java.lang.String) } method on each word from dictionary
	 *
	 * @param path path of the dictionary file
	 * @param ignoreEntriesOfLengthOne indicates whether to exclude words of length one from the dictionary or not
	 */
	private void loadDictionary(String path, boolean ignoreEntriesOfLengthOne) {
		String line;
		try (FileReader fr = new FileReader(path);
			BufferedReader reader = new BufferedReader(fr);) {
			while ((line = reader.readLine()) != null) {
				//line = line.trim().toLowerCase();
				line = line.trim().toUpperCase();
				if (!(ignoreEntriesOfLengthOne == true && line.length() < FersenAttack.MIN_WORD_LEN)) {
					addWord(line);
					if (line.length() > longestWordLength) {
						longestWordLength = line.length();
					}
					if (line.length() < shortestWordLength) {
						shortestWordLength = line.length();
					}
				}
			}
		}
		catch (IOException e) {

		}
	}

	/**
	 * Adds word to the tree representation of dictionary.
	 *
	 * @param word dictionary entrie added to the dictionary tree
	 */
	private void addWord(String word) {
		Node currentNode = this.root;
		Node childNode;

		for (char c : word.toCharArray()) {
			childNode = currentNode.getChild(c);
			if (childNode == null) {
				currentNode = currentNode.addChild(c);
			}
			else {
				currentNode = childNode;
			}
		}
		currentNode.wholeWord = true;
	}

	/**
	 * Evaluates plaintext specified by the <code>text</code> argument and returns its score.
	 *
	 * @param text plain text to be evaluated
	 * @param scoreScaling either linear or quadratic scaling of the score
	 * @param MIN_WORD_LEN minimal word length
	 * @return score
	 */
	public double evaluate(String text, Scale scoreScaling, int MIN_WORD_LEN) {
		Node currentNode;
		Node lastWholeWord;
		double score = 0.0;
		String substr;

		for (int i = 0; i <= text.length() - shortestWordLength; i++) {

			try {
				substr = text.substring(i, i + longestWordLength);
			}
			catch (IndexOutOfBoundsException e) {
				substr = text.substring(i);
			}

			currentNode = root;
			lastWholeWord = null;

			boolean flag = true;
			for (char c : substr.toCharArray()) {

				if (currentNode.containsChild(c)) {
					currentNode = currentNode.getChild(c);
					if (currentNode.wholeWord) {
						lastWholeWord = currentNode;
					}
				}
				else {
					flag = false;
					break;
				}

			}
			if (lastWholeWord == null && flag) {
				lastWholeWord = currentNode;
			}

			if (lastWholeWord != null) {
				score += lastWholeWord.wordLength * (lastWholeWord.wordLength * scoreScaling.scaleFactor + 1 - scoreScaling.scaleFactor);
				i += lastWholeWord.wordLength - 1;
			}
		}

		return score / text.length();
	}

	/**
	 * Evaluates plaintext specified by the <code>text</code> argument and returns words found.
	 *
	 * @param text plain text to be evaluated
	 * @param scoreScaling either linear or quadratic scaling of the score
	 * @param MIN_WORD_LEN minimal word length
	 * @return words found in the plaintext
	 */
	public String evaluateAndReturnWords(String text, Scale scoreScaling, int MIN_WORD_LEN) {
		Node currentNode;
		Node lastWholeWord;
		double score = 0.0;
		String substr;
		StringBuilder words = new StringBuilder();

		for (int i = 0; i <= text.length() - shortestWordLength; i++) {

			try {
				substr = text.substring(i, i + longestWordLength);
			}
			catch (IndexOutOfBoundsException e) {
				substr = text.substring(i);
			}

			currentNode = root;
			lastWholeWord = null;

			for (char c : substr.toCharArray()) {

				if (currentNode.containsChild(c)) {
					currentNode = currentNode.getChild(c);
					if (currentNode.wholeWord) {
						lastWholeWord = currentNode;
					}
				}
				else {
					break;
				}

			}
			if (lastWholeWord != null) {
				score += lastWholeWord.wordLength * (lastWholeWord.wordLength * scoreScaling.scaleFactor + 1 - scoreScaling.scaleFactor);
				i += lastWholeWord.wordLength - 1;
				words.append(lastWholeWord.getWord()).append(',');

			}

		}

		String retVal = "";
		if (words.length() > 0) {
			retVal = words.toString().substring(0, words.toString().length() - 1);
		}
		return retVal;
	}

	/**
	 * Inner class representing dictionary tree's nodes.
	 */
	public class Node {

		/**
		 * Letter of the node.
		 */
		public final Character letter;

		/**
		 * Word length ~ node depth.
		 */
		public final int wordLength;

		/**
		 * Marks whether this is a node that ends a word or not.
		 */
		public boolean wholeWord;

		/**
		 * List of children nodes.
		 */
		public List<Node> children;

		/**
		 * Parent node.
		 */
		public Node parent;

		private static final int NO_SUCH_CHILD = -1;

		/**
		 * Initializes the root node.
		 */
		private Node() {
			this.letter = '*';
			this.wordLength = 0;
			this.wholeWord = false;
			this.children = new ArrayList<>();
			this.parent = null;
		}

		/**
		 * Initializes a node.
		 *
		 * @param letter letter of the node
		 * @param wordLength word length of the node
		 * @param wholeWord true, if this node marks the end of a word
		 */
		private Node(char letter, int wordLength, boolean wholeWord) {
			this.letter = letter;
			this.wordLength = wordLength;
			this.wholeWord = wholeWord;
			this.children = new ArrayList<>();
			this.parent = null;
		}

		/**
		 * Returns true, if this contains a child node with letter specified by <code>c</code> argument.
		 *
		 * @param c character being searched for in the children of this node
		 * @return true, if one of the children of this node has letter c
		 */
		private boolean containsChild(char c) {
			for (Node node : this.children) {
				if (node.letter == c) {
					return true;
				}
			}
			return false;
		}

		@Deprecated
		private int indexOfChild(char c) {
			for (int i = 0; i < this.children.size(); i++) {
				if (this.children.get(i).letter == c) {
					return i;
				}
			}
			return NO_SUCH_CHILD;
		}

		/**
		 * If there is a child with letter specified by the <code>c</code> argument, returns the child node, otherwise returns null.
		 *
		 * @param c character to be searched for
		 * @return node if found, otherwise null
		 */
		protected Node getChild(char c) {
			for (Node node : this.children) {
				if (node.letter == c) {
					return node;
				}
			}
			return null;
		}

		/**
		 * Creats a new node with letter specified by the <code>c</code> argument and word length 1 character longer and adds it to the children of this.
		 *
		 * @param c character of the created node
		 * @return created node
		 */
		private Node addChild(char c) {
			Node newNode = new Node(c, this.wordLength + 1, false);
			this.children.add(newNode);
			newNode.parent = this;
			return newNode;
		}

		public String getWord() {
			StringBuilder sb = new StringBuilder();
			Node n = this;
			do {
				sb.append(n.letter);
				n = n.parent;
			}
			while (n != null);

			return sb.reverse().toString();
		}

	}

}
