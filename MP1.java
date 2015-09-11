import java.io.File;
import java.lang.reflect.Array;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class MP1 {
    Random generator;
    String userName;
    String inputFileName;
    String delimiters = " \t,;.?!-:@[](){}_*/";
    String[] stopWordsArray = {"i", "me", "my", "myself", "we", "our", "ours", "ourselves", "you", "your", "yours",
            "yourself", "yourselves", "he", "him", "his", "himself", "she", "her", "hers", "herself", "it", "its",
            "itself", "they", "them", "their", "theirs", "themselves", "what", "which", "who", "whom", "this", "that",
            "these", "those", "am", "is", "are", "was", "were", "be", "been", "being", "have", "has", "had", "having",
            "do", "does", "did", "doing", "a", "an", "the", "and", "but", "if", "or", "because", "as", "until", "while",
            "of", "at", "by", "for", "with", "about", "against", "between", "into", "through", "during", "before",
            "after", "above", "below", "to", "from", "up", "down", "in", "out", "on", "off", "over", "under", "again",
            "further", "then", "once", "here", "there", "when", "where", "why", "how", "all", "any", "both", "each",
            "few", "more", "most", "other", "some", "such", "no", "nor", "not", "only", "own", "same", "so", "than",
            "too", "very", "s", "t", "can", "will", "just", "don", "should", "now"};

    void initialRandomGenerator(String seed) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA");
        messageDigest.update(seed.toLowerCase().trim().getBytes());
        byte[] seedMD5 = messageDigest.digest();

        long longSeed = 0;
        for (int i = 0; i < seedMD5.length; i++) {
            longSeed += ((long) seedMD5[i] & 0xffL) << (8 * i);
        }

        this.generator = new Random(longSeed);
    }

    Integer[] getIndexes() throws NoSuchAlgorithmException {
        Integer n = 10000;
        Integer number_of_lines = 50000;
        Integer[] ret = new Integer[n];
        this.initialRandomGenerator(this.userName);
        for (int i = 0; i < n; i++) {
            ret[i] = generator.nextInt(number_of_lines);
        }
        return ret;
    }

    public MP1(String userName, String inputFileName) {
        this.userName = userName;
        this.inputFileName = inputFileName;
    }

    public String[] process() throws Exception {
        String[] ret = new String[20];
       
List<String> lines = readFile(this.inputFileName);
		Map<String, Integer> wordCount = new HashMap<String, Integer>();

		List<String> wordList = removeWords(
				getWordList(getLinesAtIndices(lines, getIndexes())),
				Arrays.asList(stopWordsArray));

		Map<String, Integer> wordCounts = countWords(wordList);

		ret = topWords(wordCounts, 20);

		return ret;
	}

	private List<String> getLinesAtIndices(List<String> lines, Integer[] indexes) {
		List<String> linesAtIndices = new ArrayList<String>();
		for (int i = 0; i < indexes.length; i++) {
			linesAtIndices.add(lines.get(indexes[i].intValue()));
		}
		return linesAtIndices;
	}

	private String[] topWords(Map<String, Integer> wordCounts, int topCount) {
		String[] words = new String[topCount];

		// re-map to count to a word list instead of word to count
		List<String> wordList = new ArrayList<String>(wordCounts.keySet());
		Map<Integer, List<String>> countWords = new HashMap<Integer, List<String>>();
		for (String word : wordList) {
			int count = wordCounts.get(word);
			if (countWords.get(count) == null) {
				List<String> newList = new ArrayList<String>();
				newList.add(word);
				countWords.put(count, newList);
			} else {
				List<String> wordsWithCount = countWords.get(count);
				wordsWithCount.add(word);
				countWords.put(count, wordsWithCount);
			}
		}

		List<Integer> sortCounts = new ArrayList<Integer>();
		sortCounts.addAll(countWords.keySet());
		Collections.sort(sortCounts, new Comparator<Integer>() {
			public int compare(Integer value1, Integer value2) {
				return value2.compareTo(value1);
			}
		});

		int i = 0;
		for (int count : sortCounts) {
			List<String> wordsWithSameCount = countWords.get(count);
			Collections.sort(wordsWithSameCount);
			for (String word : wordsWithSameCount) {
				if (i < topCount) {
					words[i] = word;
				} else {
					break;
				}
				i++;
			}
		}

		return words;
	}

	private Map<String, Integer> countWords(List<String> wordList) {
		Map<String, Integer> wordCounts = new HashMap<String, Integer>();
		Iterator<String> it = wordList.iterator();
		String word = null;
		while (it.hasNext()) {
			word = it.next();
			if (wordCounts.get(word) == null) {
				wordCounts.put(word, 1);
			} else {
				wordCounts.put(word, wordCounts.get(word) + 1);
			}
		}

		return wordCounts;
	}

	private List<String> getWordList(List<String> lines) {
		Iterator<String> it = lines.iterator();
		String line = null;
		List<String> wordList = new ArrayList<String>();
		while (it.hasNext()) {
			line = it.next();

			// attempt to use the .split method as StringTokenizer is deprecated
			StringTokenizer st = new StringTokenizer(normalize(line),
					delimiters);
			while (st.hasMoreTokens()) {
				wordList.add(st.nextToken());
			}
		}
		return wordList;
	}

	// refactor to make this an object with a property like wordsToRemove
	private List<String> removeWords(List<String> wordList,
			List<String> wordsToRemove) {
		List<String> newWordList = new ArrayList<String>();
		Iterator<String> it = wordList.iterator();

		String word = null;
		while (it.hasNext()) {
			word = it.next();
			if (!wordsToRemove.contains(word)) {
				newWordList.add(word);
			}
		}

		return newWordList;
	}

	private String normalize(String word) {
		return word.toLowerCase().trim();
	}

	private List<String> readFile(String filename) throws Exception {
		FileReader file = new FileReader(filename);
		List<String> stringList = new ArrayList<String>();
		BufferedReader reader = new BufferedReader(file);
		String text = null;
		while ((text = reader.readLine()) != null) {
			stringList.add(text);
		}
		reader.close();

		return stringList;
	}

    public static void main(String[] args) throws Exception {
        if (args.length < 1){
            System.out.println("MP1 <User ID>");
        }
        else {
            String userName = args[0];
            String inputFileName = "./input.txt";
            MP1 mp = new MP1(userName, inputFileName);
            String[] topItems = mp.process();
            for (String item: topItems){
                System.out.println(item);
            }
        }
    }
}
