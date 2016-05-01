package DataProcessing;

import Model.AspectSentiment;
import Model.Feature;
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
    
    private static final int SEARCH_NEGATION_AREA = 5;
    private static final int POSITIVE = 1;
    private static final int NEGATIVE = -1;
    private static final int NETRAL = 0;
    
    private static final ArrayList<String> TAG_ASPECT = new ArrayList<>(Arrays.asList("ASPECT-B", "ASPECT-I"));
    private static final ArrayList<String> TAG_SENTIMENT_POSITIVE = new ArrayList<>(Arrays.asList("OP_POS-B", "OP_POS-I"));
    private static final ArrayList<String> TAG_SENTIMENT_NEGATIVE = new ArrayList<>(Arrays.asList("OP_NEG-B", "OP_NEG-I"));

    /**
     * find <aspect, sentiment> from the data
     * @param data data
     * @return list of aspect sentiment
     */
    public static ArrayList<AspectSentiment> findAspectSentiment(SequenceTagging data) {
        assert data.getOutput().length == 1;
        
        Sequence[] output = data.getOutput();
        ArrayList<Feature> input = data.getSequenceInput();

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
        
        //find sentiment for each aspect
        HashSet<AspectSentiment> aspectSentiment = getSentimentFromAspect(input, output);
        
        //find aspect for each sentiment
        HashSet<AspectSentiment> aspectPosSentiment = getAspectFromPosSentiment(input, output);
        aspectSentiment.addAll(aspectPosSentiment);
        
        HashSet<AspectSentiment> aspectNegSentiment = getAspectFromNegSentiment(input, output);
        aspectSentiment.addAll(aspectNegSentiment);
        
        return new ArrayList<>(aspectSentiment);
    }
    
    /**
     * find sentiment from aspect
     * @param input sequence of input (word, postag)
     * @param output sequence of output (label)
     * @return list of aspect sentiment
     */
    private static HashSet<AspectSentiment> getSentimentFromAspect(ArrayList<Feature> input, Sequence[] output) {
        String aspect = "";
        String posOpinion = "";
        String negOpinion = "";
        int indexOpinion = 0;
        
        HashSet<AspectSentiment> aspectSentiment = new HashSet<>();
        
        for (int i = 0; i < output[0].size(); i++) {
            if (TAG_ASPECT.contains(output[0].get(i).toString())) {
                if (aspect.isEmpty()) {
                    aspect = input.get(i).getWord();
                } else {
                    aspect = aspect + " " + input.get(i).getWord();
                }
            } else {
                if (!aspect.isEmpty()) {
                    //find opinion after this aspect
                    boolean found = false;
                    int j = i;
                    while (!found && j < output[0].size()) {
                        if (TAG_SENTIMENT_POSITIVE.contains(output[0].get(j).toString())) {
                            if (!negOpinion.isEmpty()) {
                                if (isOrientationChange(input, output, indexOpinion)) {
                                    aspectSentiment.add(new AspectSentiment(aspect, negOpinion, POSITIVE));
                                } else {
                                    aspectSentiment.add(new AspectSentiment(aspect, negOpinion, NEGATIVE));
                                }
                                found = true;
                            } else {
                                if (posOpinion.isEmpty()) {
                                    indexOpinion = j;
                                    posOpinion = input.get(j).getWord();
                                } else {
                                    posOpinion = posOpinion + " " + input.get(j).getWord();
                                }
                            }
                        } else if (TAG_SENTIMENT_NEGATIVE.contains(output[0].get(j).toString())) {
                            if (!posOpinion.isEmpty()) {
                                if (isOrientationChange(input, output, indexOpinion)) {
                                    aspectSentiment.add(new AspectSentiment(aspect, posOpinion, NEGATIVE));
                                } else {
                                    aspectSentiment.add(new AspectSentiment(aspect, posOpinion, POSITIVE));
                                }
                                found = true;
                            } else {
                                if (negOpinion.isEmpty()) {
                                    indexOpinion = j;
                                    negOpinion = input.get(j).getWord();
                                } else {
                                    negOpinion = negOpinion + " " + input.get(j).getWord();
                                }
                            }
                        } else { //other or aspect
                            if (!posOpinion.isEmpty()) {
                                assert negOpinion.isEmpty(); //harus ga ada neg opinion
                                if (isOrientationChange(input, output, indexOpinion)) {
                                    aspectSentiment.add(new AspectSentiment(aspect, posOpinion, NEGATIVE));
                                } else {
                                    aspectSentiment.add(new AspectSentiment(aspect, posOpinion, POSITIVE));
                                }
                                found = true;
                            } else if (!negOpinion.isEmpty()) {
                                assert posOpinion.isEmpty(); //harus ga ada pos opinion
                                if (isOrientationChange(input, output, indexOpinion)) {
                                    aspectSentiment.add(new AspectSentiment(aspect, negOpinion, POSITIVE));
                                } else {
                                    aspectSentiment.add(new AspectSentiment(aspect, negOpinion, NEGATIVE));
                                }
                                found = true;
                            }
                        }
                        j++;
                    }

                    //ga ketemu sentimen apa-apa untuk aspek ini atau sentimennya di akhir kalimat
                    if (!found) {
                        if (!posOpinion.isEmpty()) {
                            assert negOpinion.isEmpty(); //harus ga ada neg opinion
                            if (isOrientationChange(input, output, indexOpinion)) {
                                aspectSentiment.add(new AspectSentiment(aspect, posOpinion, NEGATIVE));
                            } else {
                                aspectSentiment.add(new AspectSentiment(aspect, posOpinion, POSITIVE));
                            }
                        } else if (!negOpinion.isEmpty()) {
                            assert posOpinion.isEmpty(); //harus ga ada pos opinion
                            if (isOrientationChange(input, output, indexOpinion)) {
                                aspectSentiment.add(new AspectSentiment(aspect, negOpinion, POSITIVE));
                            } else {
                                aspectSentiment.add(new AspectSentiment(aspect, negOpinion, NEGATIVE));
                            }
                        } else {
                            aspectSentiment.add(new AspectSentiment(aspect, "", NETRAL));
                        }
                    }
                    aspect = "";
                    posOpinion = "";
                    negOpinion = "";
                }
            }
        }
        if (!aspect.isEmpty()) { //aspek di akhir kalimat
            aspectSentiment.add(new AspectSentiment(aspect, "", NETRAL));
            aspect = "";
        }
        
        return aspectSentiment;
    }
    
    /**
     * find aspect in output from positive opinions
     * @param input sequence of input (word, postag)
     * @param output sequence of output (label)
     * @return list of aspect sentiment 
     */
    private static HashSet<AspectSentiment> getAspectFromPosSentiment(ArrayList<Feature> input, Sequence[] output) {
        int indexOpinion = 0;
        String posOpinion = "";
        
        HashSet<AspectSentiment> aspectSentiment = new HashSet<>();
        
        for (int i = 0; i < output[0].size(); i++) {
            if (TAG_SENTIMENT_POSITIVE.contains(output[0].get(i).toString())) {
                if (posOpinion.isEmpty()) {
                    indexOpinion = i;
                    posOpinion = input.get(i).getWord();
                } else {
                    posOpinion = posOpinion + " " + input.get(i).getWord();
                }
            } else {
                if (!posOpinion.isEmpty()) {
                    //find aspect before this opinion
                    String aspectOpinion = findAspect(input, output, i);
                    
                    System.out.println("change: "+isOrientationChange(input, output, indexOpinion));
                    if (isOrientationChange(input, output, indexOpinion)) {
                        aspectSentiment.add(new AspectSentiment(aspectOpinion, posOpinion, NEGATIVE));
                    } else {
                        aspectSentiment.add(new AspectSentiment(aspectOpinion, posOpinion, POSITIVE));
                    }
                    posOpinion = "";
                }
            }
        }
        if (!posOpinion.isEmpty()) {
            String aspectOpinion = findAspect(input, output, output[0].size());
            
            System.out.println("change: "+isOrientationChange(input, output, indexOpinion));
            if (isOrientationChange(input, output, indexOpinion)) {
                aspectSentiment.add(new AspectSentiment(aspectOpinion, posOpinion, NEGATIVE));
            } else {
                aspectSentiment.add(new AspectSentiment(aspectOpinion, posOpinion, POSITIVE));
            }
        }
        
        return aspectSentiment;
    }
    
    /**
     * find aspect in output from negative opinions
     * @param input sequence of input (word, postag)
     * @param output sequence of output (label)
     * @return list of aspect sentiment 
     */
    private static HashSet<AspectSentiment> getAspectFromNegSentiment(ArrayList<Feature> input, Sequence[] output) {
        int indexOpinion = 0;
        String negOpinion = "";
        
        HashSet<AspectSentiment> aspectSentiment = new HashSet<>();
        
        for (int i = 0; i < output[0].size(); i++) {
            if (TAG_SENTIMENT_NEGATIVE.contains(output[0].get(i).toString())) {
                if (negOpinion.isEmpty()) {
                    indexOpinion = i;
                    negOpinion = input.get(i).getWord();
                } else {
                    negOpinion = negOpinion + " " + input.get(i).getWord();
                }
            } else {
                if (!negOpinion.isEmpty()) {
                    //find aspect before this opinion
                    String aspectOpinion = findAspect(input, output, i);
                    
                    System.out.println("change: "+isOrientationChange(input, output, indexOpinion));
                    if (isOrientationChange(input, output, indexOpinion)) {
                        aspectSentiment.add(new AspectSentiment(aspectOpinion, negOpinion, POSITIVE));
                    } else {
                        aspectSentiment.add(new AspectSentiment(aspectOpinion, negOpinion, NEGATIVE));
                    }
                    negOpinion = "";
                }
            }
        }
        if (!negOpinion.isEmpty()) {
            String aspectOpinion = findAspect(input, output, output[0].size());
                    System.out.println("change: "+isOrientationChange(input, output, indexOpinion));
            if (isOrientationChange(input, output, indexOpinion)) {
                aspectSentiment.add(new AspectSentiment(aspectOpinion, negOpinion, POSITIVE));
            } else {
                aspectSentiment.add(new AspectSentiment(aspectOpinion, negOpinion, NEGATIVE));
            }
        }
        return aspectSentiment;
    }
    
    /**
     * find aspect in output before index i
     * @param input sequence of input (word, postag)
     * @param output sequence of output (label)
     * @return aspect 
     */
    private static String findAspect(ArrayList<Feature> input, Sequence[] output, int i) {
        boolean found = false;
        String aspect = "";
        int j = i - 1;
        while (!found && j >= 0) {
            if (TAG_ASPECT.contains(output[0].get(j).toString())) {
                if (aspect.isEmpty()) {
                    aspect = input.get(j).getWord();
                } else {
                    aspect = input.get(j).getWord() + " " + aspect;
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
    public static ArrayList<String> findAllAspect(ArrayList<Feature> input, Sequence[] output) {
        ArrayList<String> aspects = new ArrayList<>();
        String aspect = "";
        for (int i = 0; i < output[0].size(); i++) {
            if (TAG_ASPECT.contains(output[0].get(i).toString())) {
                 if (aspect.isEmpty()) {
                    aspect = input.get(i).getWord();
                } else {
                    aspect = aspect + " " + input.get(i).getWord();
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
    public static ArrayList<String> findAllPositiveOpinion(ArrayList<Feature> input, Sequence[] output) {
        ArrayList<String> posOpinions = new ArrayList<>();
        String posOpinion = "";
        for (int i = 0; i < output[0].size(); i++) {
            if (TAG_SENTIMENT_POSITIVE.contains(output[0].get(i).toString())) {
                if (posOpinion.isEmpty()) {
                    posOpinion = input.get(i).getWord();
                } else {
                    posOpinion = posOpinion + " " + input.get(i).getWord();
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
    public static ArrayList<String> findAllNegativeOpinion(ArrayList<Feature> input, Sequence[] output) {
        ArrayList<String> negOpinions = new ArrayList<>();
        String negOpinion = "";
        for (int i = 0; i < output[0].size(); i++) {
            if (TAG_SENTIMENT_NEGATIVE.contains(output[0].get(i).toString())) {
                if (negOpinion.isEmpty()) {
                    negOpinion = input.get(i).getWord();
                } else {
                    negOpinion = negOpinion + " " + input.get(i).getWord();
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
  
    /**
     * return true if there is a negative word before the index of opinion
     * @param input sequence of input (word, postag)
     * @param output sequence of output (label)
     * @param index index of the first opinion
     * @return true if there is a negative word before the index of opinion
     */
    public static boolean isOrientationChange(ArrayList<Feature> input, Sequence[] output, int index) {
        ArrayList<String> negDict = Dictionary.getNegationWordsDict();
        ArrayList<String> ccDict = Dictionary.getCoordinatingConjuctionsDict();
        
        boolean found = false;
        boolean change = false;
        
        for (int i = 1; i < SEARCH_NEGATION_AREA && (index - i) >=0 && !found; i++) {
            if (ccDict.contains(input.get(index-i).getWord())) {
                found = true;
            } else if (TAG_ASPECT.contains(output[0].get(index-i).toString())) {
                found = true;
            } else if (negDict.contains(input.get(index-i).getWord())) {
                found = true;
                change = true;
            }
        }
        return change;
    }
}

