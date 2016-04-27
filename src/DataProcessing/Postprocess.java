package DataProcessing;

import Model.AspectSentiment;
import Model.SequenceTagging;
import cc.mallet.types.Sequence;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

/**
 *
 * @author susanti_2
 */
public class Postprocess {

    private static final ArrayList<String> TAG_ASPECT = new ArrayList<>(Arrays.asList("ASPECT-B", "ASPECT-I"));
    private static final ArrayList<String> TAG_SENTIMENT_POSITIVE = new ArrayList<>(Arrays.asList("OP_POS-B", "OP_POS-I"));
    private static final ArrayList<String> TAG_SENTIMENT_NEGATIVE = new ArrayList<>(Arrays.asList("OP_NEG-B", "OP_NEG-I"));

    public static ArrayList<AspectSentiment> findAspectSentiment(SequenceTagging data) {
        assert data.getOutput().length == 1;
        
        Sequence[] output = data.getOutput();
        Sequence input = data.getInput();

        ArrayList<String> aspects = findAllAspect(input, output);
        ArrayList<String> posOpinions = findAllPositiveOpinion(input, output);
        ArrayList<String> negOpinions = findAllNegativeOpinion(input, output);
        System.out.println("ASPECT=============");
        for (int i = 0; i < aspects.size(); i++) {
            System.out.println(i +" "+ aspects.get(i));
        }
        System.out.println("OP_POS=============");
        for (int i = 0; i < posOpinions.size(); i++) {
            System.out.println(i +" "+ posOpinions.get(i));
        }
        System.out.println("OP_NEG============");
        for (int i = 0; i < negOpinions.size(); i++) {
            System.out.println(i +" "+ negOpinions.get(i));
        }

        
        HashSet<AspectSentiment> aspectSentiment = new HashSet<>();
        //find sentiment for each aspect
        String aspect = "";
        String posOpinion = "";
        String negOpinion = "";
        
        for (int i = 0; i < output[0].size(); i++) {
            if (TAG_ASPECT.contains(output[0].get(i).toString())) {
                if (aspect.isEmpty()) {
                    aspect = input.get(i).toString();
                } else {
                    aspect = aspect + " " + input.get(i).toString();
                }
            } else {
                if (!aspect.isEmpty()) {
                    //find opinion after this aspect
                    boolean found = false;
                    int j = i;
                    while (!found && j < output[0].size()) {
                        if (TAG_SENTIMENT_POSITIVE.contains(output[0].get(j).toString())) {
                            if (!negOpinion.isEmpty()) {
                                aspectSentiment.add(new AspectSentiment(aspect, negOpinion, -1));
                                found = true;
                            } else {
                                if (posOpinion.isEmpty()) {
                                    posOpinion = input.get(j).toString();
                                } else {
                                    posOpinion = posOpinion + " " + input.get(j).toString();
                                }
                            }
                        } else if (TAG_SENTIMENT_NEGATIVE.contains(output[0].get(j).toString())) {
                            if (!posOpinion.isEmpty()) {
                                aspectSentiment.add(new AspectSentiment(aspect, posOpinion, 1));
                                found = true;
                            } else {
                                if (negOpinion.isEmpty()) {
                                    negOpinion = input.get(j).toString();
                                } else {
                                    negOpinion = negOpinion + " " + input.get(j).toString();
                                }
                            }
                        } else { //other or aspect
                            if (!posOpinion.isEmpty()) {
                                assert negOpinion.isEmpty(); //harus ga ada neg opinion
                                aspectSentiment.add(new AspectSentiment(aspect, posOpinion, 1));
                                found = true;
                            } else if (!negOpinion.isEmpty()) {
                                assert posOpinion.isEmpty(); //harus ga ada pos opinion
                                aspectSentiment.add(new AspectSentiment(aspect, negOpinion, -1));
                                found = true;
                            }
                        }
                        j++;
                    }

                    //ga ketemu sentimen apa-apa untuk aspek ini atau sentimennya di akhir kalimat
                    if (!found) {
                        if (!posOpinion.isEmpty()) {
                            assert negOpinion.isEmpty(); //harus ga ada neg opinion
                            aspectSentiment.add(new AspectSentiment(aspect, posOpinion, 1));
                        } else if (!negOpinion.isEmpty()) {
                            assert posOpinion.isEmpty(); //harus ga ada pos opinion
                            aspectSentiment.add(new AspectSentiment(aspect, negOpinion, -1));
                        } else {
                            aspectSentiment.add(new AspectSentiment(aspect, "", 0));
                        }
                    }
                    aspect = "";
                    posOpinion = "";
                    negOpinion = "";
                }
            }
        }
        if (!aspect.isEmpty()) { //aspek di akhir kalimat
            aspectSentiment.add(new AspectSentiment(aspect, "", 0));
            aspect = "";
        }

        //Find aspect for each positive sentiment
        for (int i = 0; i < output[0].size(); i++) {
            if (TAG_SENTIMENT_POSITIVE.contains(output[0].get(i).toString())) {
                if (posOpinion.isEmpty()) {
                    posOpinion = input.get(i).toString();
                } else {
                    posOpinion = posOpinion + " " + input.get(i).toString();
                }
            } else {
                if (!posOpinion.isEmpty()) {
                    //find aspect before this opinion
                    String aspectOpinion = findAspect(input, output, i);
                    aspectSentiment.add(new AspectSentiment(aspectOpinion, posOpinion, 1));
                    posOpinion = "";
                }
            }
        }
        if (!posOpinion.isEmpty()) {
            String aspectOpinion = findAspect(input, output, output[0].size());
            aspectSentiment.add(new AspectSentiment(aspectOpinion, posOpinion, 1));
        }
        
         //Find aspect for each negative sentiment
        for (int i = 0; i < output[0].size(); i++) {
            if (TAG_SENTIMENT_NEGATIVE.contains(output[0].get(i).toString())) {
                if (negOpinion.isEmpty()) {
                    negOpinion = input.get(i).toString();
                } else {
                    negOpinion = negOpinion + " " + input.get(i).toString();
                }
            } else {
                if (!negOpinion.isEmpty()) {
                    //find aspect before this opinion
                    String aspectOpinion = findAspect(input, output, i);
                    aspectSentiment.add(new AspectSentiment(aspectOpinion, negOpinion, -1));
                    negOpinion = "";
                }
            }
        }
        if (!negOpinion.isEmpty()) {
            String aspectOpinion = findAspect(input, output, output[0].size());
            aspectSentiment.add(new AspectSentiment(aspectOpinion, negOpinion, -1));
        }

        return new ArrayList<>(aspectSentiment);
    }
    
    
    /**
     * find aspect in output before index i
     * @param input sequence of input (word, postag)
     * @param output sequence of output (label)
     * @param i index
     * @return aspect 
     */
    public static String findAspect(Sequence input, Sequence[] output, int i) {
        boolean found = false;
        String aspect = "";
        int j = i - 1;
        while (!found && j >= 0) {
            if (TAG_ASPECT.contains(output[0].get(j).toString())) {
                if (aspect.isEmpty()) {
                    aspect = input.get(j).toString();
                } else {
                    aspect = input.get(j).toString() + " " + aspect;
                }
            } else { //other than aspect
                if (!aspect.isEmpty()) {
                    found = true;
                }
            }
            j--;
        }
        return aspect;
    }
    
