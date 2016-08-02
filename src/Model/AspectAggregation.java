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

    // initialize dictionary
    private static void initDict() throws FileNotFoundException {
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
     *
     * @return list of aspect categories
     */
    public static ArrayList<String> getAspectCategories() {
        if (aspectCategoryDicts.isEmpty()) {
            try {
                initDict();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }

        ArrayList<String> labels = new ArrayList<>();
        for (String categoryName : aspectCategoryDicts.keySet()) {
            labels.add(categoryName);
        }
        labels. add(OTHER_CATEGORY);

        return labels;
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
            String category = getCategory2(aspect);
//            System.out.println("category " + as.getAspect() + " = " + category);
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
     * @param aspects aspects
     * @return category of aspect
     * @throws FileNotFoundException dictionary not found
     */
    public static String getCategory(String aspects) throws FileNotFoundException {
        if (aspectCategoryDicts.isEmpty()) {
            initDict();
        }

        String category = null;
        double max = Double.NEGATIVE_INFINITY;
        boolean found = false;
        
        String aspectTemp = aspects;
        if (aspects.endsWith("nya")) {
            aspectTemp = aspects.substring(0, aspects.length() - 3);
        }
        
        ArrayList<String> translates = Translator.getTranslation(aspectTemp);
        if (translates != null) { //translate all words
            for (String categoryName : aspectCategoryDicts.keySet()) {
                ArrayList<String> value = aspectCategoryDicts.get(categoryName);

                for (int i = 0; i < value.size() && !found; i++) {
                    String cat = value.get(i);
                    
                    for (int j = 0; j < translates.size() && !found; j++) {
                        if (cat.compareTo(translates.get(j)) == 0) {
                            found = true;
                            category = categoryName;
                                //System.out.println("found full" +translates.get(j) +" "+ categoryName + " "+cat + " dict");
                        } else {
                            double jcn = Wordnet.jcn(translates.get(j), cat);
                            //System.out.println(categoryName + " "+cat + " "+jcn);
                            if (jcn > max) {
                                max = jcn;
//                                    System.out.println("max full" + translates.get(j) + ": " + max + " " + cat);
                                category = categoryName;
                            }
                        }
                    }
                }
            }
        } else {
            String[] aspectList = aspects.split(" ");
           
            for (String aspect : aspectList) {
                if (aspect.endsWith("nya")) {
                    aspect = aspect.substring(0, aspect.length() - 3);
                }

                ArrayList<String> trans = Translator.getTranslation(aspect);

                for (String categoryName : aspectCategoryDicts.keySet()) {
                    ArrayList<String> value = aspectCategoryDicts.get(categoryName);

                    for (int i = 0; i < value.size() && !found; i++) {
                        String cat = value.get(i);
                        if (trans == null) {
                            if (cat.compareTo(aspect) == 0) {
                                found = true;
                                category = categoryName;
                                //System.out.println("found no trans " + categoryName + " "+cat + " dict");
                            } else {
                                double jcn = Wordnet.jcn(aspect, cat);
                                //System.out.println("found no trans " +categoryName + " "+cat + " "+jcn + " "+aspect);
                                if (jcn > max) {
                                    max = jcn;
//                                    System.out.println("max no trans " + aspect + ": " + max + " " + cat);
                                    category = categoryName;
                                }
                            }
                        } else { //translate found
                            for (int j = 0; j < trans.size() && !found; j++) {
                                if (cat.compareTo(trans.get(j)) == 0) {
                                    found = true;
                                    category = categoryName;
                                    //System.out.println("found " + categoryName + " "+cat + " dict");
                                } else {
                                    double jcn = Wordnet.jcn(trans.get(j), cat);
                                    //System.out.println("found " +trans.get(j)+" "+categoryName + " "+cat + " "+jcn);
                                    if (jcn > max) {
                                        max = jcn;
//                                        System.out.println("max " + trans.get(j) + ": " + max + " " + cat);
                                        category = categoryName;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (max == 0.0) {
            category = OTHER_CATEGORY;
        }
        System.out.println("");

        return category;
    }
    
    /**
     * return the maximum similarity between aspect and dictionary, return POSITIVE INFINITTY if dictionary contains aspect
     *
     * @param aspects aspect
     * @param dict dictionary
     * @return similarity
     */
    private static double findMax( ArrayList<String> aspect, ArrayList<String> dict) {
        boolean found = false;
        double max = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < dict.size() && !found; i++) {
            String seed = dict.get(i);

            for (int j = 0; j < aspect.size() && !found; j++) {
                if (seed.compareTo(aspect.get(j)) == 0) {
                    System.out.println(aspect.get(j) + " "+ dict.get(i) + " found");
                    found = true;
                    max = Double.POSITIVE_INFINITY;
                } else {
                    double jcn = Wordnet.jcn(aspect.get(j), seed);
                    System.out.println(aspect.get(j) + " "+ dict.get(i) + " "+jcn);
                    if (jcn > max) {
                        max = jcn;
                    }
                }
            }
        }
        return max;
    }
    
    /**
     * categorize aspect based on dictionary
     * *calculate the maximum similarity of each category aspect
     * @param aspects aspect
     * @return category of aspect
     * @throws FileNotFoundException dictionary not found
     */
    public static String getCategory2(String aspects) throws FileNotFoundException {
        System.out.println("---->"+ aspects);
        if (aspectCategoryDicts.isEmpty()) {
            initDict();
        }

        String aspectTemp = aspects;
        if (aspects.endsWith("nya")) {
            aspectTemp = aspects.substring(0, aspects.length() - 3);
        }

        ArrayList<String> newAspect = new ArrayList<>();
        ArrayList<String> translates = Translator.getTranslation(aspectTemp);
        if (translates != null) { //translate all words
            newAspect = translates;
            System.out.println("full trans");
        } else {
            //find translate for each token
            String[] aspectList = aspects.split(" ");
            ArrayList<String> trans = new ArrayList<>();
            
            for (String aspect : aspectList) {
                if (aspect.endsWith("nya")) {
                    aspect = aspect.substring(0, aspect.length() - 3);
                }
                ArrayList<String> transTemp = Translator.getTranslation(aspect);
                if (transTemp != null) {
                    trans.addAll(transTemp);
                }
//                System.out.println(transTemp.size());
//                System.out.println("  "+trans.size());
            }

            if (trans.isEmpty()) {
                System.out.println("trans null");
                for (int i = 0; i < aspectList.length; i++) {
                    if (aspectList[i].endsWith("nya")) {
                        newAspect.add(aspectList[i].substring(0, aspectList[i].length() - 3));
                    } else {
                        newAspect.add(aspectList[i]);
                    }
                }
                
            } else {
                System.out.println("trans ");
                newAspect = trans;
            }
        }
        
        String category = null;
        double max = Double.NEGATIVE_INFINITY;
        boolean found =false;

        for (String categoryName : aspectCategoryDicts.keySet()) {
            ArrayList<String> value = aspectCategoryDicts.get(categoryName);

            double jcn = findMax(newAspect, value);
            if (jcn == Double.POSITIVE_INFINITY) {
                if (!found) {
                    found = true;
                    max = Double.POSITIVE_INFINITY;
                    category = categoryName;
                }
                System.out.println(categoryName + ": found");
            } else {
                if (jcn > max) {
                    max = jcn;
                    category = categoryName;
                }
                System.out.println(categoryName + ": "+ jcn);
            }
        }
        
        if (max == 0.0) {
            category = OTHER_CATEGORY;
        }
        //postprocess
        if (found) {
            boolean inf = false;
            for (int i = 0; i < newAspect.size() && !inf; i++) {
                for (String categoryName : aspectCategoryDicts.keySet()) {
                    ArrayList<String> value = aspectCategoryDicts.get(categoryName);
                    ArrayList<String> temp = new ArrayList<String>();
                    temp.add(newAspect.get(i));
                    double jcn = findMax(temp, value);
                    
                    if (jcn == Double.POSITIVE_INFINITY) {
                        category = categoryName;
                        inf = true;
                    }
                }
            }
        }
        //System.out.println("");

        return category;
    }

    public static void main(String args[]) {
        try {
            ArrayList<AspectSentiment> aspectSentiments = new ArrayList<>();
            aspectSentiments.add(new AspectSentiment("pizzanya cheesenya", "enak", 1));
            aspectSentiments.add(new AspectSentiment("adu dombanya", "murah", 1));
            aspectSentiments.add(new AspectSentiment("dekorasi interior", "lezat", 1));
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
