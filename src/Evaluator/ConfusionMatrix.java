package Evaluator;

import java.text.DecimalFormat;

/**
 *
 * @author Susanti Gojali
 */
public class ConfusionMatrix {

    private String[] labels;
    private int[][] cm; //matrix

    /**
     *
     * @param n number of labels
     */
    public ConfusionMatrix(int n) {
        this.labels = new String[n] ;
        this.cm = new int[n][n];
        
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                cm[i][j] = 0;
            }
        }
    }

    /**
     *
     * @param labels labels for confusion matrix
     * @param cm confusion matrix
     */
    public ConfusionMatrix(String[] labels, int[][] cm) {
        this.labels = labels;
        this.cm = cm;
    }
    
    /**
     *
     * @param labels labels
     */
    public void setLabel(String[] labels) {
        this.labels = labels;
    }
    
    /**
     *
     * @return labels
     */
    public String[] getLabel() {
        return this.labels;
    }

    /**
     *
     * @param actualLabel index of actual label
     * @param predictedLabel index of predicted label
     * @param value value
     */
    public void setCM(int actualLabel, int predictedLabel, int value) {
        this.cm[actualLabel][predictedLabel] = value;
    }

    /**
     *
     * @param actualLabel index of actual label
     * @param predictedLabel index of predicted label
     * @return value
     */
    public int getCM(int actualLabel, int predictedLabel) {
        return this.cm[actualLabel][predictedLabel];
    }

    /**
     * Print the confusion matrix
     */
    public void printConfusionMatrix() {
        System.out.println("CONFUSION MATRIX=========");
        for (int i = 0; i < cm.length; i++) {
            if(labels[i]=="O") {
                System.out.print(labels[i] + "\t\t");
            } else {
                System.out.print(labels[i] + "\t");
            }
            for (int j = 0; j < cm.length; j++) {
                System.out.print(cm[i][j] + "\t");
            }
            System.out.println();
        }
    }

    /**
     *
     * @return accuracy for all label
     */
    public double getAccuracy() {
        int trueLabel = 0;
        for (int i = 0; i < cm.length; i++) {
            trueLabel += cm[i][i];
        }
        return (double) trueLabel / sumMatrix();
    }
    
    /**
     * Print Precision, Recall, and F1 for all label
     */
    public void printEvaluation() {
        System.out.println("CONFUSION MATRIX EVALUATION============");
        DecimalFormat f = new DecimalFormat("0.####");
        System.out.println("Accuracy: " + f.format(getAccuracy()));
        
        
        for (int i = 0; i < cm.length; i++) {
            double precision, recall, f1;

            if (sumColumn(i) == 0) {
                precision = Double.NaN;
            } else {
                precision = (double) cm[i][i] / sumColumn(i);
            }

            if (sumRow(i) == 0) {
                recall = Double.NaN;
            } else {
                recall = (double) cm[i][i] / sumRow(i);
            }

            if (precision + recall == Double.NaN) {
                f1 = Double.NaN;
            } else {
                f1 = (2 * precision * recall) / (precision + recall);
            }

            System.out.println("label: " + labels[i]);
            System.out.println("prec: " + f.format(precision) + " rec: " + f.format(recall) + " f1: " + f.format(f1));
        }

    }

    /**
     * Combine the confusion matrix
     * @param cm array of confusion matrix
     * @return confusion matrix
     */
    public static ConfusionMatrix getCrossValidationCM(ConfusionMatrix[] cm) {
        assert (cm.length > 1);
        int numLabel = cm[0].getLabel().length;
        
        ConfusionMatrix cmCV = new ConfusionMatrix(numLabel);
        cmCV.setLabel(cm[0].getLabel());
        
        for (int i = 0; i < numLabel; i++) {
            for (int j = 0; j < numLabel; j++) {
                int sum = 0;
                for (int k = 0; k < cm.length; k++) {
                    sum += cm[k].getCM(i, j);
                }
                cmCV.setCM(i, j, sum);
            }
        }
        return cmCV; 
        
    }
    
    private int sumMatrix() {
        int sum = 0;
        for (int i = 0; i < cm.length; i++) {
            for (int j = 0; j < cm.length; j++) {
                sum += cm[i][j];
            }
        }
        return sum;
    }

    private int sumColumn(int idx) {
        int sum = 0;
        for (int i = 0; i < cm.length; i++) {
            sum += cm[i][idx];
        }
        return sum;
    }

    private int sumRow(int idx) {
        int sum = 0;
        for (int i = 0; i < cm.length; i++) {
            sum += cm[idx][i];
        }
        return sum;
    }

}
