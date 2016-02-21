package WebScraper;

import Model.Review;
import com.jaunt.Document;
import com.jaunt.Element;
import com.jaunt.Elements;
import com.jaunt.JauntException;
import com.jaunt.NotFound;
import com.jaunt.UserAgent;
import com.jaunt.util.ProxyAuthenticator;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.commons.lang3.StringEscapeUtils;

/**
 *
 * @author susanti_2
 */
public class TAScraper {

    private String url;
    private String restoName;
    private final UserAgent userAgent;

    /**
     * Constructor TAScraper
     */
    public TAScraper() {
        url = null;
        restoName = null;

        userAgent = new UserAgent();
        userAgent.settings.autoSaveAsHTML = true;
        //userAgent.settings.showHeaders = true;
        userAgent.cachingEnabled();
        //System.out.println("SETTINGS:\n" + userAgent.settings);      //print the userAgent's default settings.
    }

    /**
     * get URL
     *
     * @return URL
     */
    public String getUrl() {
        return url;
    }

    /**
     * get restaurant name
     *
     * @return restaurant name
     */
    public String getRestoName() {
        return restoName;
    }

    /**
     * set proxy to visit URL
     *
     * @param proxyHost proxy host
     * @param proxyPort proxy port
     * @param username username
     * @param password password
     */
    public void setProxyITB(String proxyHost, String proxyPort, String username, String password) {
        //specify http proxy at System level.
        System.setProperty("http.proxyHost", proxyHost);
        System.setProperty("http.proxyPort", proxyPort);

        //specify https proxy at System level.
        System.setProperty("https.proxyHost", proxyHost);
        System.setProperty("https.proxyPort", proxyPort);

        //specify username/password credentials at System level.
        ProxyAuthenticator.setCredentials(username, password);
    }

    /**
     * get document from URL
     *
     * @param url URL
     * @return document from URL
     * @throws JauntException exception if can not visit URL
     */
    public Document visit(String url) throws JauntException {
        this.url = url;
        userAgent.visit(url);
        return userAgent.doc;
    }

    /**
     * get restaurant ID from URL
     *
     * @param domain domain
     * @return restaurant ID from the URL
     */
    private String getRestoID(String domain) {
        if (domain.compareTo("com") == 0) {
            return url.substring(45, 61);
        } else { //co.id
            return url.substring(48, 64);
        }
    }

    /**
     * get reviews ID from reviews
     *
     * @param reviews elements from class <reviewSelector   track_back> or
     * <reviewSelector>
     * return all reviews ID
     */
    private String getReviewsID(Elements reviews) {
        String idReviews = "";

        for (Element review : reviews) {
            String id = review.getAtString("id");
            String idReview = id.substring(id.indexOf('_') + 1);

            if (review.getAtString("class").compareTo("reviewSelector   track_back") == 0) {
                idReviews = idReview;
            } else {
                idReviews = idReviews + "," + idReview;
            }
        }
        return idReviews;
    }

    /**
     * get target ID from reviews to open review
     *
     * @param reviews elements from class <reviewSelector   track_back> or
     * <reviewSelector>
     * return target ID
     */
    private String getTargetReview(Elements reviews) {
        boolean targetFound = false;
        String target = null;

        for (Element review : reviews) {
            String id = review.getAtString("id");
            if (!targetFound) {
                try {
                    Element temp = review.findFirst("<div class=entry>").findFirst("<span class='partnerRvw'>");
                    target = id.substring(id.indexOf('_') + 1);
                    targetFound = true;
                } catch (NotFound ex) {
                }
            }
        }
        return target;
    }

    /**
     * find all review from TripAdvisor URL
     *
     * @param url URL
     * @return list of reviews
     */
    public ArrayList<Review> findReviews(String url) {
        ArrayList<Review> reviews = new ArrayList<>();

        Document doc;
        try {
            doc = visit(url);
            try {
                restoName = StringEscapeUtils.unescapeHtml4(doc.findFirst("<h1>").getText()); //get child text of title element.
                //expanded link
                String restoCode = getRestoID("co.id");
                Elements reviewsElement = doc.findEvery("<div class='reviewSelector  |reviewSelector   track_back|reviewSelector    track_back'>");
                String target = getTargetReview(reviewsElement);

                System.out.println("Target: " + target + " Review size: " + reviewsElement.size());

                if (target == null) { //no need to expand
                    for (Element e : reviewsElement) {
                        String title = cleanText(e.findFirst("<span class='noQuotes'>").innerText());
                        String reviewText = cleanText(e.findFirst("<p class='partial_entry'>").innerText());
                        reviews.add(new Review(title, reviewText));
                        System.out.println(title + ";" + reviewText);
                    }
                } else {
                    String reviewsLink = getReviewsID(reviewsElement);
                    String[] idReviews = reviewsLink.split(",");
                    String expandedReviewlink = url.substring(0, url.indexOf("/", 8) + 1) + "ExpandedUserReviews-" + restoCode
                            + "?target=" + target + "&context=1&reviews=" + reviewsLink + "&servlet=Restaurant_Review&expand=1";
                    reviews = findExpandedReviews(expandedReviewlink, idReviews);
                }
                return reviews;
            } catch (NotFound ex) {
                System.out.println("HTML Not found");
            }
        } catch (JauntException ex) {
            System.out.println("Can not Visit: " + url);
        }
        return reviews;
    }
    
