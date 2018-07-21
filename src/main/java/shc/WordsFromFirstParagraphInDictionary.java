package shc;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class WordsFromFirstParagraphInDictionary {

	public WordsFromFirstParagraphInDictionary() {

		Set<String> firstParaWords = getLinesFromFile("wordsFromFirstParaInDic/prvy_para_na_slova.txt");
		//System.out.println("before set difference: " + firstParaWords.size());

		Set<String> dic = getLinesFromFile("wordsFromFirstParaInDic/github_fr_10k_norm.txt");
		//System.out.println("dic: " + dic.size());

		firstParaWords.removeAll(dic);

		//System.out.println("after set difference firstParaWords \\ dic: " + firstParaWords.size());
		for (String s : firstParaWords) {
			//System.out.println(s);
		}

	}

	private Set<String> getLinesFromFile(String fileName) {
		Set<String> Set = new HashSet<>();

		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			String line;
			while ((line = br.readLine()) != null) {
				Set.add(line.toLowerCase());
			}
		}
		catch (IOException ex) {
		}

		return Set;
	}

}
