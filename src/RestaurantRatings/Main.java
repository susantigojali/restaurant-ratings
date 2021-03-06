package RestaurantRatings;

import DataProcessing.Postprocess;
import DataProcessing.Preprocess;
import Evaluator.AspectAggregationEvaluator;
import Evaluator.ExtractionEvaluator;
import Learning.MyCRFSimpleTagger;
import Learning.WekaHelper;
import Model.AspectAggregation;
import Model.AspectSentiment;
import Model.Csv2Arff;
import Model.Rating;
import Model.Reader;
import Model.Review;
import Model.SequenceTagging;
import cc.mallet.fst.CRF;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSink;

/**
 *
 * @author susanti_2
 */
public class Main {

    private static final String NBC_MODEL = "datatest/Model/nbc AA 6F (1688 modif).model";
    private static final String J48_MODEL = "datatest/Model/J48 B - 7 Fitur (1696).model";
    private static final String CRF_MODEL = "datatest/Model/crfFULL1 (605).model";

    private static final int SUBJECTIVE_INDEX = 0;
    private static final int FORMALIZED_SENTENCE_INDEX = 0;

    private static final String NEW_LINE_SEPARATOR = "\n";

    public static int INDEX = 0;
    /**
     * read model from file
     *
     * @param modelFilename model filename
     * @return CRF
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static CRF loadModelCRF(String modelFilename) throws IOException, ClassNotFoundException {
        ObjectInputStream s = new ObjectInputStream(new FileInputStream(modelFilename));
        CRF crf = (CRF) s.readObject();
        return crf;
    }

    /**
     * Prepare data for annotation (NBC Learning)
     */
    public static void prepareDataLearningNBC() {

        String rawFilename = "datatest/NBC/RawDatatest.txt";
        String dataNbc = "datatest/NBC/NBCData";
        try {
            ArrayList<Review> reviews = Reader.readReviewFromFile(rawFilename);
            classifySentenceSubjectivity(reviews, dataNbc, true);
        } catch (FileNotFoundException ex) {
            System.out.println("File not found: prepare data learning NBC");
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    /**
     * Prepare data for annotation (CRF Learning)
     * @throws java.io.IOException
     */
    public static void prepareDataLearningCRF() throws IOException {
        String rawCrfFilename = "dataset/CRF/rawdata_FULL2 (992) .txt";
        String dataCRF = "dataset/CRF/CRFAnotasif (file temporary because of app).csv";
        Preprocess.preprocessDataforSequenceTagging(rawCrfFilename, dataCRF, false);
        
        Boolean includeInput = true;
        int nBestOption = 1;
        ArrayList<SequenceTagging> inputSequence = Reader.readSequenceInput(dataCRF);
        
        try {
            String dataCRFAnotasi = "dataset/CRF/CRFAnotasif new.csv";
            ArrayList<SequenceTagging> outputs = MyCRFSimpleTagger.myClassify(dataCRF, CRF_MODEL, includeInput, nBestOption, inputSequence);
            
            saveClassifyCRF(dataCRFAnotasi, outputs);
            
        } catch (FileNotFoundException ex) {
            System.out.println("file not found: data , model CRF");
        } catch (ClassNotFoundException ex) {
            System.out.println("class not found: model");
        }
    }
    
    //save to file CRF yang udah dilabelim
    private static void saveClassifyCRF(String filename, ArrayList<SequenceTagging> outputs) throws IOException {
        File file = new File(filename);
        if (!file.exists()) {
            file.createNewFile();
        } else {
            System.out.println("File sudah ada");
            System.exit(-1);
        }

        FileWriter fw = new FileWriter(file);
        try (BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write("sep=;" + NEW_LINE_SEPARATOR);
            for (SequenceTagging output : outputs) {
                for (int i = 0; i < output.getSequenceInput().size(); i++) {
                    bw.write(output.getSequenceInput().get(i).getWord() + ";" + output.getSequenceInput().get(i).getPostag() + ";");
                    bw.write(output.getOutput().get(i) + NEW_LINE_SEPARATOR);
                }
                bw.write(NEW_LINE_SEPARATOR);
            }
        }
    }

    public static void saveSubjectiveInstance(String filename, Instances instances) throws IOException {
        File file = new File(filename);
        if (!file.exists()) {
            file.createNewFile();
        }

        FileWriter fw = new FileWriter(file);
        try (BufferedWriter bw = new BufferedWriter(fw)) {
            for (Instance inst : instances) {
                if (inst.classAttribute().value((int) inst.classValue()).equals(inst.attribute(inst.classAttribute().index()).value(SUBJECTIVE_INDEX))) {
                    bw.write(inst.stringValue(FORMALIZED_SENTENCE_INDEX) + NEW_LINE_SEPARATOR);
                }
            }
        }
    }

    public static void main(String args[]) {
        try {
//            prepareDataLearningNBC();
//            prepareDataLearningCRF();
            startApp();
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //for testing
    /**
     * run the application
     *
     */
    public static void startApp() {
        double fExtraction = 0.0 ;
        double precExtraction = 0.0 ;
        double recExtraction = 0.0 ;
        double fAgg = 0.0 ;
        double precAgg = 0.0 ;
        double recAgg = 0.0 ;
       
        try {
            //for extraction evaluation
            String dataExtractionAS = "datatest/CRF/CRFExtraction.txt";
            ArrayList<ArrayList<AspectSentiment>> actualAspectSentiments = Reader.readActualAspectSentiment(dataExtractionAS);
            
            //for aggregation evaluation
            String dataAspectAggregation = "datatest/CRF/AspectAggregation.txt";
            ArrayList<LinkedHashMap<String, ArrayList<AspectSentiment>>> actualAspectAggregation = Reader.readAspectAggregation(dataAspectAggregation);
            
            //Start Application
            
            String rawFilename = "datatest/NBC/RawDatatest.txt";
            String dataNbcCsv = "datatest/NBC/NBCData";
            ArrayList<Review> reviews = Reader.readReviewFromFile(rawFilename);
            for (Review review:reviews) {
                System.out.println("======================================================================= "+INDEX);
                ArrayList<Review> reviewsTemp = new ArrayList<>();
                reviewsTemp.add(review);
                
                //Step 1 Subjectivity Classification
                classifySentenceSubjectivity(reviewsTemp, dataNbcCsv+INDEX, false);
                
                //Step 2
                String rawCrfFilename = "datatest/CRF/rawdata";//.txt";
                String dataCRF = "datatest/CRF/CRFData";//.txt";
                Boolean includeInput = true;
                int nBestOption = 1;

                try {
                    //Preprocess CRF 
                    Preprocess.preprocessDataforSequenceTagging(rawCrfFilename+INDEX+".txt", dataCRF+INDEX+".txt", false);

                    //Klasifikasi CRF
                    ArrayList<SequenceTagging> inputSequence = Reader.readSequenceInput(dataCRF+INDEX+".txt");
                    ArrayList<SequenceTagging> outputs = MyCRFSimpleTagger.myClassify(dataCRF+INDEX+".txt", CRF_MODEL, includeInput, nBestOption, inputSequence);
                    
                    //for debugging extraction
//                    for (int i = 0; i < outputs.size(); i++) {
//                        for (int j = 0; j < outputs.get(i).getSequenceInput().size(); j++) {
//                             System.out.println(outputs.get(i).getSequenceInput().get(j).getWord() + " "+
//                                    outputs.get(i).getSequenceInput().get(j).getPostag() + " "+
//                                    outputs.get(i).getOutput().get(j));
//                        }
//                       
//                    }
                    
                    //Extract Aspect and Opinion
                    ArrayList<AspectSentiment> as = new ArrayList<>();
                    for (SequenceTagging output : outputs) {
                        as.addAll(Postprocess.findAspectSentiment(output)); 
                    }

//                    System.out.println("\n\nEkstrak pasangan aspek dan sentimen----------------------");
//                    System.out.println("____________________________");
//                    for (int j = 0; j < as.size(); j++) {
//                        System.out.println("index: " + j);
//                        as.get(j).print();
//                    }
//                    System.out.println("____________________________");
                    
                    //Evaluation extraction aspect and sentiment
                    ExtractionEvaluator.evaluate(as, actualAspectSentiments.get(INDEX));
                    ExtractionEvaluator.printEvaluation();
                    fExtraction+=ExtractionEvaluator.getF1();
                    precExtraction+=ExtractionEvaluator.getPrecision();
                    recExtraction+=ExtractionEvaluator.getRecall();
                  
                    //Agregasi Aspek
//                    System.out.println("\n\nAgregasi Aspek-------------------------");
                    LinkedHashMap<String, ArrayList<AspectSentiment>> aggregation = AspectAggregation.aggregation(as);
                    System.out.println("\n\nTotal Category :"+ aggregation.size());

                    for (String key : aggregation.keySet()) {
                        ArrayList<AspectSentiment> value = aggregation.get(key);
                        double rating = Rating.getRating(value);
                        System.out.println(key + " ========= rating: " + rating);
                        for (int i = 0; i < value.size(); i++) {
                            System.out.print("\t");
                            value.get(i).print();
                        }
                    }
                    
                    //Evaluation aspect aggregation
                    AspectAggregationEvaluator.evaluate(aggregation, actualAspectAggregation.get(INDEX));
                    AspectAggregationEvaluator.printEvaluation();
                    fAgg+=AspectAggregationEvaluator.getF1();
                    precAgg+=AspectAggregationEvaluator.getPrecision();
                    recAgg+=AspectAggregationEvaluator.getRecall();
                    
                    
                } catch (IOException ex) {
                    System.out.println("IO Exeption");
                } catch (ClassNotFoundException ex) {
                    System.out.println("Class not found exception");
                }
                
                INDEX++;
            }
            
            System.out.println("Extraction::");
            System.out.println("prec:  "+precExtraction/reviews.size());
            System.out.println("rec: " + recExtraction/reviews.size());
            System.out.println("f1: " + fExtraction/reviews.size());
            
            System.out.println("Aggregation::");
            System.out.println("prec:  "+precAgg/reviews.size());
            System.out.println("rec: " + recAgg/reviews.size());
            System.out.println("f1: " + fAgg/reviews.size());
            
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    private static void classifySentenceSubjectivity(ArrayList<Review> reviews, String dataNbc, boolean isLearning) {
        //Step 1 Subjectivity Classification
        //String rawFilename = "datatest/NBC/RawDatatest.txt";
        try {
            //Read review from file
            //ArrayList<Review> reviews = Reader.readReviewFromFile(rawFilename);

            //Preprocess
            String dataNbcCsv = dataNbc + ".csv";
            String dataNbcArff = dataNbc + "(delete this).arff";
            Preprocess.preprocessDataforNBC(reviews, dataNbcCsv);
            Csv2Arff.convert(dataNbcCsv, dataNbcArff);

            //load ARFF datatest
            Instances dataTest = WekaHelper.loadDataFromFile(dataNbcArff);
            dataTest.setClassIndex(1);

            //load model
            NaiveBayes NB = (NaiveBayes) WekaHelper.loadModelFromFile(NBC_MODEL);
            J48 J48 = (J48) WekaHelper.loadModelFromFile(J48_MODEL);
//            System.out.println(NB.getHeader());

            //Classification
            Instances unlabeledData = WekaHelper.removeAttribute(dataTest, "1"); //remove attribute formalized_sentence
            
            for (int i = 0; i < unlabeledData.size(); i++) {
                double label = NB.classifyInstance(unlabeledData.instance(i));
//                double label = J48.classifyInstance(unlabeledData.instance(i));
                unlabeledData.instance(i).setClassValue(label);
                dataTest.instance(i).setClassValue(label);
            }

//            System.out.println("DATATEST=="); //for debugging
//            DataSink.write(System.out, dataTest); //for debugging
            
            if (!isLearning) {
                //Save data with label subjective
                String filename = "datatest/CRF/rawdata"+INDEX+".txt";
                saveSubjectiveInstance(filename, dataTest);
            } else {
                //for annotation to get the label
                printLabel(dataTest);
            }
            
        } catch (FileNotFoundException ex) {
            System.out.println("File not found exception");
        } catch (IOException ex) {
            System.out.println("IO Exception");
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    
    private static void printLabel(Instances instances) {
        System.out.println("\nLabel==========");
        for (Instance inst : instances) {
            System.out.println(inst.classAttribute().value((int) inst.classValue()));
        }
    }
}
