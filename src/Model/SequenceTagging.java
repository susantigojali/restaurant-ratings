package Model;

import cc.mallet.types.Sequence;

/**
 *
 * @author susanti_2
 */
public class SequenceTagging {
    private Sequence input;
    private Sequence[] output;

    public SequenceTagging(Sequence input, Sequence[] output) {
        this.input = input;
        this.output = output;
    }

    public Sequence getInput() {
        return input;
    }

    public Sequence[] getOutput() {
        return output;
    }
    
}
