package Learning;

import Evaluator.MyPerClassAccuracyEvaluator;
import Evaluator.Evaluation;
import Evaluator.ConfusionMatrixEvaluator;
import Evaluator.ConfusionMatrix;
import Model.SequenceTagging;
import cc.mallet.fst.CRF;
import cc.mallet.fst.CRFTrainerByLabelLikelihood;
import cc.mallet.fst.CRFTrainerByThreadedLabelLikelihood;
import cc.mallet.fst.MultiSegmentationEvaluator;
import cc.mallet.fst.NoopTransducerTrainer;
import cc.mallet.fst.SimpleTagger;
import static cc.mallet.fst.SimpleTagger.apply;
import static cc.mallet.fst.SimpleTagger.test;
import cc.mallet.fst.TokenAccuracyEvaluator;
import cc.mallet.fst.Transducer;
import cc.mallet.fst.TransducerEvaluator;
import cc.mallet.fst.ViterbiWriter;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.iterator.LineGroupIterator;
import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureVector;
import cc.mallet.types.InstanceList;
import cc.mallet.types.Sequence;
import cc.mallet.util.CommandOption;
import cc.mallet.util.MalletLogger;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.logging.Logger;

/**
 *
 * @author susanti_2
 */
public class MyCRFSimpleTagger {

    private static Logger logger
            = MalletLogger.getLogger(SimpleTagger.class.getName());

    private static final CommandOption.Double gaussianVarianceOption = new CommandOption.Double(SimpleTagger.class, "gaussian-variance", "DECIMAL", true, 10.0,
            "The gaussian prior variance used for training.", null);

    private static final CommandOption.Boolean trainOption = new CommandOption.Boolean(SimpleTagger.class, "train", "true|false", true, false,
            "Whether to train", null);

    private static final CommandOption.String testOption = new CommandOption.String(SimpleTagger.class, "test", "lab or seg=start-1.continue-1,...,start-n.continue-n",
            true, null,
            "Test measuring labeling or segmentation (start-i, continue-i) accuracy", null);

    private static final CommandOption.File modelOption = new CommandOption.File(SimpleTagger.class, "model-file", "FILENAME", true, null,
            "The filename for reading (train/run) or saving (train) the model.", null);

    private static final CommandOption.Double trainingFractionOption = new CommandOption.Double(SimpleTagger.class, "training-proportion", "DECIMAL", true, 0.5,
            "Fraction of data to use for training in a random split.", null);

    private static final CommandOption.Integer randomSeedOption = new CommandOption.Integer(SimpleTagger.class, "random-seed", "INTEGER", true, 0,
            "The random seed for randomly selecting a proportion of the instance list for training", null);

    private static final CommandOption.IntegerArray ordersOption = new CommandOption.IntegerArray(SimpleTagger.class, "orders", "COMMA-SEP-DECIMALS", true, new int[]{1},
            "List of label Markov orders (main and backoff) ", null);

    private static final CommandOption.String forbiddenOption = new CommandOption.String(SimpleTagger.class, "forbidden", "REGEXP", true,
            "\\s", "label1,label2 transition forbidden if it matches this", null);

    private static final CommandOption.String allowedOption = new CommandOption.String(SimpleTagger.class, "allowed", "REGEXP", true,
            ".*", "label1,label2 transition allowed only if it matches this", null);

    private static final CommandOption.String defaultOption = new CommandOption.String(SimpleTagger.class, "default-label", "STRING", true, "O",
            "Label for initial context and uninteresting tokens", null);

    private static final CommandOption.Integer iterationsOption = new CommandOption.Integer(SimpleTagger.class, "iterations", "INTEGER", true, 500,
            "Number of training iterations", null);

    private static final CommandOption.Boolean viterbiOutputOption = new CommandOption.Boolean(SimpleTagger.class, "viterbi-output", "true|false", true, false,
            "Print Viterbi periodically during training", null);

    private static final CommandOption.Boolean connectedOption = new CommandOption.Boolean(SimpleTagger.class, "fully-connected", "true|false", true, true,
            "Include all allowed transitions, even those not in training data", null);

    private static final CommandOption.String weightsOption = new CommandOption.String(SimpleTagger.class, "weights", "sparse|some-dense|dense", true, "some-dense",
            "Use sparse, some dense (using a heuristic), or dense features on transitions.", null);

