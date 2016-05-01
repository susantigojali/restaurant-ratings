package DataProcessing;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author susanti_2
 */
public class Dictionary {

    private static final String EMOTICON_DICT = "dict/emoticon_dict.txt";
    private static final String FOREIGN_WORD_DICT = "dict/fw_dict.txt";
    private static final String NEGATION_WORD_DICT = "dict/neg_dict.txt";
    private static final String COORDINATING_CONJUCTION_DICT = "dict/coordinating_conjuction_dict.txt";

    /**
     *
     * @return list of emoticons from dictionary
     */
    public static ArrayList<String> getEmoticonsDict() {
        //init emoticon list
        ArrayList<String> emoticonDict = new ArrayList<>();
        try {
            try (BufferedReader reader = new BufferedReader(new FileReader(EMOTICON_DICT))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    emoticonDict.add(line);
                }
            }
        } catch (FileNotFoundException ex) {
            System.out.println("File emoticon dictionary not found");
        } catch (IOException ex) {
            System.out.println("IO Excsption: emoticon dictionary");
        }
        return emoticonDict;
        
    }

    /**
     *
     * @return Map of foreign words to Indonesia from dictionary
     */
    public static HashMap<String, String> getForeignWordsDict() {
       HashMap<String, String> listFW = new HashMap<>();

        try {
            try (BufferedReader fileReader = new BufferedReader(new FileReader(FOREIGN_WORD_DICT))) {
                String line;
                while ((line = fileReader.readLine()) != null) {
                    String[] entry = line.split("\t");
                    listFW.put(entry[0], entry[1]);
                }
            }
        } catch (FileNotFoundException ex) {
            System.out.println("File foreign word dictionary not found");
        } catch (IOException ex) {
            System.out.println("IO Excsption: foreign word dictionary");
        }
        
        return listFW;
    }

    /**
     *
     * @return list of negation words from dictionary
     */
    public static ArrayList<String> getNegationWordsDict() {
        //init emoticon list
        ArrayList<String> negDict = new ArrayList<>();
        try {
            try (BufferedReader reader = new BufferedReader(new FileReader(NEGATION_WORD_DICT))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    negDict.add(line);
                }
            }
        } catch (FileNotFoundException ex) {
            System.out.println("File negation word dictionary not found");
        } catch (IOException ex) {
            System.out.println("IO Excsption: negation word dictionary");
        }

        return negDict;
    }
    
    /**
     *
     * @return list of coordinating conjuction from dictionary
     */
    public static ArrayList<String> getCoordinatingConjuctionsDict() {
        //init emoticon list
        ArrayList<String> ccDict = new ArrayList<>();
        try {
            try (BufferedReader reader = new BufferedReader(new FileReader(COORDINATING_CONJUCTION_DICT))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    ccDict.add(line);
                }
            }
        } catch (FileNotFoundException ex) {
            System.out.println("File coordinating conjunction word dictionary not found");
        } catch (IOException ex) {
            System.out.println("IO Excsption: coordinating conjunction word dictionary");
        }

        return ccDict;
    }
    
}
