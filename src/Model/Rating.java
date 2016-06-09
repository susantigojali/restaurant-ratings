package Model;

import java.util.ArrayList;

/**
 *
 * @author susanti_2
 */
public class Rating {

    /**
     * count the rating 
     * @param aspectSentiments list of aspectsentiments
     * @return rating 
     */
    public static double getRating (ArrayList<AspectSentiment> aspectSentiments) {
        double sumPos = 0;
        double sumNeg = 0;
        for (AspectSentiment as: aspectSentiments) {
            if (as.isPositive()) {
                sumPos++;
            }
            if (as.isNegative()) {
                sumNeg++;
            }
        }
        
        return (double)(4 * (sumPos/(sumNeg+sumPos)))+ 1;
    }
}
