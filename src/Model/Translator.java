package Model;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author susanti_2
 */
public class Translator {

    private static final String DICTIONARY_FILENAME = "dict/MMT-Database/BASIC/MASTER";
    private static final LinkedHashMap<String, ArrayList<String>> dicts = new LinkedHashMap<>();
    
    private static void initDict() throws FileNotFoundException {
        BufferedReader fileReader = new BufferedReader(new FileReader(DICTIONARY_FILENAME));
        String line;

        try {
            String word = "";
            while ((line = fileReader.readLine()) != null) {
                if (!line.equals("")) {
                    if (line.startsWith("%1")) {
                        word = line.substring(2, line.length());
                    }
                    if (line.startsWith("%4")) {
                        String trans = line.substring(2, line.length());
                        addToDictionary(word, trans);
                    }
                    //derivation
                    if (line.startsWith("&") && line.substring(3, 5).equals("11") ) {
                        word = line.substring(5, line.length());
                    }
                    if (line.startsWith("#") && line.substring(3, 5).equals("00") ) {
                        String trans = line.substring(5, line.length());
                        addToDictionary(word, trans);
                    }
                } 
            }
        } catch (IOException ex) {
            Logger.getLogger(Translator.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        System.out.println("size: "+dicts.size());
//        for (String key : dicts.keySet()) {
//            ArrayList<String> value = dicts.get(key);
//            System.out.println(key + "=" +value.toString());
//        }

    }
    
    private static void addToDictionary(String key, String word) {
        String trans = word.replaceAll("\\(.*\\)", ""); //delete ()
        trans = trans.replaceAll("to ", ""); //delete ()
        ArrayList<String> translations = new ArrayList<>(Arrays.asList(trans.trim().split(";")));
        for (int i = 0; i < translations.size(); i++) {
            translations.set(i, translations.get(i).trim());
        }

        if (dicts.containsKey(key)) {
            HashSet temp = new HashSet(dicts.get(key));
            temp.addAll(translations);
            translations = new ArrayList(temp);
        }
        dicts.put(key, translations);
    }
    
    /**
     * translate the word
     * @param word word to be translated
     * @return list of translation of the word 
     * @throws FileNotFoundException dictionary not found
     */
    public static ArrayList<String> getTranslation(String word) throws FileNotFoundException {
        if (dicts.isEmpty()) {
            initDict();
        }
        return dicts.get(word);
    }
    
    /**
     *
     * @param args
     */
    public static void main (String args[]) {
        try {
            initDict();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Translator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