    /**
     * find all aspects in this output
     * @param input sequence of input (word, postag)
     * @param output sequence of output (label)
     * @return aspects
     */
    public static ArrayList<String> findAllAspect(Sequence input, Sequence[] output) {
        ArrayList<String> aspects = new ArrayList<>();
        String aspect = "";
        for (int i = 0; i < output[0].size(); i++) {
            if (TAG_ASPECT.contains(output[0].get(i).toString())) {
                 if (aspect.isEmpty()) {
                    aspect = input.get(i).toString();
                } else {
                    aspect = aspect + " " + input.get(i).toString();
                }
            } else {
                if (!aspect.isEmpty()) {
                    aspects.add(aspect);
                    aspect = "";
                }
            }
        }

        if (!aspect.isEmpty()) {
            aspects.add(aspect);
        }
        
        return aspects;
    }
    
    /**
     * find all positive opinions in this output
     * @param input sequence of input (word, postag)
     * @param output sequence of output (label)
     * @return positive opinions
     */
    public static ArrayList<String> findAllPositiveOpinion(Sequence input, Sequence[] output) {
        ArrayList<String> posOpinions = new ArrayList<>();
        String posOpinion = "";
        for (int i = 0; i < output[0].size(); i++) {
            if (TAG_SENTIMENT_POSITIVE.contains(output[0].get(i).toString())) {
                if (posOpinion.isEmpty()) {
                    posOpinion = input.get(i).toString();
                } else {
                    posOpinion = posOpinion + " " + input.get(i).toString();
                }
            } else {
                if (!posOpinion.isEmpty()) {
                    posOpinions.add(posOpinion);
                    posOpinion = "";
                }
            }
        }

        if (!posOpinion.isEmpty()) {
            posOpinions.add(posOpinion);
        }
        
        return posOpinions;
    }
    
    /**
     * find all negative opinions in this output
     * @param input sequence of input (word, postag)
     * @param output sequence of output (label)
     * @return negative opinions
     */
    public static ArrayList<String> findAllNegativeOpinion(Sequence input, Sequence[] output) {
        ArrayList<String> negOpinions = new ArrayList<>();
        String negOpinion = "";
        for (int i = 0; i < output[0].size(); i++) {
            if (TAG_SENTIMENT_NEGATIVE.contains(output[0].get(i).toString())) {
                if (negOpinion.isEmpty()) {
                    negOpinion = input.get(i).toString();
                } else {
                    negOpinion = negOpinion + " " + input.get(i).toString();
                }
            } else {
                if (!negOpinion.isEmpty()) {
                    negOpinions.add(negOpinion);
                    negOpinion = "";
                }
            }
        }

        if (!negOpinion.isEmpty()) {
            negOpinions.add(negOpinion);
        }
        
        return negOpinions;
    }
  
}

