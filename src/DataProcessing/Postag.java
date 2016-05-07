package DataProcessing;

import IndonesianNLP.IndonesianPOSTagger;
import java.util.ArrayList;

/**
 *
 * @author susanti_2
 */
public class Postag {

    /**
     * Header for all postag
     */
    public static final String HEADER = "open_parenthesis;close_parenthesis;slash;"
            + "semicolon;colon;quotation;sentence_terminator;comma;dash;ellipsis;"
            + "adjective;adverb;common_noun;proper_noun;genitive_noun;intransitive_verb;"
            + "transitive_verb;preprosition;modal;coor_conjuction;subor_conjunction;"
            + "determiner;interjection;ordinal_numerals;collective_numerals;"
            + "primary_numerals;irregular_numerals;personal_pronouns;wh_pronouns;"
            + "number_pronouns;locative_pronouns;negation;symbols;particles;foreign_word;"
            + "pronouns;bilangan";

    private static final String OPEN_PARENTHESIS = "(";
    private static final String CLOSE_PARENTHESIS = ")";
    private static final String SLASH = "GM";
    private static final String SEMICOLON = ";";
    private static final String COLON = ":";
    private static final String QUOTATION1 = "\"";
    private static final String QUOTATION2 = "\'";
    private static final String SENTENCE_TERMINATOR = "."; //.?!
    private static final String COMMA = ",";
    private static final String DASH = "-";
    private static final String ELLIPSIS = "...";
    private static final String ADJECTIVE = "JJ";
    private static final String ADVERB = "RB";
    private static final String COMMON_NOUN = "NN";
    private static final String PROPER_NOUN = "NNP";
    private static final String GENITIVE_NOUN = "NNG";
    private static final String INTRANSITIVE_VERB = "VBI";
    private static final String TRANSITIVE_VERB = "VBT";
    private static final String PREPROSITION = "IN";
    private static final String PERSONAL_PRONOUNS = "PRP";
    private static final String MODAL = "MD";
    private static final String COOR_CONJUNCTION = "CC";
    private static final String SUBOR_CONJUNCTION = "SC";
    private static final String DETERMINER = "DT";
    private static final String INTERJECTION = "UH";
    private static final String ORDINAL_NUMBERALS = "CDO";
    private static final String COLLECTIVE_NUMBERALS = "CDC";
    private static final String PRIMARY_NUMBERALS = "CDP";
    private static final String IRREGULAR_NUMBERALS = "CDI";
    private static final String WH_PRONOUNS = "WP";
    private static final String NUMBER_PRONOUNS = "PRN";
    private static final String LOCATIVE_PRONOUNS = "PRL";
    private static final String NEGATION = "NEG";
    private static final String SYMBOLS = "SYM";
    private static final String PARTICLES = "RP";
    private static final String FOREIGN_WORDS = "FW";

    private static final String[] PRONOUNS = {PERSONAL_PRONOUNS, WH_PRONOUNS, NUMBER_PRONOUNS, LOCATIVE_PRONOUNS};
    private static final String[] NUMERALS = {ORDINAL_NUMBERALS, COLLECTIVE_NUMBERALS, PRIMARY_NUMBERALS, IRREGULAR_NUMBERALS};
    private static final String CARDINAL_NUMBER = PRIMARY_NUMBERALS;
    private static final String[] PUNCTUATION = {COMMA, SENTENCE_TERMINATOR};

    /**
     * return list of <token, POSTag> from sentence
     *
     * @param sentence sentence
     * @return list of <token, POSTag>
     */
    public static ArrayList<String[]> doPOSTag(String sentence) {
        ArrayList<String[]> postag = IndonesianPOSTagger.doPOSTag(sentence);
        return postag;
    }

