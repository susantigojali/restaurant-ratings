package Model;

import java.util.ArrayList;

/**
 *
 * @author susanti_2
 */
public class SequenceTagging {

    private ArrayList<Feature> sequenceInput;
    private ArrayList<String> output;

    public SequenceTagging(ArrayList<Feature> sequenceInput, ArrayList<String> output) {
        this.sequenceInput = sequenceInput;
        this.output = output;
    }

    public ArrayList<String> getOutput() {
        return output;
    }

    public ArrayList<Feature> getSequenceInput() {
        return sequenceInput;
    }

    public void setOutput(ArrayList<String> output) {
        this.output = output;
    }
    
    public void print() {
        for (int i = 0; i < sequenceInput.size(); i++) {
            String word = sequenceInput.get(i).getWord();
            String postag =  sequenceInput.get(i).getPostag();
            
            System.out.print("feature: " + word + " " + postag + " ");
            if (!output.isEmpty()) {
                String label = output.get(i);
                System.out.print(label);
            }
            System.out.println();
            
        }
    }
    
}