    private static final CommandOption.Boolean continueTrainingOption = new CommandOption.Boolean(SimpleTagger.class, "continue-training", "true|false", false, false,
            "Continue training from model specified by --model-file", null);

    private static final CommandOption.Integer nBestOption = new CommandOption.Integer(SimpleTagger.class, "n-best", "INTEGER", true, 1,
            "How many answers to output", null);

    private static final CommandOption.Integer cacheSizeOption = new CommandOption.Integer(SimpleTagger.class, "cache-size", "INTEGER", true, 100000,
            "How much state information to memoize in n-best decoding", null);

    private static final CommandOption.Boolean includeInputOption = new CommandOption.Boolean(SimpleTagger.class, "include-input", "true|false", true, false,
            "Whether to include the input features when printing decoding output", null);

    private static final CommandOption.Boolean featureInductionOption = new CommandOption.Boolean(SimpleTagger.class, "feature-induction", "true|false", true, false,
            "Whether to perform feature induction during training", null);

    private static final CommandOption.Integer numThreads = new CommandOption.Integer(SimpleTagger.class, "threads", "INTEGER", true, 1,
            "Number of threads to use for CRF training.", null);

    private static final CommandOption.List commandOptions
            = new CommandOption.List(
                    "Training, testing and running a generic tagger.",
                    new CommandOption[]{
                        gaussianVarianceOption,
                        trainOption,
                        iterationsOption,
                        testOption,
                        trainingFractionOption,
                        modelOption,
                        randomSeedOption,
                        ordersOption,
                        forbiddenOption,
                        allowedOption,
                        defaultOption,
                        viterbiOutputOption,
                        connectedOption,
                        weightsOption,
                        continueTrainingOption,
                        nBestOption,
                        cacheSizeOption,
                        includeInputOption,
                        featureInductionOption,
                        numThreads
                    });

    
    public static void main(String[] args) throws Exception {
       // Training CRF
        String trainFilename = "dataset/CRF/CRFDatasetFULL2 (992).txt";
        String modelFilename = "dataset/CRF/crfFULL2 (992).model";
        int folds = 10;
        myTrain(trainFilename, modelFilename, folds);

        //Classify CRF
//        String testFilename = "dataset/CRF/CRFDatatest.txt";
//        String modelFilename = "crf.model";
//        Boolean includeInput = true;
//        int nBestOption = 1;
//
//        //classify(args);
//        ArrayList<SequenceTagging> outputs = myClassify(testFilename, modelFilename, includeInput, nBestOption);
        
        
    }