    /**
     * get all postag from sentence define in HEADER
     * @param sentence sentence and postag
     * @param delimiter delimiter
     * @return feature from sentence 
     */
    public static String createAllPostag(ArrayList<String[]> sentence, String delimiter) {

        String feature = "";
        if (containOpenParenthesis(sentence)) {
            feature = feature + "1" + delimiter;
        } else {
            feature = feature + "0" + delimiter;
        }
        if (containCloseParenthesis(sentence)) {
            feature = feature + "1" + delimiter;
        } else {
            feature = feature + "0" + delimiter;
        }
        if (containsSlash(sentence)) {
            feature = feature + "1" + delimiter;
        } else {
            feature = feature + "0" + delimiter;
        }
        if (containSemicolon(sentence)) {
            feature = feature + "1" + delimiter;
        } else {
            feature = feature + "0" + delimiter;
        }
        if (containColon(sentence)) {
            feature = feature + "1" + delimiter;
        } else {
            feature = feature + "0" + delimiter;
        }
        if (containQuotation(sentence)) {
            feature = feature + "1" + delimiter;
        } else {
            feature = feature + "0" + delimiter;
        }
        if (containSentenceTerminator(sentence)) {
            feature = feature + "1" + delimiter;
        } else {
            feature = feature + "0" + delimiter;
        }
        if (containComma(sentence)) {
            feature = feature + "1" + delimiter;
        } else {
            feature = feature + "0" + delimiter;
        }
        if (containDash(sentence)) {
            feature = feature + "1" + delimiter;
        } else {
            feature = feature + "0" + delimiter;
        }
        if (containEllipsis(sentence)) {
            feature = feature + "1" + delimiter;
        } else {
            feature = feature + "0" + delimiter;
        }
        if (containAdjective(sentence)) {
            feature = feature + "1" + delimiter;
        } else {
            feature = feature + "0" + delimiter;
        }
        if (containAdverb(sentence)) {
            feature = feature + "1" + delimiter;
        } else {
            feature = feature + "0" + delimiter;
        }
        if (containCommonNoun(sentence)) {
            feature = feature + "1" + delimiter;
        } else {
            feature = feature + "0" + delimiter;
        }
        if (containProperNoun(sentence)) {
            feature = feature + "1" + delimiter;
        } else {
            feature = feature + "0" + delimiter;
        }
        if (containGenitiveNoun(sentence)) {
            feature = feature + "1" + delimiter;
        } else {
            feature = feature + "0" + delimiter;
        }
        if (containIntransitiveVerb(sentence)) {
            feature = feature + "1" + delimiter;
        } else {
            feature = feature + "0" + delimiter;
        }
        if (containTransitiveVerb(sentence)) {
            feature = feature + "1" + delimiter;
        } else {
            feature = feature + "0" + delimiter;
        }
        if (containPreposition(sentence)) {
            feature = feature + "1" + delimiter;
        } else {
            feature = feature + "0" + delimiter;
        }
        if (containModal(sentence)) {
            feature = feature + "1" + delimiter;
        } else {
            feature = feature + "0" + delimiter;
        }
        if (containCoorConjuction(sentence)) {
            feature = feature + "1" + delimiter;
        } else {
            feature = feature + "0" + delimiter;
        }
        if (containSuborConjuction(sentence)) {
            feature = feature + "1" + delimiter;
        } else {
            feature = feature + "0" + delimiter;
        }
        if (containDeterminer(sentence)) {
            feature = feature + "1" + delimiter;
        } else {
            feature = feature + "0" + delimiter;
        }
        if (containInterjection(sentence)) {
            feature = feature + "1" + delimiter;
        } else {
            feature = feature + "0" + delimiter;
        }
        if (containOrdinalNumberals(sentence)) {
            feature = feature + "1" + delimiter;
        } else {
            feature = feature + "0" + delimiter;
        }
        if (containCollectiveNumerals(sentence)) {
            feature = feature + "1" + delimiter;
        } else {
            feature = feature + "0" + delimiter;
        }
        if (containPrimaryNumerals(sentence)) {
            feature = feature + "1" + delimiter;
        } else {
            feature = feature + "0" + delimiter;
        }
        if (containIrregularNumerals(sentence)) {
            feature = feature + "1" + delimiter;
        } else {
            feature = feature + "0" + delimiter;
        }
        if (containPersonalPronouns(sentence)) {
            feature = feature + "1" + delimiter;
        } else {
            feature = feature + "0" + delimiter;
        }
        if (containWHPronouns(sentence)) {
            feature = feature + "1" + delimiter;
        } else {
            feature = feature + "0" + delimiter;
        }
        if (containNumberPronouns(sentence)) {
            feature = feature + "1" + delimiter;
        } else {
            feature = feature + "0" + delimiter;
        }
        if (containLocativePronouns(sentence)) {
            feature = feature + "1" + delimiter;
        } else {
            feature = feature + "0" + delimiter;
        }
        if (containNegation(sentence)) {
            feature = feature + "1" + delimiter;
        } else {
            feature = feature + "0" + delimiter;
        }
        if (containSymbols(sentence)) {
            feature = feature + "1" + delimiter;
        } else {
            feature = feature + "0" + delimiter;
        }
        if (containParticles(sentence)) {
            feature = feature + "1" + delimiter;
        } else {
            feature = feature + "0" + delimiter;
        }
        if (containForeignWords(sentence)) {
            feature = feature + "1" + delimiter;
        } else {
            feature = feature + "0" + delimiter;
        }
        if (containPronouns(sentence)) {
            feature = feature + "1" + delimiter;
        } else {
            feature = feature + "0" + delimiter;
        }
        if (containNumerals(sentence)) {
            feature = feature + "1" ;
        } else {
            feature = feature + "0" ;
        }
        
        return feature;
    }

