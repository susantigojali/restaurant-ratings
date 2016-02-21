
package Model;

/**
 *
 * @author susanti_2
 */
public class Review {
    
    private String title;
    private String text;

    /**
     *
     * @param title review title 
     * @param text review text
     */
    public Review(String title, String text) {
        this.title = title;
        this.text = text;
    }

    /**
     *
     * @return review title 
     */
    public String getTitle() {
        return title;
    }

    /**
     *
     * @param title review title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     *
     * @return review text 
     */
    public String getText() {
        return text;
    }

    /**
     *
     * @param text review text 
     */
    public void setText(String text) {
        this.text = text;
    }
    
    /**
     * print review title and text
     */
    public void print() {
        System.out.println("Title: "+title);
        System.out.println("Text: "+text);
    }
}
