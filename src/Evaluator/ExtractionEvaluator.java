package Evaluator;

import Model.AspectSentiment;
import java.util.ArrayList;

/**
 *
 * @author susanti_2
 */
public class ExtractionEvaluator {
    private static double precision;
    private static double recall;
    
   
    public static void evaluate(ArrayList<AspectSentiment> prediction, ArrayList<AspectSentiment> actual) {
        if (prediction.isEmpty()) {
            precision = 0;
            recall = 0;
        } else {
            int correct = 0 ;
            for(AspectSentiment as :actual) {
                boolean found = false;
                for (AspectSentiment pred : prediction) {
                    if (same(pred, as) && !found) {
                        found = true;
                        correct++;
                    }
                }
            }
            precision = (double) correct / prediction.size();
            recall = (double) correct / actual.size();
        }
    }
    
    private static boolean same(AspectSentiment pred, AspectSentiment act) {
        if (pred.getAspect().compareToIgnoreCase(act.getAspect()) == 0 && 
                pred.getSentiment().compareToIgnoreCase(act.getSentiment()) == 0) {
            return true;
        } else {
            return false;
        }
    }

    public static double getPrecision() {
        return precision;
    }

    public static double getRecall() {
        return recall;
    }
    
    public static double getF1() {
        if (recall == 0 ) {
            return 0;
        } else {
            return (2 * precision * recall) / (precision + recall);
        }
    }
    
    public static void printEvaluation() {
        System.out.println("Evaluation:: prec:"+ precision + " rec:" + recall + " f1: " + getF1());
    }
    
    public static void main(String args[]) {
        ArrayList<AspectSentiment> prediction = new ArrayList<>();
        prediction.add(new AspectSentiment("makanan", "enak", 1));
        prediction.add(new AspectSentiment("makanan", "renyah", 1));
        prediction.add(new AspectSentiment("makanan", "gosong", -1));
        prediction.add(new AspectSentiment("palayan", "ramah", 1));
        prediction.add(new AspectSentiment("pemandangan", "bagus", 1));
        prediction.add(new AspectSentiment("harga", "mahal", -1));
        prediction.add(new AspectSentiment("harga", "tidak murah", -1));
        
        
        ArrayList<AspectSentiment> actual= new ArrayList<>();
        actual.add(new AspectSentiment("makanan", "enak banget", 0));
        actual.add(new AspectSentiment("makanan", "renyah", 0));
        actual.add(new AspectSentiment("makanan", "gosong sekali", 0));
        actual.add(new AspectSentiment("palayan", "ramah", 0));
        actual.add(new AspectSentiment("pemandangan", "bagus", 0));
        actual.add(new AspectSentiment("harga", "tidak mahal", 0));
        
        ExtractionEvaluator.evaluate(prediction, actual);
        System.out.println(ExtractionEvaluator.getPrecision() + " "+ ExtractionEvaluator.getRecall());
    }
    
    
}
