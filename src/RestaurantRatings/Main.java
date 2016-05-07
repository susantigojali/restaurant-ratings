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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSink;

/**
 *
 * @author susanti_2
 */
public class Main {

    private static final String NBC_MODEL = "datatest/Model/nbc.model";

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
        String rawFilename = "datatest/DataLearningNBC2 (120).txt";
        try {
            //Read review from file
            ArrayList<Review> reviews = Reader.readReviewFromFile(rawFilename);

            //Preprocess
            String dataNBC = "datatest/NBC/DataLearningNBC2 (120) NBCAnotasi.csv";
            Preprocess.preprocessDataforNBC(reviews, dataNBC, false);

        } catch (FileNotFoundException ex) {
            System.out.println("File not found exception");
        } catch (IOException ex) {
            System.out.println("IO Exception");
        }
    }

    public static void main(String args[]) {
        try {
            startApp();
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //for testing
    /**
     * run the application
     * @throws Exception
     */
    public static void startApp() throws Exception {
        //Step 1 Subjectivity Classification
        String rawFilename = "datatest/RawDatatest.txt";
        try {
            //Read review from file
            ArrayList<Review> reviews = Reader.readReviewFromFile(rawFilename);

            //Preprocess
            String dataNbcCsv = "datatest/NBCData.csv";
            String dataNbcArff = "datatest/NBCData.arff";
            Preprocess.preprocessDataforNBC(reviews, dataNbcCsv, false);
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

        } catch (FileNotFoundException ex) {
            System.out.println("File not found exception");
        } catch (IOException ex) {
            System.out.println("IO Exception");
        }
        System.exit(-1);

        //Step 2
        String modelFilename = "crf.model";
        String crfFilename = "dataset/CRF/CRFDatatest.txt";
        Boolean includeInput = true;
        int nBestOption = 1;

        try {
            //Preprocess CRF : to do

            //Klasifikasi CRF
            ArrayList<SequenceTagging> inputSequence = Reader.readSequenceInput(crfFilename);
            ArrayList<SequenceTagging> outputs = MyCRFSimpleTagger.myClassify(crfFilename, modelFilename, includeInput, nBestOption, inputSequence);

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

}
