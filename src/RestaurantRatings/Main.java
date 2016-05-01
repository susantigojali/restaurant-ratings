package RestaurantRatings;

import DataProcessing.Postprocess;
import DataProcessing.Preprocess;
import Learning.MyCRFSimpleTagger;
import Model.AspectSentiment;
import Model.Reader;
import Model.Review;
import Model.SequenceTagging;
import cc.mallet.fst.CRF;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

/**
 *
 * @author susanti_2
 */
public class Main {

    public static CRF getModelCRF(String modelFilename) throws IOException, ClassNotFoundException {
        ObjectInputStream s = new ObjectInputStream(new FileInputStream(modelFilename));
        CRF crf = (CRF) s.readObject();
        return crf;
    }

    public static void main(String args[]) {
        //Step 1
        String rawFilename = "datatest/RawDatatest.txt";
        try {
            //Read review from file
            ArrayList<Review> reviews = Reader.readReviewFromFile(rawFilename);
            
            //Preprocess
            String dataNBC = "datatest/NBCdata.csv";
            Preprocess.preprocessDataforNBC(reviews, dataNBC);
            
            //Klasifikasi Subjektivitas: to do
            
            
        } catch (FileNotFoundException ex) {
            System.out.println("File not found exception");
        } catch (IOException ex) {
            System.out.println("IO Exception");
        }

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
