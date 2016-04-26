package Model;

/**
 *
 * @author susanti_2
 */
public class AspectSentiment {
    private String aspect;
    private String sentiment;
    private int orientation; //pos = 1, neg = -1, netral = 0

    public AspectSentiment(String aspect, String sentiment, int orientation) {
        this.aspect = aspect;
        this.sentiment = sentiment;
        this.orientation = orientation;
    }
    
    public void print() {
        System.out.println("Aspect: "+ aspect );
        System.out.println("Sentiment: " + sentiment );
        System.out.println("Orientation: "+ orientation );
    }

    public String getAspect() {
        return aspect;
    }

    public String getSentiment() {
        return sentiment;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setAspect(String aspect) {
        this.aspect = aspect;
    }

    public void setSentiment(String sentiment) {
        this.sentiment = sentiment;
    }

    public void setorientation(int orientation) {
        this.orientation = orientation;
    }
    
    public void setPosOrientation() {
        this.orientation = 1;
    }
    
    public void setNegOrientation() {
        this.orientation = -1;
    }
    
    public boolean isPositive(){
        return orientation == 1;
    }
    
    public boolean isNegative(){
        return orientation == -1;
    }
    
    public boolean isNetral(){
        return orientation == 0;
    }
}
