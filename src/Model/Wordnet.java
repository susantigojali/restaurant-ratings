package Model;

import edu.cmu.lti.jawjaw.pobj.POS;
import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.cmu.lti.lexical_db.NictWordNet;
import edu.cmu.lti.lexical_db.data.Concept;
import edu.cmu.lti.ws4j.Relatedness;
import edu.cmu.lti.ws4j.RelatednessCalculator;
import edu.cmu.lti.ws4j.impl.HirstStOnge;
import edu.cmu.lti.ws4j.impl.JiangConrath;
import edu.cmu.lti.ws4j.impl.LeacockChodorow;
import edu.cmu.lti.ws4j.impl.Lesk;
import edu.cmu.lti.ws4j.impl.Lin;
import edu.cmu.lti.ws4j.impl.Path;
import edu.cmu.lti.ws4j.impl.Resnik;
import edu.cmu.lti.ws4j.impl.WuPalmer;
import edu.cmu.lti.ws4j.util.WS4JConfiguration;
import java.util.List;

/**
 *
 * @author susanti_2
 */
public class Wordnet {
    private static final ILexicalDatabase db = new NictWordNet();
    private static RelatednessCalculator rc;
        
    /**
     * calculate similarity between two word using Jiang and Conrath <p>
     * min score = 0.0
     * max score = Infinity
     * @param word1 first word
     * @param word2 second word
     * @return similarity
     */
    public static double jcn(String word1, String word2) {
        rc = new JiangConrath(db);
        return calculateSimilarity(word1, word2);
    }
    
    /**
     * calculate similarity between two word using Wu and Palmer <p>
     * min = 0 , max = 1
     * @param word1 first word
     * @param word2 second word
     * @return similarity
     */
    public static double wup(String word1, String word2) {
        rc = new WuPalmer(db);
        return calculateSimilarity(word1, word2);
    }
    
    /**
     * calculate similarity between two word using Lin <p>
     * min = 0 , max = 1
     * @param word1 first word
     * @param word2 second word
     * @return similarity
     */
    public static double lin(String word1, String word2) {
        rc = new Lin(db);
        return calculateSimilarity(word1, word2);
    }
    
    /**
     * calculate similarity between two word using Path <p>
     * min = 0 , max = 1
     * @param word1 first word
     * @param word2 second word
     * @return similarity
     */
    public static double path(String word1, String word2) {
        rc = new Path(db);
        return calculateSimilarity(word1, word2);
    }
    
    /**
     * calculate similarity between two word using Resnik <p>
     * min = 0 , max = infinity
     * @param word1 first word
     * @param word2 second word
     * @return similarity
     */
    public static double res(String word1, String word2) {
        rc = new Resnik(db);
        return calculateSimilarity(word1, word2);
    }
    
    /**
     * calculate similarity between two word using Leacock Chodorow <p>
     * min = 0 , max = infinity
     * @param word1 first word
     * @param word2 second word
     * @return similarity
     */
    public static double lch(String word1, String word2) {
        rc = new LeacockChodorow(db);
        return calculateSimilarity(word1, word2);
    }
    
    /**
     * calculate similarity between two word using Lesk <p>
     * min = 0 , max = infinity
     * @param word1 first word
     * @param word2 second word
     * @return similarity
     */
    public static double lesk(String word1, String word2) {
        rc = new Lesk(db);
        return calculateSimilarity(word1, word2);
    }
    
    /**
     * calculate similarity between two word using HirstStOnge <p>
     * min = 0 , max = 16
     * @param word1 first word
     * @param word2 second word
     * @return similarity
     */
    public static double hso(String word1, String word2) {
        rc = new HirstStOnge(db);
        return calculateSimilarity(word1, word2);
    }
    
    private static double calculateSimilarity(String word1, String word2) {
        WS4JConfiguration.getInstance().setMFS(true);
        List<POS[]> posPairs = rc.getPOSPairs();
        double maxScore = -1D;
        
        for(POS[] posPair: posPairs) {
            System.out.println(posPair[0].toString() + " "+ posPair[1].toString());
            List<Concept> synsets1 = (List<Concept>)db.getAllConcepts(word1, posPair[0].toString());
            List<Concept> synsets2 = (List<Concept>)db.getAllConcepts(word2, posPair[1].toString());

            for(Concept synset1: synsets1) {
                for (Concept synset2: synsets2) {
                    Relatedness relatedness = rc.calcRelatednessOfSynset(synset1, synset2);
                    double score = relatedness.getScore();
                    System.out.println(synset1.getSynset()+ " "+ synset2.getSynset()+ " "+score);
                    if (score > maxScore) { 
                        maxScore = score;
                    }
                }
            }
        }

        if (maxScore == -1D) {
            maxScore = 0.0;
        }
        return maxScore;
    }
    

    public static void main (String args[]) {
        System.out.println(jcn("food", "menu"));
    }
}