    /**
     * train CRF with feature word and pos tag
     * @param trainFilename file name 
     * @param modelFilename file model
     * @param nfolds number of folds for evaluation
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void myTrain(String trainFilename, String modelFilename, int nfolds) throws FileNotFoundException, IOException {
        Reader trainingFile = null;
        InstanceList data;

        trainingFile = new FileReader(new File(trainFilename));
        Pipe p = null;
        CRF crf = null;
        TransducerEvaluator eval = null;
  
        p = new SimpleTagger.SimpleTaggerSentence2FeatureVectorSequence();
        p.getTargetAlphabet().lookupIndex(defaultOption.value);

        p.setTargetProcessing(true);   
        data = new InstanceList(p);
        data.addThruPipe(new LineGroupIterator(trainingFile,
                Pattern.compile("^\\s*$"), true));
        System.out.println("Data: " + data.size());

        System.out.println("Number of features in training data: " + p.getDataAlphabet().size());
        System.out.println("Number of predicates: " + p.getDataAlphabet().size());

        Alphabet targets = p.getTargetAlphabet();
        StringBuffer buf = new StringBuffer("Labels:");
        for (int i = 0; i < targets.size(); i++) {
            buf.append(" ").append(targets.lookupObject(i).toString());
        }
        System.out.println(buf.toString());

        InstanceList.CrossValidationIterator cvIter = data.crossValidationIterator(nfolds);
        int folds = 0;
        ConfusionMatrix[] cm = new ConfusionMatrix[nfolds];

        while (cvIter.hasNext()) {
            System.out.println("============ fold : " + folds);
            InstanceList[] trainTestSplits = cvIter.nextSplit();
            InstanceList trainSplit = trainTestSplits[0];
            InstanceList testSplit = trainTestSplits[1];

            System.out.println("train " + trainSplit.size());
            System.out.println("test " + testSplit.size());

            System.out.println("CRF Training..");
//            System.out.println("Orders: " + ordersOption.valueToString());
//            System.out.println("Default Option: " + defaultOption.value);
//            System.out.println("Forbidden Option: " + forbiddenOption.value);
//            System.out.println("allowed Option: " + allowedOption.value);
//            System.out.println("Forbidden Option: " + forbiddenOption.value);
//            System.out.println("connected Option: " + connectedOption.value);
//            System.out.println("iterations Option: " + iterationsOption.value);
//            System.out.println("gaussianVariance Option: " + gaussianVarianceOption.value);

            crf = train(trainSplit, testSplit, eval,
                    ordersOption.value, defaultOption.value,
                    forbiddenOption.value, allowedOption.value,
                    connectedOption.value, iterationsOption.value,
                    gaussianVarianceOption.value, crf);

            System.out.println("\n\nPrint model ke file..");
            ObjectOutputStream s
                    = new ObjectOutputStream(new FileOutputStream(modelFilename));
            s.writeObject(crf);
            s.close();

            if (trainingFile != null) {
                trainingFile.close();
            }

            cm[folds] = getConfusionMatrixEvaluation(crf, testSplit);
            folds++;
        }

        //Confusion Matrix for All Cross Validation
        System.out.println("=======CROSS VALIDATION EVALUATION=======");
        ConfusionMatrix cmAll = ConfusionMatrix.getCrossValidationCM(cm);
        cmAll.printConfusionMatrix();
        cmAll.printEvaluation();

    }

    /**
     * return the sequence output
     * @param testFilename filename to classify
     * @param modelFilename model file
     * @param includeInput if true, input will be printed
     * @param nBestOption number of output
     * @param inputSequences sequence of input <word, postag> and output
     * @return list of sequence output 
     * @throws FileNotFoundException test file, model file
     * @throws IOException model file
     * @throws ClassNotFoundException CRF
     */
    public static ArrayList<SequenceTagging> myClassify(String testFilename, String modelFilename, Boolean includeInput, int nBestOption, ArrayList<SequenceTagging> inputSequences) throws FileNotFoundException, IOException, ClassNotFoundException {
        Reader testFile = null;
        InstanceList testData = null;

        //Load test file
        testFile = new FileReader(new File(testFilename));

        Pipe p = null;
        CRF crf = null;

        //Load Model
        ObjectInputStream s = new ObjectInputStream(new FileInputStream(modelFilename));
        crf = (CRF) s.readObject();
        s.close();
        p = crf.getInputPipe();

        p.setTargetProcessing(false);
        testData = new InstanceList(p);
        testData.addThruPipe(
                new LineGroupIterator(testFile,
                        Pattern.compile("^\\s*$"), true));

//        logger.info("Number of predicates: " + p.getDataAlphabet().size());

        ArrayList<SequenceTagging> outputClassify = new ArrayList<>();  
        assert (inputSequences.size() == testData.size()); //memastikan reader sequence input jalan dengan benar
        
        for (int i = 0; i < testData.size(); i++) {
            Sequence input = (Sequence) testData.get(i).getData();
            Sequence[] outputs = apply(crf, input, nBestOption);

            int k = outputs.length;
            boolean error = false;
            for (int a = 0; a < k; a++) {
                if (outputs[a].size() != input.size()) {
                    logger.info("Failed to decode input sequence " + i + ", answer " + a);
                    error = true;
                }
            }
            if (!error) {
                ArrayList<String> seqOutputs = new ArrayList<>();
                for (int j = 0; j < outputs[0].size(); j++) {
                    seqOutputs.add(outputs[0].get(j).toString());
                }
                outputClassify.add(new SequenceTagging(inputSequences.get(i).getSequenceInput(), seqOutputs));
                
                for (int j = 0; j < input.size(); j++) {
                    StringBuffer buf = new StringBuffer();
                    if (includeInput) {
                        buf.append(inputSequences.get(i).getSequenceInput().get(j).getWord()).append(" ");
                        buf.append(inputSequences.get(i).getSequenceInput().get(j).getPostag()).append(" ");
                    }
                    
                    for (int a = 0; a < k; a++) {
                        buf.append(outputs[a].get(j).toString());
                    }
//                    System.out.println(buf.toString());
                }
//                System.out.println();
            } else { // jika hasil output error, kasih sequence kosong
                outputClassify.add(new SequenceTagging(inputSequences.get(i).getSequenceInput(), null));
            }
        }

        if (testFile != null) {
            testFile.close();
        }
        
        return outputClassify;
    }
    
