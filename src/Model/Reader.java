package Model;

import cc.mallet.types.Sequence;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 *
 * @author susanti_2
 */
public class Reader {

    private static final String DELIMITER = ";";
    private static final int REVIEW_TITLE = 0;
    private static final int REVIEW_TEXT = 1;

    private static final String SPACE = " ";
    private static final String TAB = "\\t";

    /**
     * Parsing review from the file
     *
     * @param fileName file name
     * @return list of reviews
     * @throws FileNotFoundException Exception if file can not be found
     */
    public static ArrayList<Review> readReviewFromFile(String fileName) throws FileNotFoundException {

        ArrayList<Review> reviews = new ArrayList<>();
        BufferedReader fileReader = new BufferedReader(new FileReader(fileName));

        try {
            fileReader.readLine(); //read separator
            fileReader.readLine(); //read header
            String line;

            while ((line = fileReader.readLine()) != null) {
                //Get all tokens available in line
                String[] tokens = line.split(DELIMITER);

                if (tokens.length > 0) {
                    Review review = new Review(tokens[REVIEW_TITLE], tokens[REVIEW_TEXT]);
                    reviews.add(review);
                }
            }
        } catch (IOException ex) {
            System.out.println("IO Exception: readReviewFromFile");
        }
        return reviews;
    }

    /**
     * Read reviews text from the file
     *
     * @param fileName file name
     * @return list of reviews text
     * @throws FileNotFoundException Exception if file can not be found
     */
    public static ArrayList<String> readReviewText(String fileName) throws FileNotFoundException {
        ArrayList<String> reviewsText = new ArrayList<>();
        BufferedReader fileReader = new BufferedReader(new FileReader(fileName));

        try {
            String line;
            while ((line = fileReader.readLine()) != null) {
                reviewsText.add(line);
            }

        } catch (IOException ex) {
            System.out.println("IO Exception: readReviewText");
        }

        return reviewsText;
    }

    /**
     * Read sequence from file
     *
     * @param fileName sequence tagging file
     * @return List of Sequence Tagging
     * @throws FileNotFoundException Exception if file can not be found
     */
    public static ArrayList<SequenceTagging> readSequenceInput(String fileName) throws FileNotFoundException {
        ArrayList<SequenceTagging> sequenceTaggings = new ArrayList<>();
        BufferedReader fileReader = new BufferedReader(new FileReader(fileName));

        try {
            String line;
            ArrayList<Feature> sequenceInput = new ArrayList();

            while ((line = fileReader.readLine()) != null) {
                if (!line.isEmpty()) {
                    String[] token = line.split(SPACE);
                    assert token.length == 2;
                    sequenceInput.add(new Feature(token[0], token[1]));
                } else {
                    if (!sequenceInput.isEmpty()) {
                        SequenceTagging st = new SequenceTagging(sequenceInput, null);
                        sequenceTaggings.add(st);
                        sequenceInput = new ArrayList();
                    }
                }
            }
            if (!sequenceInput.isEmpty()) {
                sequenceTaggings.add(new SequenceTagging(sequenceInput, null));
            }

        } catch (IOException ex) {
            System.out.println("IO Exception: readSequenceTagging");
        }

        return sequenceTaggings;
    }

    /**
     * Read list of list of aspect sentiment form file
     * @param fileName file name
     * @return list of list of aspect and sentiment
     * @throws FileNotFoundException Exception if file can not be found
     */
    public static ArrayList<ArrayList<AspectSentiment>> readActualAspectSentiment(String fileName) throws FileNotFoundException {
        ArrayList<ArrayList<AspectSentiment>> actualAspectSentiments = new ArrayList<>();
        BufferedReader fileReader = new BufferedReader(new FileReader(fileName));

        try {
            String line;
            ArrayList<AspectSentiment> as = new ArrayList();

            while ((line = fileReader.readLine()) != null) {
                if (!line.isEmpty()) {
                    String[] token = line.split(TAB);
                    assert token.length == 2;
                    as.add(new AspectSentiment(token[0], token[1], 0));
                } else {
                    if (!as.isEmpty()) {
                        actualAspectSentiments.add(as);
                        as =  new ArrayList();
                    }
                }
            }
            if (!as.isEmpty()) {
                actualAspectSentiments.add(as);
            }

        } catch (IOException ex) {
            System.out.println("IO Exception: readActualAspectSentiment");
        }

        return actualAspectSentiments;
    }

    /**
     * Read list of aspect categories from file
     * @param fileName file name
     * @return list of aspect categories
     * @throws FileNotFoundException Exception if file can not be found
     */
    public static ArrayList<LinkedHashMap<String, ArrayList<AspectSentiment>>> readAspectAggregation(String fileName) throws FileNotFoundException {
        ArrayList<LinkedHashMap<String, ArrayList<AspectSentiment>>> actualAspectAggregations = new ArrayList<>();
        BufferedReader fileReader = new BufferedReader(new FileReader(fileName));

        try {
            String line;
            ArrayList<AspectSentiment> as = new ArrayList();
            LinkedHashMap<String, ArrayList<AspectSentiment>> aspectAggregations = new LinkedHashMap<>();

            while ((line = fileReader.readLine()) != null) {
                if (!line.isEmpty()) {
                    String[] token = line.split(TAB);
                    assert token.length == 3;
                    String category = token[2];
                    if (aspectAggregations.containsKey(category)) {
                        ArrayList<AspectSentiment> newAS = new ArrayList<>(aspectAggregations.get(category));
                        newAS.add(new AspectSentiment(token[0], token[1], 0));
                        aspectAggregations.put(category, newAS);
                    } else {
                        ArrayList<AspectSentiment> newAS = new ArrayList<>();
                        newAS.add(new AspectSentiment(token[0], token[1], 0));
                        aspectAggregations.put(category, newAS);
                    }
                } else {
                    if (!aspectAggregations.isEmpty()) {
                        actualAspectAggregations.add(aspectAggregations);
                        aspectAggregations = new LinkedHashMap<>();                       
                    }
                }
            }
            if (!aspectAggregations.isEmpty()) {
                actualAspectAggregations.add(aspectAggregations);                 
            }

        } catch (IOException ex) {
            System.out.println("IO Exception: readActualAspectAggregation");
        }
        
        return actualAspectAggregations;
    }
}
