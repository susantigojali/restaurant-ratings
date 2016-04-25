package Evaluator;

import java.text.DecimalFormat;

/**
 *
 * @author susanti_2
 */
public class Evaluation {

    private String[] labels;
    private double accuracy;
    private double[] precisson;
    private double[] recall;
    private double[] f1;
    private double macroAverage;

    /**
     *
     * @param labels
     * @param accuracy
     * @param precission
     * @param recall
     * @param f1
     * @param macroAverage
     */
    public Evaluation(String[] labels, double accuracy, double[] precission, double[] recall, double[] f1, double macroAverage) {
        this.labels = labels;
        this.accuracy = accuracy;
        this.precisson = precission;
        this.recall = recall;
        this.f1 = f1;
        this.macroAverage = macroAverage;
    }

    /**
     *
     * @return accuracy
     */
    public double getAccuracy() {
        return accuracy;
    }

    /**
     *
     * @return precision
     */
    public double[] getPrecisson() {
        return precisson;
    }

    /**
     *
     * @return recall
     */
    public double[] getRecall() {
        return recall;
    }

    /**
     *
     * @return f1
     */
    public double[] getF1() {
        return f1;
    }

    /**
     *
     * @return label
     */
    public String[] getLabels() {
        return labels;
    }

    /**
     *
     * @return macro average
     */
    public double getMacroAverage() {
        return macroAverage;
    }

    /**
     *
     * @param idx index label
     * @return name of label
     */
    public String getLabel(int idx) {
        return labels[idx];
    }

    /**
     * print the accuracy, precision, recall, f1, and macro average
     */
    public void printEvaluation() {
        System.out.println("CLASS EVALUATION");
        DecimalFormat f = new DecimalFormat("0.####");
        
        System.out.println("Accuracy: "+ f.format(accuracy));
        for (int i = 0; i < labels.length; i++) {
            System.out.println("labels: "+ labels[i]);
            if (Double.isNaN(precisson[i]) || Double.isNaN(recall[i]) || Double.isNaN(f1[i])) {
                System.out.println("NAN");
            }
            System.out.println("Prec: " + f.format(precisson[i]) + " Rec: "+ f.format(recall[i]) + " F1: " + f.format(f1[i]));
        }
        System.out.println("Macro Average: " + f.format(macroAverage));
    }

}
