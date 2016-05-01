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
    private static final String NEW_LINE_SEPARATOR = "\n";
    private static final String FILE_HEADER = "no;sentence;formalized_sentence;class;sentence_position";

    /**
     * Split sentences to list of sentence
     *
     * @param sentences sentences
     * @return list of sentence from sentences
     */
    public static ArrayList<String> splitSentences(String sentences) {
        IndonesianSentenceDetector isd = new IndonesianSentenceDetector();
        ArrayList<String> sentencesList = isd.splitSentence(sentences);

//        ArrayList<String> sentence = new ArrayList<>();
//        for (String s : sentencesList) {
//            String[] splits = s.split("\\.|\\?|!");
//            sentence.addAll(Arrays.asList(splits));
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
            String fileHeader = FILE_HEADER + DELIMITER
                    + "emoticon" + DELIMITER + "adj_dict" + DELIMITER
                    + "neg_dict" + DELIMITER
                    + Postag.HEADER;
            bw.write(fileHeader + NEW_LINE_SEPARATOR);

            int n = 1;
            for (Review review : reviews) {
                ArrayList<String> reviewText = splitSentences(review.getText());
                for (int i = 0; i < reviewText.size(); i++) {
                    bw.write(n + DELIMITER);
                    n++;

                    bw.write(reviewText.get(i).toLowerCase() + DELIMITER);
                    String newSentence = formalizeForeignWord(reviewText.get(i).toLowerCase());
                    String formalizedSentence = deleteStopword(formalizeSentence2(cleanTextForWeka(newSentence)));
                    bw.write(formalizedSentence + DELIMITER + "?" + DELIMITER);

                    //sentence position
                    if (i == 0) {
                        bw.write(1 + DELIMITER);
                    } else {
                        bw.write(0 + DELIMITER);
                    }
                   
                    ArrayList<String> emoticons = getEmoticons(reviewText.get(i).toLowerCase());
                    if (!emoticons.isEmpty()) {
                        bw.write(1 + DELIMITER);
                    } else {
                        bw.write(0 + DELIMITER);
                    }

                    if (PostagDict.containAdjective(formalizedSentence)) {
                        bw.write(1 + DELIMITER);
                    } else {
                        bw.write(0 + DELIMITER);
                    }

                    if (PostagDict.containNegation(formalizedSentence)) {
                        bw.write(1 + DELIMITER);
                    } else {
                        bw.write(0 + DELIMITER);
                    }

                    ArrayList<String[]> postag = Postag.doPOSTag(formalizedSentence);
                    String feature = Postag.createAllPostag(postag, DELIMITER);
                    bw.write(feature + NEW_LINE_SEPARATOR);
                }
            }
        }
    }

    /**
     * Create data train for sequence tagging from file
     * @param inputFile input file name
     * @param outputFile output file name
     * @param type type : 0 = lexical and postag, 1 = lexical, 2 = postag
     * @throws FileNotFoundException Exception file not found
     * @throws IOException IO Exception
     */
    public static void preprocessDataforSequenceTagging(String inputFile, String outputFile, int type) throws FileNotFoundException, IOException {
        ArrayList<String> reviewsText = Reader.readReviewText(inputFile);

        //write data to file
        File file = new File(outputFile);
        // creates the file
        if (!file.exists()) {
            file.createNewFile();
        }

        FileWriter fw = new FileWriter(file);
        try (BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write("sep=" + DELIMITER + NEW_LINE_SEPARATOR);
            
            for (String reviewText : reviewsText) {
                ArrayList<String[]> postag = Postag.doPOSTag(reviewText);
                for (String[] pos: postag) {
                    if (type == 0) {
                        bw.write(pos[0] + DELIMITER + pos[1] + NEW_LINE_SEPARATOR);
                    } else if (type == 1) {
                        bw.write(pos[0] + NEW_LINE_SEPARATOR);
                    } else if (type == 2) {
                        bw.write(pos[1] + NEW_LINE_SEPARATOR);
                    }
                }
                bw.write(NEW_LINE_SEPARATOR);
            }
        }
    }
    
    public static void main(String[] args) {
        //NBC
//        ArrayList<Review> reviews = new ArrayList<>();
//
//        String fileInput = "crawl-data/Test1_FULL_SAMPLE (120).csv";
//        try {
//            reviews = Reader.readReviewFromFile(fileInput);
//        } catch (FileNotFoundException ex) {
//            System.out.println("File not found");
//        }
//
//        String filename = "dataset/testes.csv";
//        try {
//            Preprocess.preprocessDataforNBC(reviews, filename);
//        } catch (IOException ex) {
//            Logger.getLogger(Preprocess.class.getName()).log(Level.SEVERE, null, ex);
//        }

        //HMM
        String inputFile = "dataset/HMM/rawdata.txt";        
        String outputFile = "dataset/HMM/HMMtrainFull.csv";
        int type = 0;
        try {
            Preprocess.preprocessDataforSequenceTagging(inputFile, outputFile, type);
        } catch (IOException ex) {
            Logger.getLogger(Preprocess.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        ////////////
        
//        String sentence = "Biasanya untuk cari suasana indah hrs ke daerah Dago dll ? Ini tengah kota tapi suasana ya ga kalah cantik.. Saya suka makanannya.. Suasana nya.. Rasanya.. Harga emang aga mahal tapi untuk kepuasan yg didapat.. Relaaaaa";
//        sentence = "pada saat saya datang hari minggu pk.08.00 sepi tapi pada saat saya selesai banyak orang menunggu di luar .";
//        //System.out.println(Preprocess.cleanTextForWeka(Preprocess.formalizeSentence2(sentence).replaceAll("\"|%", "")));
//
//        sentence = Preprocess.deleteStopword(formalizeSentence2(Preprocess.formalizeForeignWord(Preprocess.deleteStopword(sentence))));
//
//        String newSentence = formalizeForeignWord(sentence);
//        String formalizedSentence = deleteStopword(formalizeSentence2(cleanTextForWeka(newSentence)));

//        ArrayList<String> splits = Preprocess.splitSentences(sentence);
//        //ArrayList<String> splits = Preprocess.splitSentences(Preprocess.deleteStopword(formalizeSentence2(sentence)));
//        for (String split : splits) {
//            System.out.println(split);
//        }
//
//        ArrayList<String[]> postag = Postag.doPOSTag(Preprocess.deleteStopword(formalizeSentence2(sentence)));
//        for (String[] doPOSTag1 : postag) {
//            System.out.println(doPOSTag1[0] + " " + doPOSTag1[1]);
//        }
    }

}