    /**
     * evaluate instance list 
     * @param crf model CRF
     * @param testing instance for testing
     * @return evaluation
     */
    public static Evaluation evaluation(CRF crf, InstanceList testing) {
        CRFTrainerByLabelLikelihood crft = new CRFTrainerByLabelLikelihood(crf);
        String description = "Testing"; //harus ini isinya

        logger.info("Testing Token Accuracy Evaluator========");
        TokenAccuracyEvaluator tokenAccuracyEval = new TokenAccuracyEvaluator(testing, description);
        test(crft, tokenAccuracyEval, testing);
        double accuracy = tokenAccuracyEval.getAccuracy(description);
        System.out.println("accuracy = " + accuracy);

        logger.info("My Testing Per Class Accuracy Evaluator========");
        MyPerClassAccuracyEvaluator ev = new MyPerClassAccuracyEvaluator(testing, description);
        test(crft, ev, testing);

        Evaluation eval = new Evaluation(ev.getLabels(), accuracy, ev.getPrec(), ev.getRec(), ev.getFmeasure(), ev.getMacroAverage());

        return eval;
    }

    /**
     * evaluate testing instance using model
     * @param crf model
     * @param testing instance for testing
     * @return confusion matrix 
     */
    public static ConfusionMatrix getConfusionMatrixEvaluation(CRF crf, InstanceList testing) {
        CRFTrainerByLabelLikelihood crft = new CRFTrainerByLabelLikelihood(crf);
        String description = "Testing"; //harus ini isinya

        logger.info("My confusion matrix Evaluator========");
        ConfusionMatrixEvaluator cmEval = new ConfusionMatrixEvaluator(testing, description);
        test(crft, cmEval, testing);
        return cmEval.getCM();

    }

