package Evaluator;

import cc.mallet.fst.TokenAccuracyEvaluator;
import cc.mallet.fst.Transducer;
import cc.mallet.fst.TransducerEvaluator;
import cc.mallet.fst.TransducerTrainer;
import cc.mallet.types.Alphabet;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.Sequence;
import cc.mallet.util.MalletLogger;
import java.util.logging.Logger;

/**
 *
 * @author Susanti Gojali
 */
public class ConfusionMatrixEvaluator extends TransducerEvaluator {

    private static Logger logger = MalletLogger.getLogger(TokenAccuracyEvaluator.class.getName());

    private ConfusionMatrix cm;

    public ConfusionMatrixEvaluator(InstanceList[] instanceLists, String[] descriptions) {
        super(instanceLists, descriptions);
    }

    public ConfusionMatrixEvaluator(InstanceList i1, String d1) {
        this(new InstanceList[]{i1}, new String[]{d1});
    }

    @Override
    public void evaluateInstanceList(TransducerTrainer tt, InstanceList data, String description) {

        Transducer model = tt.getTransducer();
        Alphabet dict = model.getInputPipe().getTargetAlphabet();
        int numLabels = dict.size();

        String[] labels = new String[numLabels];
        for (int i = 0; i < labels.length; i++) {
            labels[i] = (String) dict.lookupObject(i);
        }

        cm = new ConfusionMatrix(numLabels);
        cm.setLabel(labels);
        
        
        logger.info("Confusion Matrix " + description);
        for (int i = 0; i < data.size(); i++) {
            Instance instance = data.get(i);
            Sequence input = (Sequence) instance.getData();
            Sequence trueOutput = (Sequence) instance.getTarget();
            assert (input.size() == trueOutput.size());
            Sequence predOutput = model.transduce(input);
            assert (predOutput.size() == trueOutput.size());

            for (int j = 0; j < trueOutput.size(); j++) {
                int idxTrue = dict.lookupIndex(trueOutput.get(j));
                int idxPred = dict.lookupIndex(predOutput.get(j));
                int value =  cm.getCM(idxTrue, idxPred) + 1;
                cm.setCM(idxTrue, idxPred, value);
            }
        }
//        cm.printConfusionMatrix();
//        cm.printEvaluation();
    }

    public ConfusionMatrix getCM() {
        return cm;
    }

}
