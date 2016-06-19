package Evaluator;

import Model.AspectAggregation;
import Model.AspectSentiment;
import Model.Reader;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author susanti_2
 */
public class AspectAggregationEvaluator {

    private static double precision;
    private static double recall;

    /**
     * Evaluate prediction from actual aspect aggregation
     * @param prediction prediction aspect aggregation
     * @param actual actual aspect aggregation
     */
    public static void evaluate(LinkedHashMap<String, ArrayList<AspectSentiment>> prediction, LinkedHashMap<String, ArrayList<AspectSentiment>> actual) {
        if (prediction.isEmpty()) {
            precision = 0;
            recall = 0;
        } else {
            int correct = 0;
            int pred = 0;
            int act = 0;

            for (Entry<String, ArrayList<AspectSentiment>> entry : actual.entrySet()) {
                act += entry.getValue().size();
                String category = entry.getKey();

                if (prediction.containsKey(category)) {
                    ArrayList<AspectSentiment> actualsAS = entry.getValue();
                    ArrayList<AspectSentiment> predsAS = prediction.get(category);

                    for (AspectSentiment actAS : actualsAS) {
                        if (contain(actAS, predsAS)) {
                            correct++;
                        } 
                    }
                } 
            }
            for (Entry<String, ArrayList<AspectSentiment>> entry : prediction.entrySet()) {
                pred += entry.getValue().size();
            }
            precision = (double) correct / pred;
            recall = (double) correct / act;
        }
    }

    private static boolean contain(AspectSentiment pred, ArrayList<AspectSentiment> act) {
        boolean found = false;
        for (int i = 0; i < act.size() && !found; i++) {
            if (pred.getAspect().compareToIgnoreCase(act.get(i).getAspect()) == 0
                    && pred.getSentiment().compareToIgnoreCase(act.get(i).getSentiment()) == 0) {
                found = true;
            }
        }
        return found;
    }

    /**
     * Print evaluation for aspect aggregation : precision, recall, f1
     */
    public static void printEvaluation() {
        DecimalFormat f = new DecimalFormat("0.####");
        System.out.println("Evaluation Aggregate:: prec:" + f.format(precision) + " rec:" + f.format(recall) + " f1: " + f.format(getF1()));
    }

    /**
     *
     * @return precision
     */
    public static double getPrecision() {
        return precision;
    }

    /**
     *
     * @return recall
     */
    public static double getRecall() {
        return recall;
    }

    /**
     *
     * @return F1
     */
    public static double getF1() {
        if (recall == 0) {
            return 0;
        } else {
            return (2 * precision * recall) / (precision + recall);
        }
    }

    /**
     *
     * @param args
     */
    public static void main(String args[]) {
        double fAgg = 0.0;
        double precAgg = 0.0;
        double recAgg = 0.0;
        String dataAspectAggregation = "datatest/CRF/AspectAggregation.txt";
        String dataExtractionAS = "datatest/CRF/CRFExtraction.txt";
        try {
            ArrayList<LinkedHashMap<String, ArrayList<AspectSentiment>>> actualAspectAggregations = Reader.readAspectAggregation(dataAspectAggregation);
            ArrayList<ArrayList<AspectSentiment>> actualAspectSentiments = Reader.readActualAspectSentiment(dataExtractionAS);

            int INDEX = 0;
            for (ArrayList<AspectSentiment> as : actualAspectSentiments) {
                LinkedHashMap<String, ArrayList<AspectSentiment>> aggregation = AspectAggregation.aggregation(as);
                AspectAggregationEvaluator.evaluate(aggregation, actualAspectAggregations.get(INDEX));
                System.out.println(INDEX);
                if (AspectAggregationEvaluator.getF1() < 0.7) {
                    System.out.println("+++++++");

                    for (Map.Entry<String, ArrayList<AspectSentiment>> entry : actualAspectAggregations.get(INDEX).entrySet()) {
                        System.out.println(entry.getKey());
                        ArrayList<AspectSentiment> value = entry.getValue();
                        for (int j = 0; j < value.size(); j++) {
                            value.get(j).print();
                        }
                    }

                    System.out.println("++++++++++");

                    for (Map.Entry<String, ArrayList<AspectSentiment>> entry : aggregation.entrySet()) {
                        System.out.println(entry.getKey());
                        ArrayList<AspectSentiment> value = entry.getValue();
                        for (int j = 0; j < value.size(); j++) {
                            value.get(j).print();
                        }
                    }

                }
                AspectAggregationEvaluator.printEvaluation();
                fAgg += AspectAggregationEvaluator.getF1();
                precAgg += AspectAggregationEvaluator.getPrecision();
                recAgg += AspectAggregationEvaluator.getRecall();
                INDEX++;
            }

            System.out.println("Aggregation::");
            System.out.println("prec:  " + precAgg / actualAspectSentiments.size());
            System.out.println("rec: " + recAgg / actualAspectSentiments.size());
            System.out.println("f1: " + fAgg / actualAspectSentiments.size());

        } catch (FileNotFoundException ex) {
            Logger.getLogger(AspectAggregationEvaluator.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
