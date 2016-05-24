package Model;

import java.util.ArrayList;

/**
 *
 * @author susanti_2
 */
public class Rating {
    public static double getRating (ArrayList<AspectSentiment> aspectSentiments) {
        double sumPos = 0;
        for (AspectSentiment as: aspectSentiments) {
            if (as.isPositive()) {
                sumPos++;
            }
        }
        
        return (double)(4 * (sumPos/aspectSentiments.size()))+ 1;
    }
}
