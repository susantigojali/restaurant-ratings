package DataProcessing;

import Model.Review;
import IndonesianNLP.IndonesianSentenceDetector;
import IndonesianNLP.IndonesianSentenceFormalization;
import Model.Reader;
import inanlp.IndonesianSentenceFormalizer;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author susanti_2
 */
public class Preprocess {

    //Delimiter used in CSV file
    private static final String DELIMITER = ";";
    private static final String SPACE = " ";
    private static final String NEW_LINE_SEPARATOR = "\n";
//    private static final String FILE_HEADER = "no;sentence;formalized_sentence;class;sentence_position";
    private static final String FILE_HEADER = "no;sentence;formalized_sentence;class;sentence_position_binarized";

    /**
     * Split sentences to list of sentence
     *
     * @param sentences sentences
     * @return list of sentence from sentences
     */
    public static ArrayList<String> splitSentences(String sentences) {
        IndonesianSentenceDetector isd = new IndonesianSentenceDetector();
        ArrayList<String> sentencesList = isd.splitSentence(sentences);

//        ArrayList<String> sentencesList = new ArrayList<>();
//        String[] splits = sentences.split("\\.|\\?|!");
//        for (int i = 0; i < splits.length; i++) {
//            if (!splits[i].isEmpty()) {
//                sentencesList.add(splits[i].toString());
//            }
//        }   
        return sentencesList;
    }

    /**
     * Formalize sentence to formal Indonesia sentence using inanlp jar
     *
     * @param sentence sentence to be formalized
     * @return formalized sentence
     */
    public static String formalizeSentence(String sentence) {
        IndonesianSentenceFormalization formalizer = new IndonesianSentenceFormalization();
        return formalizer.formalizeSentence(sentence);
    }

    /**
     * Formalize sentence to formal Indonesia sentence using
     * IndonesianSentenceFormalizer
     *
     * @param sentence sentence to be formalized
     * @return formalized sentence
     */
    public static String formalizeSentence2(String sentence) {
        IndonesianSentenceFormalizer formalizer = new IndonesianSentenceFormalizer();
        return formalizer.formalizeSentence(formalizeForeignWord(sentence));
    }

    /**
     * Replace all foreign word in sentence with Indonesian
     *
     * @param sentence sentence to be formalized
     * @return formalized sentence
     */
    public static String formalizeForeignWord(String sentence) {
        HashMap<String, String> listFW = Dictionary.getForeignWordsDict();
        String newSentence = sentence;
        for (Entry<String, String> entry : listFW.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            newSentence = newSentence.replaceAll("\\b" + key + "\\b", value);
        }
        return newSentence;
    }

    /**
     * get array of emoticons from sentence
     *
     * @param sentence sentence
     * @return emoticons(unique)
     */
    public static ArrayList<String> getEmoticons(String sentence) {
        //init emoticon list
        ArrayList<String> emoticonDict = Dictionary.getEmoticonsDict();

        ArrayList<String> emoticonList = new ArrayList<>();
        String[] tokens = sentence.split("\\s+");

        for (String token : tokens) {
            if (emoticonDict.contains(token) && !emoticonList.contains(token)) {
                emoticonList.add(token);
            }
        }
        return emoticonList;
    }

    /**
     * delete stopword from sentence
     *
     * @param sentence sentence
     * @return sentence with no stopword
     */
    public static String deleteStopword(String sentence) {
        IndonesianSentenceFormalizer formalizer = new IndonesianSentenceFormalizer();
        formalizer.initStopword();
        ArrayList<String> listStopword = formalizer.getListStopword();

        return formalizer.deleteStopword(sentence);
//        String noSw = sentence;
//        for (String sw : listStopword) {
//            noSw = noSw.replaceAll("\\b" + sw + "\\b", " ");
//            //System.out.println(sw+" "+noSw);
//        }
//        return noSw;

    }

