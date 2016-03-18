package DataProcessing;

import Model.Review;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author susanti_2
 */
public class ReviewReader {

    private static final String DELIMITER = ";";
    private static final int REVIEW_TITLE = 0;
    private static final int REVIEW_TEXT = 1;

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
            Logger.getLogger(Preprocess.class.getName()).log(Level.SEVERE, null, ex);
        }
        return reviews;
    }
}
