package Model;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author susanti_2
 */
public class AspectAggregation {

    private static final String DICTIONARY_FILENAME = "dict/category_aspect_dict.txt";

    public static final String OTHER_CATEGORY = "other";

    private static final LinkedHashMap<String, ArrayList<String>> aspectCategoryDicts = new LinkedHashMap<>();

    private static void intDict() throws FileNotFoundException {
        BufferedReader fileReader = new BufferedReader(new FileReader(DICTIONARY_FILENAME));
        String line;

        try {
            String categoryName = "";
            ArrayList<String> categories = new ArrayList<>();
            while ((line = fileReader.readLine()) != null) {
                if (!line.equals("")) {
                    if (line.startsWith("#")) {
                        categoryName = line.substring(1);
                        categories = new ArrayList<>();
                    } else {
                        categories.add(line);
                    }
                } else {
                    aspectCategoryDicts.put(categoryName, categories);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Translator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * aggregate aspect sentiment based on dictionary
     *
     * @param aspectSentiments list of aspect sentiment to be aggregated
     * @return aggregation of aspect sentiment
     * @throws FileNotFoundException dictionary not found
     */
    public static LinkedHashMap<String, ArrayList<AspectSentiment>> aggregation(ArrayList<AspectSentiment> aspectSentiments) throws FileNotFoundException {
        LinkedHashMap<String, ArrayList<AspectSentiment>> aspectAggregations = new LinkedHashMap<>();
        for (AspectSentiment as : aspectSentiments) {
            
            String aspect = as.getAspect();
            if (aspect.endsWith("nya")) {
                aspect = aspect.substring(0, aspect.length()-3);
            }
            String category = getCategory(aspect);
            System.out.println("category " + as.getAspect() + " = " + category);
            if (aspectAggregations.containsKey(category)) {
                ArrayList<AspectSentiment> newAS = new ArrayList<>(aspectAggregations.get(category));
                newAS.add(as);
                aspectAggregations.put(category, newAS);
            } else {
                ArrayList<AspectSentiment> newAS = new ArrayList<>();
                newAS.add(as);
                aspectAggregations.put(category, newAS);
            }
        }

        return aspectAggregations;
    }

    /**
     * categorize aspect based on dictionary
     *
     * @param aspect aspect
     * @return category of aspect
     * @throws FileNotFoundException dictionary not found
     */
    public static String getCategory(String aspect) throws FileNotFoundException {
        if (aspectCategoryDicts.isEmpty()) {
            intDict();
        }

        String category = null;
        ArrayList<String> trans = Translator.getTranslation(aspect);

        //no translate found
        if (trans == null) {
            category = OTHER_CATEGORY;
        } else {
            double max = Double.NEGATIVE_INFINITY;
            boolean found = false;

            for (String categoryName : aspectCategoryDicts.keySet()) {
                ArrayList<String> value = aspectCategoryDicts.get(categoryName);

                for (int i = 0; i < value.size() && !found; i++) {
                    String cat = value.get(i);
                    for (int j = 0; j < trans.size() && !found; j++) {
                        if (cat.compareTo(trans.get(j)) == 0) {
                            found = true;
                            category = categoryName;
                        } else {
                            double jcn = Wordnet.jcn(trans.get(j), cat);
                            if (jcn > max) {
                                max = jcn;
                                System.out.println("max : "+ max + " "+ cat);
                                category = categoryName;
                            }
                        }
                    }
                }
            }
            
            if (max == 0.0) {
                category = OTHER_CATEGORY;
            }
        }
        return category;
    }

    public static void main(String args[]) {
        try {
            ArrayList<AspectSentiment> aspectSentiments = new ArrayList<>();
            aspectSentiments.add(new AspectSentiment("makanannya", "enak", 1));
            aspectSentiments.add(new AspectSentiment("harga", "murah", 1));
            aspectSentiments.add(new AspectSentiment("makanan", "lezat", 1));
            LinkedHashMap<String, ArrayList<AspectSentiment>> aggregation = aggregation(aspectSentiments);

            System.out.println(aggregation.size());
            for (String key : aggregation.keySet()) {
                ArrayList<AspectSentiment> value = aggregation.get(key);
                System.out.println(key + "========= ");
                for (int i = 0; i < value.size(); i++) {
                    System.out.println(value.get(i).getAspect() + " " + value.get(i).getSentiment());
                }
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(AspectAggregation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
