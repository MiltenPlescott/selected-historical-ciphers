package shc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TextStatistics {

	private static List<String> paragraphs;

	private static void normalizeDictionary() {
		writeListToFile(DictionaryNormalizer.normalize(new File("wordsFromFirstParaInDic/apostrophes_test.txt"), DictionaryNormalizer.Case.LOWER), "wordsFromFirstParaInDic/apostrophes_test_norm.txt");
	}

	static void freq() {
		String path = "fersenCipherText.txt";
		String line;
		try (final FileReader fr = new FileReader(path); final BufferedReader reader = new BufferedReader(fr)) {
			paragraphs = new ArrayList<>(10);
			while ((line = reader.readLine()) != null) {
				line = line.trim().toUpperCase();
				if (!line.equalsIgnoreCase("")) {
					paragraphs.add(line);
				}
			}
		}
		catch (IOException e) {
		}

		whole();
		zt();
		ot();
		for (int i = 0; i < 7; i++) {
			ztPswd(i, 7, true);
			ztPswd(i, 7, false);
		}
	}

	private static void whole() {
		Map<Character, Double> map = new HashMap<>();
		int sum = 0;

		for (String para : paragraphs) {
			for (Character c : para.toCharArray()) {
				sum++;
				if (map.containsKey(c)) {
					map.put(c, map.get(c) + 1.0);
				}
				else {
					map.put(c, 1.0);
				}
			}
		}

		for (Character key : map.keySet()) {
			map.put(key, map.get(key) / sum);
		}

		writeMapToFile(map, "csv/whole.csv");
	}

	private static void zt() {
		Map<Character, Double> map = new HashMap<>();
		int sum = 0;

		for (String para : paragraphs) {
			for (int i = 0; i < para.length(); i += 2) {
				sum++;
				if (map.containsKey(para.charAt(i))) {
					map.put(para.charAt(i), map.get(para.charAt(i)) + 1.0);
				}
				else {
					map.put(para.charAt(i), 1.0);
				}
			}
		}

		for (Character key : map.keySet()) {
			map.put(key, map.get(key) / sum);
		}

		writeMapToFile(map, "csv/zt.csv");
	}

	private static void ot() {
		Map<Character, Double> map = new HashMap<>();
		int sum = 0;

		for (String para : paragraphs) {
			for (int i = 1; i < para.length(); i += 2) {
				sum++;
				if (map.containsKey(para.charAt(i))) {
					map.put(para.charAt(i), map.get(para.charAt(i)) + 1.0);
				}
				else {
					map.put(para.charAt(i), 1.0);
				}
			}
		}

		for (Character key : map.keySet()) {
			map.put(key, map.get(key) / sum);
		}

		writeMapToFile(map, "csv/ot.csv");
	}

	// pswdInd <0;6>
	public static void ztPswd(int pswdInd, int pswdLength, boolean relative) {
		Map<Character, Double> map = new HashMap<>();
		int sum = 0;

		for (String para : paragraphs) {
			for (int i = 0; i < para.length(); i += 2) {
				if (((i / 2) % pswdLength) == pswdInd) {
					sum++;
					if (map.containsKey(para.charAt(i))) {
						map.put(para.charAt(i), map.get(para.charAt(i)) + 1.0);
					}
					else {
						map.put(para.charAt(i), 1.0);
					}
				}
			}
		}

		if (relative) {
			for (Character key : map.keySet()) {
				map.put(key, map.get(key) / sum);
			}

			writeMapToFile(map, "csv/pswd_" + pswdInd + ".csv");
		}
		else {
			writeMapToFile(map, "csv/pswd_Absolute" + pswdInd + ".csv");
		}

	}

	// the tool I used to extract the text from .xml added header and trailer that had to be removed.
	protected static Map<Character, Double> createCorpus(boolean writeFile) {
		Map<Character, Double> map = new HashMap<>();
		String line = null;
		String str = null;
		DictionaryNormalizer.initMap();

		for (int i = 1; i <= 85; i++) {
			try (final FileReader fr = new FileReader("corpus/" + i + ".txt");
				final BufferedReader reader = new BufferedReader(fr)) {

				reader.readLine(); // the header was only one line

				while ((line = reader.readLine()) != null && !line.trim().equals("TAPoRware Tool Parameter Summary")) { // beginning of the trailer

					line = DictionaryNormalizer.normalize(line);
					line = line.toUpperCase();

					for (Character c : line.toCharArray()) {
						if (map.containsKey(c)) {
							map.put(c, map.get(c) + 1.0);
						}
						else {
							map.put(c, 1.0);
						}
					}
				}
			}
			catch (IOException e) {
			}
		}

		Double sum = 0.0;

		for (Character c : map.keySet()) {
			sum += map.get(c);
		}

		//System.out.println("" + sum);
		for (Character c : map.keySet()) {
			map.put(c, map.get(c) / sum);
		}

		if (writeFile) {
			writeMapToFile(map, "csv/corpus.csv");

			try (FileOutputStream fos = new FileOutputStream("corpus.ser");
				ObjectOutputStream oos = new ObjectOutputStream(fos);) {
				oos.writeObject(map);
			}
			catch (IOException ioe) {
			}

		}

		return map;
	}

	@SuppressWarnings("unchecked")
	public static Map<Character, Double> getCorpusFromSer(String fileName) {
		Map<Character, Double> map = null;

		try (FileInputStream fis = new FileInputStream(fileName);
			ObjectInputStream ois = new ObjectInputStream(fis);) {
			map = (HashMap<Character, Double>) ois.readObject();
		}
		catch (IOException | ClassNotFoundException ioe) {
		}
		return map;
	}

	private static void writeStringToFile(String text, String fileName) {
		try (final FileWriter writer = new FileWriter(fileName)) {
			writer.write(text);
		}
		catch (IOException ex) {
		}
	}

	private static void writeListToFile(List<String> list, String fileName) {
		try (final FileWriter writer = new FileWriter(fileName)) {
			for (int i = 0; i < list.size(); i++) {
				if (i < list.size() - 1) {
					writer.write(list.get(i) + System.lineSeparator());
				}
				else {
					writer.write(list.get(i));
				}
			}
		}
		catch (IOException ex) {
		}
	}

	private static <T, E> void writeMapToFile(Map<T, E> map, String fileName) {
		StringBuilder builder = new StringBuilder("\n");

		for (T key : map.keySet()) {
			builder.append(";").append(key.toString()).append(";").append(map.get(key)).append("\n");
		}

		try (final FileWriter writer = new FileWriter(fileName)) {
			writer.write(builder.toString());
		}
		catch (IOException ex) {
		}
	}

}
