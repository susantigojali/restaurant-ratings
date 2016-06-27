package DataProcessing;

import Learning.MyCRFSimpleTagger;
import Model.AspectSentiment;
import Model.Feature;
import Model.Reader;
import Model.SequenceTagging;
import java.io.FileNotFoundException;
import java.io.IOException;
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
    private static final ArrayList<String> TAG_SENTIMENT = new ArrayList<>(Arrays.asList("OP_POS-B", "OP_POS-I", "OP_NEG-B", "OP_NEG-I"));
    private static final ArrayList<String> TAG_SENTIMENT_POSITIVE = new ArrayList<>(Arrays.asList("OP_POS-B", "OP_POS-I"));
    private static final ArrayList<String> TAG_SENTIMENT_POSITIVE_INSIDE = new ArrayList<>(Arrays.asList("OP_POS-I"));
    private static final ArrayList<String> TAG_SENTIMENT_NEGATIVE = new ArrayList<>(Arrays.asList("OP_NEG-B", "OP_NEG-I"));
    private static final ArrayList<String> TAG_SENTIMENT_NEGATIVE_INSIDE = new ArrayList<>(Arrays.asList("OP_NEG-I"));

    private static final ArrayList<String> ASPECT = new ArrayList<>(Arrays.asList("aspect"));
    private static final ArrayList<String> SENTIMENT = new ArrayList<>(Arrays.asList("op_pos", "op_neg"));
    private static final ArrayList<String> POS_SENTIMENT = new ArrayList<>(Arrays.asList("op_pos"));
    private static final ArrayList<String> NEG_SENTIMENT = new ArrayList<>(Arrays.asList("op_neg"));
    private static final ArrayList<String> CONJUCTION = new ArrayList<>(Arrays.asList("dan", "tapi", ",", "hanya", "namun"));

    /**
     * find <aspect, sentiment> from the data
     *
     * @param data data
     * @return list of aspect sentiment
     */
    public static ArrayList<AspectSentiment> findAspectSentiment(SequenceTagging data) {
        ArrayList<String> output = data.getOutput();
        ArrayList<Feature> input = data.getSequenceInput();

        ArrayList<String> aspects = findAllAspect(input, output);
        ArrayList<String> posOpinions = findAllPositiveOpinion(input, output);
        ArrayList<String> negOpinions = findAllNegativeOpinion(input, output);
//        System.out.println("ASPECT=============");
//        for (int i = 0; i < aspects.size(); i++) {
//            System.out.println(i + " " + aspects.get(i));
//        }
//        System.out.println("OP_POS=============");
//        for (int i = 0; i < posOpinions.size(); i++) {
//            System.out.println(i + " " + posOpinions.get(i));
//        }
//        System.out.println("OP_NEG============");
//        for (int i = 0; i < negOpinions.size(); i++) {
//            System.out.println(i + " " + negOpinions.get(i));
//        }
//        System.out.println();

        /// Aspect Sentiment from rule
        HashSet<AspectSentiment> aspectSentiments = getAS(input, output);
        return new ArrayList<>(aspectSentiments);
//        System.exit(-1);

        // Aspect Sentiment [sentiment after aspect]
        // jika ga ada aspek sama sekali
//        if (aspects.isEmpty()) { //step 1
//            return new ArrayList<>();
//        }
//        
//        //jika cuma ada 1 aspek dalam 1 kalimat
//        ArrayList<AspectSentiment> as = new ArrayList<>();
//                
//        if (aspects.size() == 1) { //step 2
//            //find all aspect
//            String aspect = aspects.get(0);
//            for (String opPos : posOpinions) {
//                //find index opinion
//                String[] temp = opPos.split(" ");
//                String opTemp;
//                if (temp.length != 1) {
//                    opTemp = temp[0];
//                } else {
//                    opTemp = opPos;
//                }
//                
//                int indexOpinion = 0;
//                boolean found = false;
//                for (int i = 0; i < input.size() &&!found; i++) {
//                    if (input.get(i).getWord().compareToIgnoreCase(opTemp) == 0) {
//                        indexOpinion = i;
//                        found = true;
//                    }
//                }
//                String negation = getOrientationChange(input, output, indexOpinion);
//                if (negation != null) {
//                    opPos = negation + " " + opPos;
//                    as.add(new AspectSentiment(aspect, opPos, NEGATIVE));
//                } else {
//                    as.add(new AspectSentiment(aspect, opPos, POSITIVE));
//                }
//            }
//            
//            for (String negPos : negOpinions) {
//                //find index opinion
//                String[] temp = negPos.split(" ");
//                String opTemp;
//                if (temp.length != 1) {
//                    opTemp = temp[0];
//                } else {
//                    opTemp = negPos;
//                }
//                
//                int indexOpinion = 0;
//                boolean found = false;
//                for (int i = 0; i < input.size() &&!found; i++) {
//                    if (input.get(i).getWord().compareToIgnoreCase(opTemp) == 0) {
//                        indexOpinion = i;
//                        found = true;
//                    }
//                }
//                String negation = getOrientationChange(input, output, indexOpinion);
//                if (negation != null) {
//                    negPos = negation + " " + negPos;
//                    as.add(new AspectSentiment(aspect, negPos, POSITIVE));
//                } else {
//                    as.add(new AspectSentiment(aspect, negPos, NEGATIVE));
//                }
//            }
//            return as;
//        }      
//        
//        //jika ada banyak aspek dan sentimen //step 3
//        
//        //find sentiment for each aspect
//        HashSet<AspectSentiment> aspectSentiment = getSentimentFromAspect(input, output);
//        
//        //find aspect for each sentiment
//        HashSet<AspectSentiment> aspectPosSentiment = getAspectFromPosSentiment(input, output);
//        aspectSentiment.addAll(aspectPosSentiment);
//        
//        HashSet<AspectSentiment> aspectNegSentiment = getAspectFromNegSentiment(input, output);
//        aspectSentiment.addAll(aspectNegSentiment);
//        
//        return new ArrayList<>(aspectSentiment);
    }

    /**
     * find ASPECT from SENTIMENT
     *
     * @param input sequence of input (word, postag)
     * @param output sequence of output (label)
     * @return list of aspect sentiment
     */
    private static HashSet<AspectSentiment> getAS(ArrayList<Feature> input, ArrayList<String> output) {
        ArrayList<String> words = new ArrayList<>();
        ArrayList<String> tags = new ArrayList<>(); //aspect, op_pos, op_neg, conjuction, comma
        String token;

        int i = 0;
        while (i < output.size()) {
            if (TAG_ASPECT.contains(output.get(i))) {
                token = input.get(i).getWord();
                i++;
                boolean found = true;
                while (i < output.size() && found) {
                    if (TAG_ASPECT.contains(output.get(i))) {
                        token = token + " " + input.get(i).getWord();
                        i++;
                    } else {
                        found = false;
                    }
                }
                words.add(token);
                tags.add("aspect");
                i--;
            }
            if (TAG_SENTIMENT_POSITIVE.contains(output.get(i))) {
                token = input.get(i).getWord();
                String negation = getOrientationChange(input, output, i);
                if (negation != null) {
                    token = negation + " " + token;
                    tags.add("op_neg");
                } else {
                    tags.add("op_pos");
                }
                i++;
                boolean found = true;
                while (i < output.size() && found) {
                    if (TAG_SENTIMENT_POSITIVE_INSIDE.contains(output.get(i).toString())) {
                        token = token + " " + input.get(i).getWord();
                        i++;
                    } else {
                        found = false;
                    }
                }
                words.add(token);
                i--;
            }
            if (TAG_SENTIMENT_NEGATIVE.contains(output.get(i))) {
                token = input.get(i).getWord();
                String negation = getOrientationChange(input, output, i);
                if (negation != null) {
                    token = negation + " " + token;
                    tags.add("op_pos");
                } else {
                    tags.add("op_neg");
                }
                i++;
                boolean found = true;
                while (i < output.size() && found) {
                    if (TAG_SENTIMENT_NEGATIVE_INSIDE.contains(output.get(i))) {
                        token = token + " " + input.get(i).getWord();
                        i++;
                    } else {
                        found = false;
                    }
                }
                words.add(token);
                i--;
            }
            if (CONJUCTION.contains(input.get(i).getWord())) {
                token = input.get(i).getWord();
                if (i + 1 < output.size()) {
                    if (token.compareTo(",") == 0 && input.get(i + 1).getWord().compareTo("dan") == 0) {
                        i++;
                    }
                }
                words.add(token);
                tags.add("conjuction");
            }
            i++;
        }
         //for debug       
//        System.out.println("++++++++++++");
//        for (int j = 0; j < words.size(); j++) {
//            System.out.println(words.get(j) +"      "+ tags.get(j));
//        }
//        System.out.println("++++++++++++");

        HashSet<AspectSentiment> aspectSentiments = new HashSet<>();

        ArrayList<String> aspects = new ArrayList<>();
        ArrayList<String> posOpinions = new ArrayList<>();
        ArrayList<String> negOpinions = new ArrayList<>();

        int j = 0;
        while (j < tags.size()) {
            if (!aspects.isEmpty() && (!posOpinions.isEmpty() || !negOpinions.isEmpty())) { //udah ada aspek dan sentimen sebelumnya
                //save aspek dan sentimen
                for (String aspect : aspects) {
                    for (String posOpinion : posOpinions) {
                        aspectSentiments.add(new AspectSentiment(aspect, posOpinion, 1));
                    }
                    for (String negOpinion : negOpinions) {
                        aspectSentiments.add(new AspectSentiment(aspect, negOpinion, -1));
                    }
                }
                aspects = new ArrayList<>();
                posOpinions = new ArrayList<>();
                negOpinions = new ArrayList<>();
//                System.out.println("save !!");
            }

            if (ASPECT.contains(tags.get(j))) {
                //simpen aspek yg srkg
//                System.out.println("get aspek... = " + words.get(j));
                aspects = new ArrayList<>();
                aspects.add(words.get(j));

                //find another aspect
                int k = j + 1;
                boolean found = true;
                while (k < tags.size() - 1 && found) {
                    if (CONJUCTION.contains(words.get(k)) && ASPECT.contains(tags.get(k + 1))) {
                        //System.out.println("get aspek~... = " + words.get(k+1));
                        aspects.add(words.get(k + 1));
                        k += 2;
                    } else {
                        found = false;
                    }
                }
                j = k - 1;
            } else if (SENTIMENT.contains(tags.get(j))) {
//                System.out.println("get sentimen "+words.get(j));
                //buang aspek yg ada, simpen aspek yg srkg
                posOpinions = new ArrayList<>();
                negOpinions = new ArrayList<>();
                if (POS_SENTIMENT.contains(tags.get(j))) {
//                    System.out.println("get pos... = " + words.get(j));
                    posOpinions.add(words.get(j));
                } else if (NEG_SENTIMENT.contains(tags.get(j))) {
//                    System.out.println("get neg... = " + words.get(j));
                    negOpinions.add(words.get(j));
                } else {
                    System.out.println("ERROR!! " + tags.get(j));
                    System.exit(-1);
                }

                //find another sentimen
                int k = j + 1;
                boolean found = true;
                while (k < tags.size() - 1 && found) {
                    if (CONJUCTION.contains(words.get(k)) && SENTIMENT.contains(tags.get(k + 1))) {
                        if (POS_SENTIMENT.contains(tags.get(k + 1))) {
//                            System.out.println("get pos~... = " + words.get(k+1));
                            posOpinions.add(words.get(k + 1));
                        } else if (NEG_SENTIMENT.contains(tags.get(k + 1))) {
//                            System.out.println("get neg~... = " + words.get(k+1));
                            negOpinions.add(words.get(k + 1));
                        } else {
                            System.out.println("ERROR!! " + tags.get(k + 1));
                            System.exit(-1);
                        }
                        k += 2;
                    } else {
                        found = false;
                    }
                }
                j = k - 1;
            } else {
                aspects = new ArrayList<>();
                posOpinions = new ArrayList<>();
                negOpinions = new ArrayList<>();
            }
            j++;
        }

        if (!aspects.isEmpty() && (!posOpinions.isEmpty() || !negOpinions.isEmpty())) { //di akhir
            //save aspek dan sentimen
            for (String aspect : aspects) {
                for (String posOpinion : posOpinions) {
                    aspectSentiments.add(new AspectSentiment(aspect, posOpinion, 1));
                }
                for (String negOpinion : negOpinions) {
                    aspectSentiments.add(new AspectSentiment(aspect, negOpinion, -1));
                }
            }
        }
        //for debug
//        System.out.println("````````````");
//        for (AspectSentiment s : aspectSentiments) {
//            System.out.println(s.getAspect() + " >> "+ s.getSentiment());
//        }
//        System.out.println("````````````");
        return aspectSentiments;

    }

    public static void main(String args[]) throws IOException, FileNotFoundException, ClassNotFoundException {
        //Preprocess CRF 
        String rawCrfFilename = "datatest/CRF/rawtest";//.txt";
        String dataCRF = "datatest/CRF/test";//.txt";

        Boolean includeInput = true;
        int nBestOption = 1;
        String CRF_MODEL = "datatest/Model/crf1 (298).model";

        Preprocess.preprocessDataforSequenceTagging(rawCrfFilename + ".txt", dataCRF + ".txt", false);

        //Klasifikasi CRF
        ArrayList<SequenceTagging> inputSequence = Reader.readSequenceInput(dataCRF + ".txt");
        ArrayList<SequenceTagging> outputs = MyCRFSimpleTagger.myClassify(dataCRF + ".txt", CRF_MODEL, includeInput, nBestOption, inputSequence);

        //Extract Aspect and Opinion
        ArrayList<AspectSentiment> as = new ArrayList<>();
        for (SequenceTagging output : outputs) {
            Postprocess.findAspectSentiment(output);
        }
    }

    /**
     * find sentiment from aspect
     *
     * @param input sequence of input (word, postag)
     * @param output sequence of output (label)
     * @return list of aspect sentiment
     */
    private static HashSet<AspectSentiment> getSentimentFromAspect(ArrayList<Feature> input, ArrayList<String> output) {
        String aspect = "";
        String posOpinion = "";
        String negOpinion = "";
        int indexOpinion = 0;

        HashSet<AspectSentiment> aspectSentiment = new HashSet<>();

        for (int i = 0; i < output.size(); i++) {
            if (TAG_ASPECT.contains(output.get(i))) {
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
                    boolean conj = false;
                    while (!found && j < output.size()) {
                        if (TAG_SENTIMENT_POSITIVE.contains(output.get(j))) {
                            if (!negOpinion.isEmpty()) {
                                String negation = getOrientationChange(input, output, indexOpinion);
                                if (negation != null) {
                                    negOpinion = negation + " " + negOpinion;
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
                        } else if (TAG_SENTIMENT_NEGATIVE.contains(output.get(j))) {
                            if (!posOpinion.isEmpty()) {
                                String negation = getOrientationChange(input, output, indexOpinion);
                                if (negation != null) {
                                    posOpinion = negation + " " + posOpinion;
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
                                String negation = getOrientationChange(input, output, indexOpinion);
                                if (negation != null) {
                                    posOpinion = negation + " " + posOpinion;
                                    aspectSentiment.add(new AspectSentiment(aspect, posOpinion, NEGATIVE));
                                } else {
                                    aspectSentiment.add(new AspectSentiment(aspect, posOpinion, POSITIVE));
                                }
                                found = true;
                            } else if (!negOpinion.isEmpty()) {
                                assert posOpinion.isEmpty(); //harus ga ada pos opinion
                                String negation = getOrientationChange(input, output, indexOpinion);
                                if (negation != null) {
                                    negOpinion = negation + " " + negOpinion;
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
                            String negation = getOrientationChange(input, output, indexOpinion);
                            if (negation != null) {
                                posOpinion = negation + " " + posOpinion;
                                aspectSentiment.add(new AspectSentiment(aspect, posOpinion, NEGATIVE));
                            } else {
                                aspectSentiment.add(new AspectSentiment(aspect, posOpinion, POSITIVE));
                            }
                        } else if (!negOpinion.isEmpty()) {
                            assert posOpinion.isEmpty(); //harus ga ada pos opinion
                            String negation = getOrientationChange(input, output, indexOpinion);
                            if (negation != null) {
                                negOpinion = negation + " " + negOpinion;
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
     *
     * @param input sequence of input (word, postag)
     * @param output sequence of output (label)
     * @return list of aspect sentiment
     */
    private static HashSet<AspectSentiment> getAspectFromPosSentiment(ArrayList<Feature> input, ArrayList<String> output) {
        int indexOpinion = 0;
        String posOpinion = "";

        HashSet<AspectSentiment> aspectSentiment = new HashSet<>();

        for (int i = 0; i < output.size(); i++) {
            if (TAG_SENTIMENT_POSITIVE.contains(output.get(i).toString())) {
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

                    System.out.println("change: " + getOrientationChange(input, output, indexOpinion));
                    String negation = getOrientationChange(input, output, indexOpinion);
                    if (negation != null) {
                        posOpinion = negation + " " + posOpinion;
                        aspectSentiment.add(new AspectSentiment(aspectOpinion, posOpinion, NEGATIVE));
                    } else {
                        aspectSentiment.add(new AspectSentiment(aspectOpinion, posOpinion, POSITIVE));
                    }
                    posOpinion = "";
                }
            }
        }
        if (!posOpinion.isEmpty()) {
            String aspectOpinion = findAspect(input, output, output.size());

            System.out.println("change: " + getOrientationChange(input, output, indexOpinion));
            String negation = getOrientationChange(input, output, indexOpinion);
            if (negation != null) {
                posOpinion = negation + " " + posOpinion;
                aspectSentiment.add(new AspectSentiment(aspectOpinion, posOpinion, NEGATIVE));
            } else {
                aspectSentiment.add(new AspectSentiment(aspectOpinion, posOpinion, POSITIVE));
            }
        }

        return aspectSentiment;
    }

    /**
     * find aspect in output from negative opinions
     *
     * @param input sequence of input (word, postag)
     * @param output sequence of output (label)
     * @return list of aspect sentiment
     */
    private static HashSet<AspectSentiment> getAspectFromNegSentiment(ArrayList<Feature> input, ArrayList<String> output) {
        int indexOpinion = 0;
        String negOpinion = "";

        HashSet<AspectSentiment> aspectSentiment = new HashSet<>();

        for (int i = 0; i < output.size(); i++) {
            if (TAG_SENTIMENT_NEGATIVE.contains(output.get(i).toString())) {
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

                    System.out.println("change: " + getOrientationChange(input, output, indexOpinion));
                    String negation = getOrientationChange(input, output, indexOpinion);
                    if (negation != null) {
                        negOpinion = negation + " " + negOpinion;
                        aspectSentiment.add(new AspectSentiment(aspectOpinion, negOpinion, POSITIVE));
                    } else {
                        aspectSentiment.add(new AspectSentiment(aspectOpinion, negOpinion, NEGATIVE));
                    }
                    negOpinion = "";
                }
            }
        }
        if (!negOpinion.isEmpty()) {
            String aspectOpinion = findAspect(input, output, output.size());
            System.out.println("change: " + getOrientationChange(input, output, indexOpinion));
            String negation = getOrientationChange(input, output, indexOpinion);
            if (negation != null) {
                negOpinion = negation + " " + negOpinion;
                aspectSentiment.add(new AspectSentiment(aspectOpinion, negOpinion, POSITIVE));
            } else {
                aspectSentiment.add(new AspectSentiment(aspectOpinion, negOpinion, NEGATIVE));
            }
        }
        return aspectSentiment;
    }

    /**
     * find aspect in output before index i
     *
     * @param input sequence of input (word, postag)
     * @param output sequence of output (label)
     * @return aspect
     */
    private static String findAspect(ArrayList<Feature> input, ArrayList<String> output, int i) {
        boolean found = false;
        String aspect = "";
        int j = i - 1;
        while (!found && j >= 0) {
            if (TAG_ASPECT.contains(output.get(j))) {
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
     *
     * @param input sequence of input (word, postag)
     * @param output sequence of output (label)
     * @return aspects
     */
    public static ArrayList<String> findAllAspect(ArrayList<Feature> input, ArrayList<String> output) {
        ArrayList<String> aspects = new ArrayList<>();
        String aspect = "";
        for (int i = 0; i < output.size(); i++) {
            if (TAG_ASPECT.contains(output.get(i).toString())) {
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
     *
     * @param input sequence of input (word, postag)
     * @param output sequence of output (label)
     * @return positive opinions
     */
    public static ArrayList<String> findAllPositiveOpinion(ArrayList<Feature> input, ArrayList<String> output) {
        ArrayList<String> posOpinions = new ArrayList<>();
        String posOpinion = "";
        for (int i = 0; i < output.size(); i++) {
            if (TAG_SENTIMENT_POSITIVE.contains(output.get(i).toString())) {
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
     *
     * @param input sequence of input (word, postag)
     * @param output sequence of output (label)
     * @return negative opinions
     */
    public static ArrayList<String> findAllNegativeOpinion(ArrayList<Feature> input, ArrayList<String> output) {
        ArrayList<String> negOpinions = new ArrayList<>();
        String negOpinion = "";
        for (int i = 0; i < output.size(); i++) {
            if (TAG_SENTIMENT_NEGATIVE.contains(output.get(i).toString())) {
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
     *
     * @param input sequence of input (word, postag)
     * @param output sequence of output (label)
     * @param index index of the first opinion
     * @return true if there is a negative word before the index of opinion
     */
    public static String getOrientationChange(ArrayList<Feature> input, ArrayList<String> output, int index) {
        ArrayList<String> negDict = Dictionary.getNegationWordsDict();
        ArrayList<String> ccDict = Dictionary.getCoordinatingConjuctionsDict();

        boolean found = false;
        boolean change = false;
        String negation = null;

        for (int i = 1; i < SEARCH_NEGATION_AREA && (index - i) >= 0 && !found; i++) {
            if (ccDict.contains(input.get(index - i).getWord())) {
                found = true;
            } else if (TAG_ASPECT.contains(output.get(index - i))) {
                found = true;
            } else if (TAG_SENTIMENT.contains(output.get(index - i))) {
                found = true;
            } else if (output.get(index - i).compareTo(",") == 0) {
                found = true;
            } else if (negDict.contains(input.get(index - i).getWord())) {
                found = true;
                change = true;
                negation = input.get(index - i).getWord();
            }
        }

        return negation;
    }
}