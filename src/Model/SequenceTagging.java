package Model;

import cc.mallet.types.Sequence;
import java.util.ArrayList;

/**
 *
 * @author susanti_2
 */
public class SequenceTagging {

//    private Sequence input;
    private Sequence[] output;
    private ArrayList<Feature> sequenceInput;

//    public SequenceTagging(Sequence input, Sequence[] output) {
//        this.input = input;
//        this.output = output;
//    }

    public SequenceTagging(ArrayList<Feature> sequenceInput, Sequence[] output) {
        this.sequenceInput = sequenceInput;
        this.output = output;
    }

//    public Sequence getInput() {
//        return input;
//    }

    public Sequence[] getOutput() {
        return output;
    }

    public ArrayList<Feature> getSequenceInput() {
        return sequenceInput;
    }

//    public void setInput(Sequence input) {
//        this.input = input;
//    }

    public void setOutput(Sequence[] output) {
        this.output = output;
    }
    
    public void print() {
        for (int i = 0; i < sequenceInput.size(); i++) {
            String word = sequenceInput.get(i).getWord();
            String postag =  sequenceInput.get(i).getPostag();
            
            System.out.print("feature: " + word + " " + postag + " ");
            if (output.length != 0) {
                String label = output[0].get(i).toString();
                System.out.print(label);
            }
            System.out.println();
            
        }
    }
    
}