    /**
     * Create and train a CRF model from the given training data, optionally
     * testing it on the given test data.
     *
     * @param training training data
     * @param testing test data (possibly <code>null</code>)
     * @param eval accuracy evaluator (possibly <code>null</code>)
     * @param orders label Markov orders (main and backoff)
     * @param defaultLabel default label
     * @param forbidden regular expression specifying impossible label
     * transitions <em>current</em><code>,</code><em>next</em>
     * (<code>null</code> indicates no forbidden transitions)
     * @param allowed regular expression specifying allowed label transitions
     * (<code>null</code> indicates everything is allowed that is not forbidden)
     * @param connected whether to include even transitions not occurring in the
     * training data.
     * @param iterations number of training iterations
     * @param var Gaussian prior variance
     * @param crf
     * @return the trained model
     */
    public static CRF train(InstanceList training, InstanceList testing,
            TransducerEvaluator eval, int[] orders,
            String defaultLabel,
            String forbidden, String allowed,
            boolean connected, int iterations, double var, CRF crf) {

        Pattern forbiddenPat = Pattern.compile(forbidden);
        Pattern allowedPat = Pattern.compile(allowed);
        if (crf == null) {
            crf = new CRF(training.getPipe(), (Pipe) null);
            String startName
                    = crf.addOrderNStates(training, orders, null,
                            defaultLabel, forbiddenPat, allowedPat,
                            connected);
            for (int i = 0; i < crf.numStates(); i++) {
                crf.getState(i).setInitialWeight(Transducer.IMPOSSIBLE_WEIGHT);
            }
            crf.getState(startName).setInitialWeight(0.0);
        }

        logger.info("Training on " + training.size() + " instances");
        if (testing != null) {
            logger.info("Testing on " + testing.size() + " instances");
        }

        assert (numThreads.value > 0);
        if (numThreads.value > 1) {
            CRFTrainerByThreadedLabelLikelihood crft = new CRFTrainerByThreadedLabelLikelihood(crf, numThreads.value);
            crft.setGaussianPriorVariance(var);

            if (weightsOption.value.equals("dense")) {
                crft.setUseSparseWeights(false);
                crft.setUseSomeUnsupportedTrick(false);
            } else if (weightsOption.value.equals("some-dense")) {
                crft.setUseSparseWeights(true);
                crft.setUseSomeUnsupportedTrick(true);
            } else if (weightsOption.value.equals("sparse")) {
                crft.setUseSparseWeights(true);
                crft.setUseSomeUnsupportedTrick(false);
            } else {
                throw new RuntimeException("Unknown weights option: " + weightsOption.value);
            }

            if (featureInductionOption.value) {
                throw new IllegalArgumentException("Multi-threaded feature induction is not yet supported.");
            } else {
                boolean converged;
                for (int i = 1; i <= iterations; i++) {
                    converged = crft.train(training, 1);
                    if (i % 1 == 0 && eval != null) { // Change the 1 to higher integer to evaluate less often
                        eval.evaluate(crft);
                    }
                    if (viterbiOutputOption.value && i % 10 == 0) {
                        new ViterbiWriter("", new InstanceList[]{training, testing}, new String[]{"training", "testing"}).evaluate(crft);
                    }
                    if (converged) {
                        break;
                    }
                }
            }
            crft.shutdown();
        } else {
            CRFTrainerByLabelLikelihood crft = new CRFTrainerByLabelLikelihood(crf);
            crft.setGaussianPriorVariance(var);

            if (weightsOption.value.equals("dense")) {
                crft.setUseSparseWeights(false);
                crft.setUseSomeUnsupportedTrick(false);
            } else if (weightsOption.value.equals("some-dense")) {
                crft.setUseSparseWeights(true);
                crft.setUseSomeUnsupportedTrick(true);
            } else if (weightsOption.value.equals("sparse")) {
                crft.setUseSparseWeights(true);
                crft.setUseSomeUnsupportedTrick(false);
            } else {
                throw new RuntimeException("Unknown weights option: " + weightsOption.value);
            }

            if (featureInductionOption.value) {
                crft.trainWithFeatureInduction(training, null, testing, eval, iterations, 10, 20, 500, 0.5, false, null);
            } else {
                boolean converged;
                for (int i = 1; i <= iterations; i++) {
                    converged = crft.train(training, 1);
                    if (i % 1 == 0 && eval != null) { // Change the 1 to higher integer to evaluate less often
                        eval.evaluate(crft);
                    }

                    if (viterbiOutputOption.value && i % 10 == 0) {
                        new ViterbiWriter("", new InstanceList[]{training, testing}, new String[]{"training", "testing"}).evaluate(crft);
                    }

                    if (converged) {
                        logger.info("converged");
                        break;
                    }
                }
            }
        }

        return crf;
    }

    
    //not used
    public static void train(String[] args) throws Exception {
        System.out.println("Train:");
        Reader trainingFile = null, testFile = null;
        InstanceList trainingData = null, testData = null;
        int numEvaluations = 0;
        int iterationsBetweenEvals = 16;
        int restArgs = commandOptions.processOptions(args);

        System.out.println("RestArgs: " + restArgs);
        for (int i = 0; i < args.length; i++) {
            System.out.println(i + ": " + args[i]);
        }

        if (restArgs == args.length) {
            commandOptions.printUsage(true);
            throw new IllegalArgumentException("Missing data file(s)");
        }
        System.out.println("Train option: " + trainOption.value);
        System.out.println("Test option: " + testOption.value);

        if (trainOption.value) {
            System.out.println(">> in 1");
            trainingFile = new FileReader(new File(args[restArgs]));
            if (testOption.value != null && restArgs < args.length - 1) {
                testFile = new FileReader(new File(args[restArgs + 1]));
            }
        } else { // bukan untuk train
            testFile = new FileReader(new File(args[restArgs]));
        }

        Pipe p = null;
        CRF crf = null;
        TransducerEvaluator eval = null;

        if (continueTrainingOption.value || !trainOption.value) { // bukan untuk train
            if (modelOption.value == null) {
                commandOptions.printUsage(true);
                throw new IllegalArgumentException("Missing model file option");
            }
            ObjectInputStream s
                    = new ObjectInputStream(new FileInputStream(modelOption.value));
            crf = (CRF) s.readObject();
            s.close();
            p = crf.getInputPipe();
        } else {
            System.out.println(">> in 2");
            p = new SimpleTagger.SimpleTaggerSentence2FeatureVectorSequence();
            p.getTargetAlphabet().lookupIndex(defaultOption.value);
        }

        if (trainOption.value) {
            System.out.println(">> in 3");
            p.setTargetProcessing(true);
            trainingData = new InstanceList(p);
            trainingData.addThruPipe(new LineGroupIterator(trainingFile,
                    Pattern.compile("^\\s*$"), true));

            logger.info("Number of features in training data: " + p.getDataAlphabet().size());
            System.out.println("Data alphabet: " + p.getDataAlphabet());

            if (testOption.value != null) { // bukan untuk train
                if (testFile != null) {
                    testData = new InstanceList(p);
                    testData.addThruPipe(new LineGroupIterator(testFile,
                            Pattern.compile("^\\s*$"), true));
                } else {
                    Random r = new Random(randomSeedOption.value);
                    InstanceList[] trainingLists
                            = trainingData.split(r, new double[]{trainingFractionOption.value,
                                1 - trainingFractionOption.value});
                    trainingData = trainingLists[0];
                    testData = trainingLists[1];
                }
            }
        } else if (testOption.value != null) { // bukan untuk train
            p.setTargetProcessing(true);
            testData = new InstanceList(p);
            testData.addThruPipe(new LineGroupIterator(testFile,
                    Pattern.compile("^\\s*$"), true));
        } else { // bukan untuk train
            p.setTargetProcessing(false);
            testData = new InstanceList(p);
            testData.addThruPipe(
                    new LineGroupIterator(testFile,
                            Pattern.compile("^\\s*$"), true));
        }
        logger.info("Number of predicates: " + p.getDataAlphabet().size());

        if (testOption.value != null) { // bukan untuk train
            if (testOption.value.startsWith("lab")) {
                eval = new TokenAccuracyEvaluator(new InstanceList[]{trainingData, testData}, new String[]{"Training", "Testing"});
            } else if (testOption.value.startsWith("seg=")) {
                String[] pairs = testOption.value.substring(4).split(",");
                if (pairs.length < 1) {
                    commandOptions.printUsage(true);
                    throw new IllegalArgumentException("Missing segment start/continue labels: " + testOption.value);
                }
                String startTags[] = new String[pairs.length];
                String continueTags[] = new String[pairs.length];

                for (int i = 0; i < pairs.length; i++) {
                    String[] pair = pairs[i].split("\\.");
                    if (pair.length != 2) {
                        commandOptions.printUsage(true);
                        throw new IllegalArgumentException("Incorrectly-specified segment start and end labels: " + pairs[i]);
                    }
                    startTags[i] = pair[0];
                    continueTags[i] = pair[1];
                }
                eval = new MultiSegmentationEvaluator(new InstanceList[]{trainingData, testData}, new String[]{"Training", "Testing"},
                        startTags, continueTags);
            } else {
                commandOptions.printUsage(true);
                throw new IllegalArgumentException("Invalid test option: "
                        + testOption.value);
            }
        }

        if (p.isTargetProcessing()) {
            System.out.println(">> in 4");
            Alphabet targets = p.getTargetAlphabet();
            System.out.println("targets: " + p.getTargetAlphabet());

            StringBuffer buf = new StringBuffer("Labels:");
            for (int i = 0; i < targets.size(); i++) {
                buf.append(" ").append(targets.lookupObject(i).toString());
            }
            logger.info(buf.toString());
        }

        if (trainOption.value) {
//            System.out.println("CRF Training..");
//            System.out.println("Orders: " + ordersOption.valueToString());
//            System.out.println("Default Option: " + defaultOption.value);
//            System.out.println("Forbidden Option: " + forbiddenOption.value);
//            System.out.println("allowed Option: " + allowedOption.value);
//            System.out.println("Forbidden Option: " + forbiddenOption.value);
//            System.out.println("connected Option: " + connectedOption.value);
//            System.out.println("iterations Option: " + iterationsOption.value);
//            System.out.println("gaussianVariance Option: " + gaussianVarianceOption.value);
//            
            crf = train(trainingData, testData, eval,
                    ordersOption.value, defaultOption.value,
                    forbiddenOption.value, allowedOption.value,
                    connectedOption.value, iterationsOption.value,
                    gaussianVarianceOption.value, crf);

            if (modelOption.value != null) {
                System.out.println("Print model ke file..");
                System.out.println(">> in 6");
                ObjectOutputStream s
                        = new ObjectOutputStream(new FileOutputStream(modelOption.value));
                s.writeObject(crf);
                s.close();
            }

        } else { // bukan untuk train
            if (crf == null) {
                if (modelOption.value == null) {
                    commandOptions.printUsage(true);
                    throw new IllegalArgumentException("Missing model file option");
                }
                ObjectInputStream s
                        = new ObjectInputStream(new FileInputStream(modelOption.value));
                crf = (CRF) s.readObject();
                s.close();
            }
            if (eval != null) {
                test(new NoopTransducerTrainer(crf), eval, testData);
            } else {
                boolean includeInput = includeInputOption.value();
                for (int i = 0; i < testData.size(); i++) {
                    Sequence input = (Sequence) testData.get(i).getData();
                    Sequence[] outputs = apply(crf, input, nBestOption.value);
                    int k = outputs.length;
                    boolean error = false;
                    for (int a = 0; a < k; a++) {
                        if (outputs[a].size() != input.size()) {
                            logger.info("Failed to decode input sequence " + i + ", answer " + a);
                            error = true;
                        }
                    }
                    if (!error) {
                        for (int j = 0; j < input.size(); j++) {
                            StringBuffer buf = new StringBuffer();
                            for (int a = 0; a < k; a++) {
                                buf.append(outputs[a].get(j).toString()).append(" ");
                            }
                            if (includeInput) {
                                FeatureVector fv = (FeatureVector) input.get(j);
                                buf.append(fv.toString(true));
                            }
                            System.out.println(buf.toString());
                        }
                        System.out.println();
                    }
                }
            }
        }

        if (trainingFile != null) {
            trainingFile.close();
        }
        if (testFile != null) {
            testFile.close();
        }

    }

