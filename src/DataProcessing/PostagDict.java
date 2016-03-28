package DataProcessing;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author susanti_2
 */
public class PostagDict {

    private final static String ADJ_DICT = "dict/adj_dict.txt";
    private final static String NEG_DICT = "dict/neg_dict.txt";
    private static ArrayList<String> adjDict;
    private static ArrayList<String> negDict;

    private static void initAdjDict() {
        adjDict = new ArrayList();

        BufferedReader fileReader;
        try {
            fileReader = new BufferedReader(new FileReader(ADJ_DICT));
            String line;
            while ((line = fileReader.readLine()) != null) {
                adjDict.add(line);
            }
        } catch (FileNotFoundException ex) {
            System.out.println("File adjective dictionary not found");
        } catch (IOException ex) {
            System.out.println("IO Exception: adjective dictionary");
        }
    }

    /**
     * get all adjectives in sentence
     * @param sentence sentence
     * @return adjectives
     */
    public static String findAdjective(String sentence) {
        initAdjDict();
        String s = "";

        String[] tokens = sentence.split("\\s+");
        int i = 0;

        while (i < tokens.length) {
            if (adjDict.contains(tokens[i])) {
                if (s.isEmpty()) {
                    s = tokens[i];
                } else {
                    s = s + "," + tokens[i];
                }
            }
            i++;
        }
        return s;
    }

    /**
     * return true if there is an adjective in sentence
     * @param sentence sentence 
     * @return true if there is an adjective in sentence, otherwise false
     */
    public static boolean containAdjective(String sentence) {
        return !findAdjective(sentence).isEmpty();
    }

    private static void initNegDict() {
        negDict = new ArrayList();

        BufferedReader fileReader;
        try {
            fileReader = new BufferedReader(new FileReader(NEG_DICT));
            String line;
            while ((line = fileReader.readLine()) != null) {
                negDict.add(line);
            }
        } catch (FileNotFoundException ex) {
            System.out.println("File negation dictionary not found");
        } catch (IOException ex) {
            System.out.println("IO Exception: negation dictionary");
        }
    }

    /**
     * get all negation in sentence
     * @param sentence sentence
     * @return negation 
     */
    public static String findNegation(String sentence) {
        initNegDict();
        String s = "";

        String[] tokens = sentence.split("\\s+");
        int i = 0;

        while (i < tokens.length) {
            if (negDict.contains(tokens[i])) {
                if (s.isEmpty()) {
                    s = tokens[i];
                } else {
                    s = s + "," + tokens[i];
                }
            }
            i++;
        }
        return s;
    }

    /**
     * true if there is a negative word in sentence
     * @param sentence sentence
     * @return true if there is a negative word in sentence, otherwise false
     */
    public static boolean containNegation(String sentence) {
        return !findNegation(sentence).isEmpty();
    }

}