    private static boolean contain(ArrayList<String[]> text, String postag) {
        boolean found = false;
        int i = 0;
        while (!found && i < text.size()) {
            if (text.get(i)[1].compareTo(postag) == 0) {
                found = true;
            }
            i++;
        }
        return found;
    }

    /**
     * return true if this sentence contain pronouns
     *
     * @param sentence sentence
     * @return true if this sentence contain pronouns
     */
    public static boolean containPronouns(ArrayList<String[]> sentence) {
        boolean found = false;
        int i = 0;
        while (!found && i < PRONOUNS.length) {
            if (contain(sentence, PRONOUNS[i])) {
                found = true;
            }
            i++;
        }
        return found;
    }
    
    /**
     * return true if this sentence contain numerals
     *
     * @param sentence sentence
     * @return true if this sentence contain numerals
     */
    public static boolean containNumerals(ArrayList<String[]> sentence) {
        boolean found = false;
        int i = 0;
        while (!found && i < NUMERALS.length) {
            if (contain(sentence, NUMERALS[i])) {
                found = true;
            }
            i++;
        }
        return found;
    }

    /**
     * return if this sentence contain cardinal number
     *
     * @param sentence sentence
     * @return true if this sentence contain cardinal number
     */
    public static boolean containCardinalNumber(ArrayList<String[]> sentence) {
        return contain(sentence, CARDINAL_NUMBER);
    }

    /**
     * return if this sentence contain punctuation
     *
     * @param sentence sentence
     * @return true if this sentence contain punctuation
     */
    public static boolean containPunctuation(ArrayList<String[]> sentence) {
        boolean found = false;
        int i = 0;
        while (!found && i < PUNCTUATION.length) {
            if (contain(sentence, PUNCTUATION[i])) {
                found = true;
            }
            i++;
        }
        return found;
    }

    /**
     * return if this sentence contain Open Parenthesis
     * <P>
     * Example : (
     *
     * @param sentence sentence
     * @return true if this sentence contain Open Parenthesis
     */
    public static boolean containOpenParenthesis(ArrayList<String[]> sentence) {
        return contain(sentence, OPEN_PARENTHESIS);
    }

    /**
     * return if this sentence contain Close Parenthesis
     * <P>
     * Example : )
     *
     * @param sentence sentence
     * @return true if this sentence contain Close Parenthesis
     */
    public static boolean containCloseParenthesis(ArrayList<String[]> sentence) {
        return contain(sentence, CLOSE_PARENTHESIS);
    }

    /**
     * return if this sentence contain Slash
     * <P>
     * Example : /
     *
     * @param sentence sentence
     * @return true if this sentence contain Slash
     */
    public static boolean containsSlash(ArrayList<String[]> sentence) {
        return contain(sentence, SLASH);
    }

    /**
     * return if this sentence contain Semicolon
     * <P>
     * Example : ;
     *
     * @param sentence sentence
     * @return true if this sentence contain Semicolon
     */
    public static boolean containSemicolon(ArrayList<String[]> sentence) {
        return contain(sentence, SEMICOLON);
    }

    /**
     * return if this sentence contain Colon
     * <P>
     * Example : :
     *
     * @param sentence sentence
     * @return true if this sentence contain Colon
     */
    public static boolean containColon(ArrayList<String[]> sentence) {
        return contain(sentence, COLON);
    }

    /**
     * return if this sentence contain Quotation
     * <P>
     * Example : "'
     *
     * @param sentence sentence
     * @return true if this sentence contain Quotation
     */
    public static boolean containQuotation(ArrayList<String[]> sentence) {
        return contain(sentence, QUOTATION1) || contain(sentence, QUOTATION2);
    }

    /**
     * return if this sentence contain Sentence Terminator
     * <P>
     * Example : .!?
     *
     * @param sentence sentence
     * @return true if this sentence contain Sentence Terminator
     */
    public static boolean containSentenceTerminator(ArrayList<String[]> sentence) {
        return contain(sentence, SENTENCE_TERMINATOR);
    }

