package RestaurantRatings;

import DataProcessing.Postprocess;
import DataProcessing.Preprocess;
import Learning.MyCRFSimpleTagger;
import Learning.WekaHelper;
import Model.AspectSentiment;
import Model.Csv2Arff;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSink;

/**
 *
 * @author susanti_2
 */
public class Main {

    private static final String NBC_MODEL = "datatest/Model/nbc.model";
    private static final String CRF_MODEL = "datatest/Model/crf.model";

    private static final int SUBJECTIVE_INDEX = 0;
    private static final int FORMALIZED_SENTENCE_INDEX = 0;

    private static final String NEW_LINE_SEPARATOR = "\n";

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

        String rawFilename = "crawl-data/DataLearningNBC3 (120).csv";
        String dataNbcCsv = "dataset/NBC/DataLearningNBC3 (120) NBCAnotasi.csv";
        String dataNbcArff = "dataset/NBC/DataLearningNBC3 (120) NBCAnotasi.arff";
        classifyNBC(rawFilename, dataNbcCsv, dataNbcArff, true);

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
            startApp();
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //for testing
    /**
     * run the application
     *
     * @throws Exception
     */
    public static void startApp() {
        //Step 1 Subjectivity Classification
        String rawFilename = "datatest/NBC/RawDatatest.txt";
        String dataNbcCsv = "datatest/NBC/NBCData.csv";
        String dataNbcArff = "datatest/NBC/NBCData.arff";
       
        classifyNBC(rawFilename, dataNbcCsv, dataNbcArff, false);
        
        //Step 2
        String rawCrfFilename = "datatest/CRF/rawdata.txt";
        String dataCRF = "datatest/CRF/CRFData.txt";
        Boolean includeInput = true;
        int nBestOption = 1;

        try {
            //Preprocess CRF 
            Preprocess.preprocessDataforSequenceTagging(rawCrfFilename, dataCRF, false);
            
            //Klasifikasi CRF
            ArrayList<SequenceTagging> inputSequence = Reader.readSequenceInput(dataCRF);
            ArrayList<SequenceTagging> outputs = MyCRFSimpleTagger.myClassify(dataCRF, CRF_MODEL, includeInput, nBestOption, inputSequence);
            
            //Extract Aspect and Opinion
            for (SequenceTagging output : outputs) {
                ArrayList<AspectSentiment> as = Postprocess.findAspectSentiment(output);
                System.out.println("____________________________");
                for (int j = 0; j < as.size(); j++) {
                    System.out.println("index: " + j);
                    as.get(j).print();
                }
                System.out.println("____________________________");
            }

        } catch (IOException ex) {
            System.out.println("IO Exeption");
        } catch (ClassNotFoundException ex) {
            System.out.println("Class not found exception");
        }

        //Step 3
        //Agregasi Aspek: to do
        //Hitung Rating: to do
    }

    private static void classifyNBC(String rawFilename, String dataNbcCsv, String dataNbcArff, boolean isLearning) {
        //Step 1 Subjectivity Classification
        //String rawFilename = "datatest/NBC/RawDatatest.txt";
        try {
            //Read review from file
            ArrayList<Review> reviews = Reader.readReviewFromFile(rawFilename);

            //Preprocess
            //String dataNbcCsv = "datatest/NBC/NBCData.csv";
            //String dataNbcArff = "datatest/NBC/NBCData.arff";
            Preprocess.preprocessDataforNBC(reviews, dataNbcCsv);
            Csv2Arff.convert(dataNbcCsv, dataNbcArff);

            //load ARFF datatest
            Instances dataTest = WekaHelper.loadDataFromFile(dataNbcArff);
            dataTest.setClassIndex(1);

            //load model
            NaiveBayes NB = (NaiveBayes) WekaHelper.loadModelFromFile(NBC_MODEL);

            //Classification
            Instances unlabeledData = WekaHelper.removeAttribute(dataTest, "1"); //remove attribute formalized_sentence
            for (int i = 0; i < unlabeledData.size(); i++) {
                double label = NB.classifyInstance(unlabeledData.instance(i));
                unlabeledData.instance(i).setClassValue(label);
                dataTest.instance(i).setClassValue(label);
            }

            DataSink.write(System.out, dataTest);

            printLabel(dataTest);

            if (!isLearning) {
                //Save data with label subjective
                String filename = "datatest/CRF/rawdata.txt";
                saveSubjectiveInstance(filename, dataTest);
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
