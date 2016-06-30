package Evaluator;

import DataProcessing.Postprocess;
import Model.AspectSentiment;
import Model.Reader;
import Model.SequenceTagging;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

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
//        ArrayList<AspectSentiment> prediction = new ArrayList<>();
//        prediction.add(new AspectSentiment("makanan", "enak", 1));
//        prediction.add(new AspectSentiment("makanan", "renyah", 1));
//        prediction.add(new AspectSentiment("makanan", "gosong", -1));
//        prediction.add(new AspectSentiment("palayan", "ramah", 1));
//        prediction.add(new AspectSentiment("pemandangan", "bagus", 1));
//        prediction.add(new AspectSentiment("harga", "mahal", -1));
//        prediction.add(new AspectSentiment("harga", "tidak murah", -1));
//        
//        
//        ArrayList<AspectSentiment> actual= new ArrayList<>();
//        actual.add(new AspectSentiment("makanan", "enak banget", 0));
//        actual.add(new AspectSentiment("makanan", "renyah", 0));
//        actual.add(new AspectSentiment("makanan", "gosong sekali", 0));
//        actual.add(new AspectSentiment("palayan", "ramah", 0));
//        actual.add(new AspectSentiment("pemandangan", "bagus", 0));
//        actual.add(new AspectSentiment("harga", "tidak mahal", 0));
//        
//        ExtractionEvaluator.evaluate(prediction, actual);
//        System.out.println(ExtractionEvaluator.getPrecision() + " "+ ExtractionEvaluator.getRecall());
        
        double f = 0.0;
        double prec = 0.0;
        double rec = 0.0;
        
        String actualDataExtraction = "datatest/CRF/CRFExtraction.txt";
        String actualTokenClassfication = "datatest/CRF/CRFTokenClassification.txt";
        
        try {
            ArrayList<ArrayList<AspectSentiment>> actualAspectSentiments = Reader.readActualAspectSentiment(actualDataExtraction);
            ArrayList<ArrayList<SequenceTagging>> actualSequenceTagging = Reader.readSequenceTagging(actualTokenClassfication);

            int INDEX = 0;
            for (ArrayList<SequenceTagging> review : actualSequenceTagging) {
                ArrayList<AspectSentiment> predictAS = new ArrayList<>();
                System.out.print(INDEX + " ");
                for (SequenceTagging st : review) {
                    predictAS.addAll(Postprocess.findAspectSentiment(st)); 
                }
//                System.out.println(predictAS.size());
//                for (int i = 0; i < predictAS.size(); i++) {
//                    predictAS.get(i).print();
//                }
//                System.out.println(actualAspectSentiments.get(INDEX).size());
//                for (int i = 0; i < actualAspectSentiments.get(INDEX).size(); i++) {
//                    actualAspectSentiments.get(INDEX).get(i).print();
//                }
                
                ExtractionEvaluator.evaluate(predictAS, actualAspectSentiments.get(INDEX));
                
                ExtractionEvaluator.printEvaluation();
                f += ExtractionEvaluator.getF1();
                prec += ExtractionEvaluator.getPrecision();
                rec += ExtractionEvaluator.getRecall();
                INDEX++;

            }
            
            System.out.println("Extraction::");
            System.out.println("prec:  " + prec / actualAspectSentiments.size());
            System.out.println("rec: " + rec / actualAspectSentiments.size());
            System.out.println("f1: " + f / actualAspectSentiments.size());
        
        
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ExtractionEvaluator.class.getName()).log(Level.SEVERE, null, ex);
        } 
    } 
}