    /**
     * return if this sentence contain Comma
     * <P>
     * Example : ,
     *
     * @param sentence sentence
     * @return true if this sentence contain Comma
     */
    public static boolean containComma(ArrayList<String[]> sentence) {
        return contain(sentence, COMMA);
    }

    /**
     * return if this sentence contain Dash
     * <P>
     * Example : -
     *
     * @param sentence sentence
     * @return true if this sentence contain Dash
     */
    public static boolean containDash(ArrayList<String[]> sentence) {
        return contain(sentence, DASH);
    }

    /**
     * return if this sentence contain Ellipsis
     * <P>
     * Example : ...
     *
     * @param sentence sentence
     * @return true if this sentence contain Ellipsis
     */
    public static boolean containEllipsis(ArrayList<String[]> sentence) {
        return contain(sentence, ELLIPSIS);
    }

    /**
     * return if this sentence contain adjective
     * <P>
     * Example : Kaya, Manis
     *
     * @param sentence sentence
     * @return true if text contain adjective
     */
    public static boolean containAdjective(ArrayList<String[]> sentence) {
        return contain(sentence, ADJECTIVE);
    }

    /**
     * return if this sentence contain adverb
     * <P>
     * Example : Sementara, Nanti
     *
     * @param sentence sentence
     * @return true if this sentence contain adverb
     */
    public static boolean containAdverb(ArrayList<String[]> sentence) {
        return contain(sentence, ADVERB);
    }

    /**
     * return if this sentence contain Common Noun
     * <P>
     * Example : Mobil
     *
     * @param sentence sentence
     * @return true if this sentence contain Common Noun
     */
    public static boolean containCommonNoun(ArrayList<String[]> sentence) {
        return contain(sentence, COMMON_NOUN);
    }

    /**
     * return if this sentence contain Proper Noun
     * <P>
     * Example : Bekasi, Indonesia
     *
     * @param sentence sentence
     * @return true if this sentence contain Proper Noun
     */
    public static boolean containProperNoun(ArrayList<String[]> sentence) {
        return contain(sentence, PROPER_NOUN);
    }

    /**
     * return if this sentence contain Genitive Noun
     * <P>
     * Example : Bukunya
     *
     * @param sentence sentence
     * @return true if this sentence contain Genitive Noun
     */
    public static boolean containGenitiveNoun(ArrayList<String[]> sentence) {
        return contain(sentence, GENITIVE_NOUN);
    }

    /**
     * return if this sentence contain Intransitive Verb
     * <P>
     * Example : Pergi
     *
     * @param sentence sentence
     * @return true if this sentence contain Intransitive Verb
     */
    public static boolean containIntransitiveVerb(ArrayList<String[]> sentence) {
        return contain(sentence, INTRANSITIVE_VERB);
    }

    /**
     * return if this sentence contain Transitive Verb
     * <P>
     * Example : Membeli
     *
     * @param sentence sentence
     * @return true if this sentence contain Transitive Verb
     */
    public static boolean containTransitiveVerb(ArrayList<String[]> sentence) {
        return contain(sentence, TRANSITIVE_VERB);
    }

    /**
     * return if this sentence contain Preposition
     * <P>
     * Example : Di, Ke, Dari
     *
     * @param sentence sentence
     * @return true if this sentence contain Preposition
     */
    public static boolean containPreposition(ArrayList<String[]> sentence) {
        return contain(sentence, PREPROSITION);
    }

    /**
     * return if this sentence contain modal
     * <P>
     * Example : Bisa
     *
     * @param sentence sentence
     * @return true if this sentence contain modal
     */
    public static boolean containModal(ArrayList<String[]> sentence) {
        return contain(sentence, MODAL);
    }

    /**
     * return if this sentence contain Coor Conjuction
     * <P>
     * Example : Dan, Atau, Tetapi
     *
     * @param sentence sentence
     * @return true if this sentence contain Coor Conjuction
     */
    public static boolean containCoorConjuction(ArrayList<String[]> sentence) {
        return contain(sentence, COOR_CONJUNCTION);

    }

    /**
     * return if this sentence contain Subor Conjuction
     * <P>
     * Example : Jika, ketika
     *
     * @param sentence sentence
     * @return true if this sentence contain Subor Conjuction
     */
    public static boolean containSuborConjuction(ArrayList<String[]> sentence) {
        return contain(sentence, SUBOR_CONJUNCTION);
    }