    public static void classify(String[] args) throws Exception {
        System.out.println("Classify");
        Reader trainingFile = null, testFile = null;
        InstanceList trainingData = null, testData = null;
        int numEvaluations = 0;
        int iterationsBetweenEvals = 16;
        int restArgs = commandOptions.processOptions(args);
        System.out.println(restArgs);

        if (restArgs == args.length) {
            commandOptions.printUsage(true);
            throw new IllegalArgumentException("Missing data file(s)");
        }

        if (trainOption.value) { //Bukan untuk classify
            trainingFile = new FileReader(new File(args[restArgs]));
            if (testOption.value != null && restArgs < args.length - 1) {
                testFile = new FileReader(new File(args[restArgs + 1]));
            }
        } else {
            System.out.println(">>in 1");
            testFile = new FileReader(new File(args[restArgs]));
        }

        Pipe p = null;
        CRF crf = null;
        TransducerEvaluator eval = null;

        if (continueTrainingOption.value || !trainOption.value) {
            System.out.println(">>in 2");
            if (modelOption.value == null) { //Bukan untuk classify
                commandOptions.printUsage(true);
                throw new IllegalArgumentException("Missing model file option");
            }
            ObjectInputStream s
                    = new ObjectInputStream(new FileInputStream(modelOption.value));
            crf = (CRF) s.readObject();
            s.close();
            p = crf.getInputPipe();
        } else { //bukan untuk classify
            p = new SimpleTagger.SimpleTaggerSentence2FeatureVectorSequence();
            p.getTargetAlphabet().lookupIndex(defaultOption.value);
        }

        if (trainOption.value) { //bukan untuk classify
            p.setTargetProcessing(true);
            trainingData = new InstanceList(p);
            trainingData.addThruPipe(new LineGroupIterator(trainingFile,
                    Pattern.compile("^\\s*$"), true));

            logger.info("Number of features in training data: " + p.getDataAlphabet().size());

            if (testOption.value != null) {
                if (testFile != null) {
                    testData = new InstanceList(p);
                    testData.addThruPipe(new LineGroupIterator(testFile,
                            Pattern.compile("^\\s*$"), true));
                } else {
                    Random r = new Random(randomSeedOption.value);
                    InstanceList[] trainingLists
                            = trainingData.split(r, new double[]{trainingFractionOption.value,
                                1 - trainingFractionOption.value});
                    trainingData = trainingLists[0];
                    testData = trainingLists[1];
                }
            }
        } else if (testOption.value != null) { //bukan untuk classify
            p.setTargetProcessing(true);
            testData = new InstanceList(p);
            testData.addThruPipe(new LineGroupIterator(testFile,
                    Pattern.compile("^\\s*$"), true));
        } else {
            System.out.println(">>in 3");
            p.setTargetProcessing(false);
            testData = new InstanceList(p);
            testData.addThruPipe(
                    new LineGroupIterator(testFile,
                            Pattern.compile("^\\s*$"), true));
        }
        logger.info("Number of predicates: " + p.getDataAlphabet().size());

        if (testOption.value != null) { //bukan untuk classify
            if (testOption.value.startsWith("lab")) {
                eval = new TokenAccuracyEvaluator(new InstanceList[]{trainingData, testData}, new String[]{"Training", "Testing"});
            } else if (testOption.value.startsWith("seg=")) {
                String[] pairs = testOption.value.substring(4).split(",");
                if (pairs.length < 1) {
                    commandOptions.printUsage(true);
                    throw new IllegalArgumentException("Missing segment start/continue labels: " + testOption.value);
                }
                String startTags[] = new String[pairs.length];
                String continueTags[] = new String[pairs.length];

                for (int i = 0; i < pairs.length; i++) {
                    String[] pair = pairs[i].split("\\.");
                    if (pair.length != 2) {
                        commandOptions.printUsage(true);
                        throw new IllegalArgumentException("Incorrectly-specified segment start and end labels: " + pairs[i]);
                    }
                    startTags[i] = pair[0];
                    continueTags[i] = pair[1];
                }
                eval = new MultiSegmentationEvaluator(new InstanceList[]{trainingData, testData}, new String[]{"Training", "Testing"},
                        startTags, continueTags);
            } else {
                commandOptions.printUsage(true);
                throw new IllegalArgumentException("Invalid test option: "
                        + testOption.value);
            }
        }

        if (p.isTargetProcessing()) { // bukan untuk classify
            Alphabet targets = p.getTargetAlphabet();
            StringBuffer buf = new StringBuffer("Labels:");
            for (int i = 0; i < targets.size(); i++) {
                buf.append(" ").append(targets.lookupObject(i).toString());
            }
            logger.info(buf.toString());
        }

        if (trainOption.value) {
            crf = train(trainingData, testData, eval,
                    ordersOption.value, defaultOption.value,
                    forbiddenOption.value, allowedOption.value,
                    connectedOption.value, iterationsOption.value,
                    gaussianVarianceOption.value, crf);
            if (modelOption.value != null) {
                ObjectOutputStream s
                        = new ObjectOutputStream(new FileOutputStream(modelOption.value));
                s.writeObject(crf);
                s.close();
            }
        } else {
            if (crf == null) { //udah pernah baca, ga usah lagi
                if (modelOption.value == null) {

                    System.out.println("ccc");
                    commandOptions.printUsage(true);
                    throw new IllegalArgumentException("Missing model file option");
                }
                ObjectInputStream s
                        = new ObjectInputStream(new FileInputStream(modelOption.value));
                crf = (CRF) s.readObject();
                s.close();
            }
            if (eval != null) { //Bukan untuk classify
                test(new NoopTransducerTrainer(crf), eval, testData);
            } else {
                System.out.println(">>in 4");
                boolean includeInput = includeInputOption.value();
                for (int i = 0; i < testData.size(); i++) {
                    Sequence input = (Sequence) testData.get(i).getData();
                    Sequence[] outputs = apply(crf, input, nBestOption.value);

                    int k = outputs.length;
                    boolean error = false;
                    for (int a = 0; a < k; a++) {
                        if (outputs[a].size() != input.size()) {
                            logger.info("Failed to decode input sequence " + i + ", answer " + a);
                            error = true;
                        }
                    }
                    if (!error) {
                        for (int j = 0; j < input.size(); j++) {
                            StringBuffer buf = new StringBuffer();
                            for (int a = 0; a < k; a++) {
                                buf.append(outputs[a].get(j).toString()).append(" ");

                            }
                            if (includeInput) {
                                FeatureVector fv = (FeatureVector) input.get(j);
                                buf.append(fv.toString(true));
                            }
                            System.out.println(buf.toString());
                        }
                        System.out.println();
                    }
                }
            }
        }

        if (trainingFile != null) {
            trainingFile.close();
        }
        if (testFile != null) {
            testFile.close();
        }
    }

}
