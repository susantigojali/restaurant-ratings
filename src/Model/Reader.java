package Model;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author susanti_2
 */
public class Reader {

    private static final String DELIMITER = ";";
    private static final int REVIEW_TITLE = 0;
    private static final int REVIEW_TEXT = 1;

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

}