    /**
     * return if this sentence contain Determiner
     * <P>
     * Example : Parah, Ini, Itu
     *
     * @param sentence sentence
     * @return true if this sentence contain Determiner
     */
    public static boolean containDeterminer(ArrayList<String[]> sentence) {
        return contain(sentence, DETERMINER);
    }

    /**
     * return if this sentence contain Interjection
     * <P>
     * Example : Wah, Aduh, Oi
     *
     * @param sentence sentence
     * @return true if this sentence contain Interjection
     */
    public static boolean containInterjection(ArrayList<String[]> sentence) {
        return contain(sentence, INTERJECTION);
    }

    /**
     * return if this sentence contain Ordinal Numberals<P>
     * Example : Pertama, Kedua
     *
     * @param sentence sentence
     * @return true if this sentence contain Ordinal Numberals
     */
    public static boolean containOrdinalNumberals(ArrayList<String[]> sentence) {
        return contain(sentence, ORDINAL_NUMBERALS);
    }

    /**
     * return if this sentence contain Collective Numerals
     * <P>
     * Example : Bertiga
     *
     * @param sentence sentence
     * @return true if this sentence contain Collective Numerals
     */
    public static boolean containCollectiveNumerals(ArrayList<String[]> sentence) {
        return contain(sentence, COLLECTIVE_NUMBERALS);
    }

    /**
     * return if this sentence contain Primary Numerals
     * <P>
     * Example : Satu, Dua
     *
     * @param sentence sentence
     * @return true if this sentence contain Primary Numerals
     */
    public static boolean containPrimaryNumerals(ArrayList<String[]> sentence) {
        return contain(sentence, PRIMARY_NUMBERALS);
    }

    /**
     * return if this sentence contain Irregular Numeral
     * <P>
     * Example : Beberapa
     *
     * @param sentence sentence
     * @return true if this sentence contain Irregular Numeral
     */
    public static boolean containIrregularNumerals(ArrayList<String[]> sentence) {
        return contain(sentence, IRREGULAR_NUMBERALS);
    }

    /**
     * return if this sentence contain Personal Pronouns
     * <P>
     * Example : Saya, Kamu
     *
     * @param sentence sentence
     * @return true if this sentence contain Personal Pronouns
     */
    public static boolean containPersonalPronouns(ArrayList<String[]> sentence) {
        return contain(sentence, PERSONAL_PRONOUNS);
    }

    /**
     * return if this sentence contain WH Pronouns
     * <P>
     * Example : Apa, Siapa
     *
     * @param sentence sentence
     * @return true if this sentence contain WH Pronouns
     */
    public static boolean containWHPronouns(ArrayList<String[]> sentence) {
        return contain(sentence, WH_PRONOUNS);
    }

    /**
     * return if this sentence contain Number Pronouns
     * <P>
     * Example : Kedua-duanya
     *
     * @param sentence sentence
     * @return true if this sentence contain Number Pronouns
     */
    public static boolean containNumberPronouns(ArrayList<String[]> sentence) {
        return contain(sentence, NUMBER_PRONOUNS);
    }

    /**
     * return if this sentence contain Locative Pronouns
     * <P>
     * Example : Sini, Situ, Sana
     *
     * @param sentence sentence
     * @return true if this sentence contain Locative Pronouns
     */
    public static boolean containLocativePronouns(ArrayList<String[]> sentence) {
        return contain(sentence, LOCATIVE_PRONOUNS);
    }

    /**
     * return if this sentence contain Negation
     * <P>
     * Example : Bukan, Tidak
     *
     * @param sentence sentence
     * @return true if this sentence contain Negation
     */
    public static boolean containNegation(ArrayList<String[]> sentence) {
        return contain(sentence, NEGATION);
    }

    /**
     * return if this sentence contain Symbols
     * <P>
     * Example : @#$%^&
     *
     * @param sentence sentence
     * @return true if this sentence contain Symbols
     */
    public static boolean containSymbols(ArrayList<String[]> sentence) {
        return contain(sentence, SYMBOLS);
    }

    /**
     * return if this sentence contain Particles
     * <P>
     * Example : Pun, Kah
     *
     * @param sentence sentence
     * @return true if this sentence contain Particles
     */
    public static boolean containParticles(ArrayList<String[]> sentence) {
        return contain(sentence, PARTICLES);
    }

    /**
     * return if this sentence contain Foreign Words
     * <P>
     * Example : Foreign, Word
     *
     * @param sentence sentence
     * @return true if this sentence contain Foreign Words
     */
    public static boolean containForeignWords(ArrayList<String[]> sentence) {
        return contain(sentence, FOREIGN_WORDS);
    }

}