    /**
     * find all review from expanded TripAdvisor URL 
     *
     * @param expandedReviewlink expanded URL
     * @param idReviews all id reviews
     * @return list of reviews
     */
    private ArrayList<Review> findExpandedReviews(String expandedReviewlink, String[] idReviews) {
        ArrayList reviews = new ArrayList();

        try {
            //visit expanded review link
            Document expandedDoc = visit(expandedReviewlink);

            for (String idReview : idReviews) {
                try {
                    Element e = expandedDoc.findFirst("<div id='expanded_review_" + idReview + "'>");
                    String title = cleanText(e.findFirst("<span class='noQuotes'>").innerText());
                    String reviewText = cleanText(e.findFirst("<div class='entry'>").innerText());
                    reviews.add(new Review(title, reviewText));
                    System.out.println(title + ";" + reviewText);
                } catch (NotFound ex) {
                    System.out.println("HTML not found");
                }
            }
        } catch (JauntException ex) {
            System.out.println("Can not visits expand link: " + expandedReviewlink);
        }
        return reviews;
    }
    
    /**
     * clean the text (unescapeHTML, trim, replace "\n" to " ", replace ";" to ".")
     *
     * @param text text to be clean
     * @return clean text
     */
    private String cleanText(String text) {
        return StringEscapeUtils.unescapeHtml4(text).trim().replace("\n", " ").replace(";", ".");
    }

    /**
     * create CSV file that contains restaurant review
     *
     * @param reviews restaurant's reviews
     * @param filename file name
     * @throws IOException exception IO
     */
    public void exportReviewToCSV(ArrayList<Review> reviews, String filename) throws IOException {
        String DELIMITER = ";";
        String NEW_LINE_SEPARATOR = "\n";
        String FILE_HEADER = "title;text";

        //write data to file
        File file = new File("crawl-data/" + filename);
        FileWriter fw;
        boolean isFileExist = false;
        // creates the file
        if (!file.exists()) {
            file.createNewFile();
            fw = new FileWriter(file);

        } else {
            fw = new FileWriter(file, true);
            isFileExist = true;
        }

        try (BufferedWriter bw = new BufferedWriter(fw)) {
            if (!isFileExist) {
                bw.write("sep=" + DELIMITER + NEW_LINE_SEPARATOR);
                bw.write(FILE_HEADER + NEW_LINE_SEPARATOR);
            }

            for (Review review : reviews) {
                bw.write(review.getTitle() + DELIMITER + review.getText() + NEW_LINE_SEPARATOR);
            }
        }
    }

    public static void main(String[] args) {
        //set proxy
        String host = "cache.itb.ac.id";
        String port = "8080";
        String username = "susanti.gojali";
        String password = "110294";

        String url = "https://www.tripadvisor.co.id/Restaurant_Review-g297704-d808584-Reviews-Atmosphere_Resort_Cafe-Bandung_West_Java_Java.html";
        int n = 50; //Jumlah review(kelipatan 10)

        String urlTemp = url;
        TAScraper ws = new TAScraper();
        //ws.setProxyITB(host, port, username, password);

        ArrayList<Review> reviews = new ArrayList<>();
        for (int i = 10; i <= n; i += 10) {
            System.out.println("\nVisiting: " + urlTemp);
            boolean addAll = reviews.addAll(ws.findReviews(urlTemp));
            urlTemp = url.substring(0, url.indexOf("Reviews")) + "Reviews-or" + i + url.substring(url.indexOf("Reviews") + 7, url.length());
        }

        try {
            ws.exportReviewToCSV(reviews, ws.getRestoName() + ".csv");
        } catch (IOException ex) {
            System.out.println("Can not export to CSV");
        }

        System.out.println("done!!! ");

        //        try {
//            Cookie cookie = new Cookie("http://www.tripadvisor.com", "TALanguage=in");
//            ws.userAgent.cookies.addCookie(cookie);
//            System.out.println("cookie:  " + ws.getUserAgent().cookies.getCookies().toString());
//        } catch (Exception ex) {
//            Logger.getLogger(TAScraper.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }

}