    private static String cleanTextForWeka(String sentence) {
        return sentence.replaceAll("\"|\'|%", "");
    }
    
    //convert ... to .
    private static String cleanText(String sentence) {
        return sentence.replaceAll("([^\\d])(\\.)+ ?([^\\d])", "$1\\. $3");
    }

    /**
     * Create CSV format file from split and formalized data
     *
     * @param reviews raw data of review
     * @param filename CSV format file name
     * @throws java.io.IOException Exception
     */
    public static void preprocessDataforNBC(ArrayList<Review> reviews, String filename) throws IOException {
        //write data to file
        File file = new File(filename);
        // creates the file
        if (!file.exists()) {
            file.createNewFile();
        }

        FileWriter fw = new FileWriter(file);
        try (BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write("sep=" + DELIMITER + NEW_LINE_SEPARATOR);
            String fileHeader = FILE_HEADER + DELIMITER + "adj_dict" + DELIMITER + Postag.HEADER;
            //for full feature
//            String fileHeader = FILE_HEADER + DELIMITER + "emoticon" + DELIMITER + "adj_dict" + DELIMITER
//                    + "neg_dict" + DELIMITER + Postag.HEADER;
            bw.write(fileHeader + NEW_LINE_SEPARATOR);

            int n = 1;
            for (Review review : reviews) {
                ArrayList<String> reviewText = splitSentences(cleanText(review.getText()));
                for (int i = 0; i < reviewText.size(); i++) {
                    bw.write(n + DELIMITER);
                    bw.write(reviewText.get(i).toLowerCase() + DELIMITER);
                    System.out.println(reviewText.get(i).toLowerCase());
                    
                    n++;
                    String newSentence = formalizeForeignWord(cleanText(reviewText.get(i).toLowerCase()));
                    String formalizedSentence = deleteStopword(formalizeSentence2(cleanTextForWeka(newSentence)));
                    bw.write(formalizedSentence + DELIMITER + "?" + DELIMITER);

                    //sentence position
                    if (i == 0) {
                        bw.write(1 + DELIMITER);
                    } else {
                        bw.write(0 + DELIMITER);
                    }

//                    ArrayList<String> emoticons = getEmoticons(reviewText.get(i).toLowerCase());
//                    if (!emoticons.isEmpty()) {
//                        bw.write(1 + DELIMITER);
//                    } else {
//                        bw.write(0 + DELIMITER);
//                    }

                    if (PostagDict.containAdjective(formalizedSentence)) {
                        bw.write(1 + DELIMITER);
                    } else {
                        bw.write(0 + DELIMITER);
                    }

//                    if (PostagDict.containNegation(formalizedSentence)) {
//                        bw.write(1 + DELIMITER);
//                    } else {
//                        bw.write(0 + DELIMITER);
//                    }

                    ArrayList<String[]> postag = Postag.doPOSTag(formalizedSentence);
                    String feature = Postag.createAllPostag(postag, DELIMITER);
                    bw.write(feature + NEW_LINE_SEPARATOR);
                }
            }
        }
    }

