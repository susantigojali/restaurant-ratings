/* Copyright (C) 2003 Univ. of Massachusetts Amherst, Computer Science Dept.
   This file is part of "MALLET" (MAchine Learning for LanguagE Toolkit).
   http://www.cs.umass.edu/~mccallum/mallet
   This software is provided under the terms of the Common Public License,
   version 1.0, as published by http://www.opensource.org.  For further
   information, see the file `LICENSE' included with this distribution. */
package Evaluator;


import cc.mallet.fst.TokenAccuracyEvaluator;
import cc.mallet.fst.Transducer;
import cc.mallet.fst.TransducerEvaluator;
import cc.mallet.fst.TransducerTrainer;
import java.util.logging.Logger;
import java.util.Arrays;
import java.io.PrintStream;
import java.text.DecimalFormat;

import cc.mallet.types.*;
import cc.mallet.util.MalletLogger;

/**
 * Determines the precision, recall and F1 on a per-class basis.
 * 
 * @author Charles Sutton
 * @version $Id: PerClassAccuracyEvaluator.java,v 1.1 2007/10/22 21:37:48 mccallum Exp $
 * Modified : Susanti Gojali
 */
public class MyPerClassAccuracyEvaluator extends TransducerEvaluator {

    private static Logger logger = MalletLogger.getLogger(TokenAccuracyEvaluator.class.getName());
    private String[] labels;
    private double[] prec;
    private double[] rec;
    private double[] fmeasure;
    private double macroAverage;

    public MyPerClassAccuracyEvaluator(InstanceList[] instanceLists, String[] descriptions) {
        super(instanceLists, descriptions);
    }

    public MyPerClassAccuracyEvaluator(InstanceList i1, String d1) {
        this(new InstanceList[]{i1}, new String[]{d1});
    }

    public MyPerClassAccuracyEvaluator(InstanceList i1, String d1, InstanceList i2, String d2) {
        this(new InstanceList[]{i1, i2}, new String[]{d1, d2});
    }

    public void evaluateInstanceList(TransducerTrainer tt, InstanceList data, String description) {
        Transducer model = tt.getTransducer();
        Alphabet dict = model.getInputPipe().getTargetAlphabet();
        int numLabels = dict.size();
        int[] numCorrectTokens = new int[numLabels];
        int[] numPredTokens = new int[numLabels];
        int[] numTrueTokens = new int[numLabels];

        logger.info("Per-token results for " + description);
        for (int i = 0; i < data.size(); i++) {
            Instance instance = data.get(i);
            Sequence input = (Sequence) instance.getData();
            Sequence trueOutput = (Sequence) instance.getTarget();
            assert (input.size() == trueOutput.size());
            Sequence predOutput = model.transduce(input);
            assert (predOutput.size() == trueOutput.size());
            for (int j = 0; j < trueOutput.size(); j++) {
                int idx = dict.lookupIndex(trueOutput.get(j));
                numTrueTokens[idx]++;
                numPredTokens[dict.lookupIndex(predOutput.get(j))]++;
                if (trueOutput.get(j).equals(predOutput.get(j))) {
                    numCorrectTokens[idx]++;
                }
            }
        }

        //Add by Susanti
        labels = new String[numLabels];
        for (int i = 0; i < labels.length; i++) {
            labels[i] = (String) dict.lookupObject(i);
        }
        prec = new double[numLabels];
        rec = new double[numLabels];
        fmeasure = new double[numLabels];

      
        DecimalFormat f = new DecimalFormat("0.####");
        double[] allf = new double[numLabels];
        for (int i = 0; i < numLabels; i++) {
            Object label = dict.lookupObject(i);
            double precision = ((double) numCorrectTokens[i]) / numPredTokens[i];
            double recall = ((double) numCorrectTokens[i]) / numTrueTokens[i];
            double f1 = (2 * precision * recall) / (precision + recall);
            if (!Double.isNaN(f1)) {
                allf[i] = f1;
            }
            logger.info(description + " label " + label + " P " + f.format(precision)
                    + " R " + f.format(recall) + " F1 " + f.format(f1));

            // Add by Susanti
            prec[i] = precision;
            rec[i] = recall;
            if (Double.isNaN(f1)) {
                fmeasure[i] = 0.0;
            } else {
                fmeasure[i] = f1;
            }
        }

        logger.info("Macro-average F1 " + f.format(MatrixOps.mean(allf)));

        //Add by Susanti
        this.macroAverage = MatrixOps.mean(allf);
    }

    public double[] getPrec() {
        return prec;
    }

    public double[] getRec() {
        return rec;
    }

    public double[] getFmeasure() {
        return fmeasure;
    }

    public double getMacroAverage() {
        return macroAverage;
    }

    public String[] getLabels() {
        return labels;
    }

}
