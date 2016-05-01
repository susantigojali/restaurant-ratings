package Model;

/**
 *
 * @author susanti_2
 */
public class Feature {
    private String word;
    private String postag;

    public Feature(String word, String postag) {
        this.word = word;
        this.postag = postag;
    }

    public String getWord() {
        return word;
    }

    public String getPostag() {
        return postag;
    }
}