    /**
     * Create data train for sequence tagging from file
     *
     * @param inputFile input file name
     * @param outputFile output file name
     * @param isAnnotation true if for annotation, no if for testing application
     * @throws FileNotFoundException Exception file not found
     * @throws IOException IO Exception
     */
    public static void preprocessDataforSequenceTagging(String inputFile, String outputFile, boolean isAnnotation) throws FileNotFoundException, IOException {
        ArrayList<String> reviewsText = Reader.readReviewText(inputFile);

        //write data to file
        File file = new File(outputFile);
        // creates the file
        if (!file.exists()) {
            file.createNewFile();
        }

        FileWriter fw = new FileWriter(file);
        try (BufferedWriter bw = new BufferedWriter(fw)) {
            if (isAnnotation) {
                bw.write("sep=" + DELIMITER + NEW_LINE_SEPARATOR);
            }

            for (String reviewText : reviewsText) {
                ArrayList<String[]> postag = Postag.doPOSTag(reviewText);
                for (String[] pos : postag) {
                    if (PostagDict.containAdjective(pos[0])) {
                        if (pos[1].compareTo(Postag.ADJECTIVE) != 0 ) {
                            System.out.print(pos[0] + " "+ pos[1]+ " ");
                            count(pos);
                            
                            pos[1] =  Postag.ADJECTIVE;
                            System.out.println(pos[1]);
                        }
                    }
                    
                    if (isAnnotation) {
                        bw.write(pos[0] + DELIMITER + pos[1] + NEW_LINE_SEPARATOR);
                    } else {
                        bw.write(pos[0] + SPACE + pos[1] + NEW_LINE_SEPARATOR);
                    }
                }
                bw.write(NEW_LINE_SEPARATOR);
            }
        }
        
        System.out.println(adj.size());
        int sum=0;
        for (String[] s : adj.keySet()) {
            System.out.println(s[0] + " "+ s[1] + " : " + adj.get(s));
            sum += adj.get(s);
        }
        System.out.println("sum: "+sum);
    }
    
    public static HashMap<String[], Integer> adj = new HashMap<>();
    
    private static void count(String[] postag) {
        boolean found = false;
        for (String[] s : adj.keySet()) {
            //System.out.println(s[0]+" "+ postag[0] + " "+ s[1]+ " "+ postag[1]);
            if (s[0].contentEquals(postag[0]) && s[1].contentEquals(postag[1]) ) {
                found = true;
                adj.put(s, adj.get(s) + 1);
            } 
        }
        if (!found) {
            //System.out.println("not found");
            String[] newPostag = new String[2];
            newPostag[0] = postag[0];
            newPostag[1] = postag[1];
            
            adj.put(newPostag, 1);
        }   
    }
    
    public static void main(String[] args) {
        //Prepare data NBC for learning
//        ArrayList<Review> reviews = new ArrayList<>();
//
//        String fileInput = "crawl-data/DataLearningNBC2 (120).csv";
//        try {
//            reviews = Reader.readReviewFromFile(fileInput);
//        } catch (FileNotFoundException ex) {
//            System.out.println("File not found");
//        }
//
//        String filename = "dataset/NBC/DataLearningNBC2 (120) NBCAnotasi 3 juni.csv";
//        try {
//            Preprocess.preprocessDataforNBC(reviews, filename);
//        } catch (IOException ex) {
//            Logger.getLogger(Preprocess.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        System.exit(-1);

//        Prepare data for CRF
        String inputFile = "dataset/CRF/rawdata1 (205).txt";
        String outputFile = "dataset/CRF/CRFAnotasi1 new.csv"; //test(dibuang aja nanti, ga penting, buat liat doang).csv";
        int type = 0;
        try {
            Preprocess.preprocessDataforSequenceTagging(inputFile, outputFile, true);
        } catch (IOException ex) {
            Logger.getLogger(Preprocess.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.exit(-1);

        String sentence = "untuk minumannya yang greentea segar banget .";

        String newSentence = formalizeForeignWord(cleanText(sentence.toLowerCase()));
        String formalizedSentence = deleteStopword(formalizeSentence2(cleanTextForWeka(newSentence)));
        
        System.out.println(formalizedSentence);
//        ArrayList<String> splits = Preprocess.splitSentences(formalizedSentence);
//        for (String split : splits) {
//            System.out.println(split);
//        }
//
        ArrayList<String[]> postag = Postag.doPOSTag(formalizedSentence);
        for (String[] pos : postag) {
            if (PostagDict.containAdjective(pos[0])) {
                if (pos[1].compareTo(Postag.ADJECTIVE) != 0 ) {
                    pos[1] =  Postag.ADJECTIVE;
                }
            }
            System.out.println(pos[0] + " " + pos[1]);
        }
    }

}
