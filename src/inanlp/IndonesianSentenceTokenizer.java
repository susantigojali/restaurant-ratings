package inanlp;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ALVIN
 */
public class IndonesianSentenceTokenizer {
    public ArrayList<String> listAkronim;
    public ArrayList<String> listMajemuk;

    public IndonesianSentenceTokenizer() {
        this.listAkronim = new ArrayList<String>();
        this.listMajemuk = new ArrayList<String>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader("./resource/tokenizer/acronym.txt"));

            String line = null;

            while ((line = reader.readLine()) != null) {
                this.listAkronim.add(line);
            }

            reader.close();
            
            reader = new BufferedReader(new FileReader("./resource/tokenizer/compositewords.txt"));

            line = null;

            while ((line = reader.readLine()) != null) {
                this.listMajemuk.add(line);
            }

            reader.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(IndonesianSentenceTokenizer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(IndonesianSentenceTokenizer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private ArrayList<String> tokenizeWord(String word) {
        ArrayList<String> token = new ArrayList<String>();
        
        String rangeNumber = "\\d{1,5}[-~]\\d{1,5}";
        String reduplication1 = "\\w*([\\w]+)-\\1\\w*";
        String reduplication2 = "\\w([\\w]+)-\\w*\\1";
        String compoundWord = "\\w+-\\w+";
        
        if(word.matches(rangeNumber)) {
            if(word.contains("-")) {
                String[] tokens = word.split("-");
                token.add(tokens[0]);
                token.add("-");
                token.add(tokens[1]);
            }
            else {
                String[] tokens = word.split("~");
                token.add(tokens[0]);
                token.add("~");
                token.add(tokens[1]);
            }
        }
        else if(word.matches(reduplication1)) {
            token.add(word);
        }
        else if(word.matches(reduplication2)) {
            token.add(word);
        }
        else if(word.matches(compoundWord)) {
            String[] tokens = word.split("-");
            token.add(tokens[0]);
            token.add("-");
            token.add(tokens[1]);
        }
        else {
            token.add(word);
        }
        
        return token;
    }
    
    public ArrayList<String> tokenizeSentence(String sentence){
        ArrayList<String> token = new ArrayList<String>();
        
        String[] strippedSpace = sentence.split("\\s+");
        
        String decimalIndonesian = "\\d+,\\d+";
        String splitCommaButSaveDelimiter = "(?<=[,])|(?=[,])";
        String splitButSaveDelimiter = "(?<=[^\\w`~@#%\\-])|(?=[^\\w`~@#%\\-])";
        String apostrophedWord = "[\\w&&[^_]]+'[\\w&&[^_]]+";
        String urlException = "(https?://)?(www\\.)?[\\w\\-\\.~]+\\.[a-z]{2,6}(/[\\w\\-\\.~]*)*\"?";
        String urlExc2 = "^(https?://).*";
        String dateException1 = "\\d{1,2}[/\\.]\\d{1,2}[/\\.]\\d{2,4}";
        String dateException2 = "\\d{2,4}[/\\.]\\d{1,2}[/\\.]\\d{1,2}";
        String timeException = "([12])?\\d([\\:\\.])\\d{2}(\\2\\d{2})?([aApP][mM])?";
        
        //purge some punctuations
        ArrayList<String> strippedComma = new ArrayList();
        for(String word : strippedSpace) {
            String[] words = {word};
            if(!word.matches(decimalIndonesian)) {
                words = word.split(splitCommaButSaveDelimiter);
            }
            strippedComma.addAll(Arrays.asList(words));
        }
        
        ArrayList<String> tokens = new ArrayList();
        for(String word : strippedComma) {
            String[] trueWords = {word};
            //System.out.println(word);
            if(!word.matches(decimalIndonesian) &&
                    !word.matches(apostrophedWord) &&
                    !word.matches(urlException) &&
                    !word.matches(urlExc2) &&
                    !word.matches(dateException1) &&
                    !word.matches(dateException2) &&
                    !word.matches(timeException)) {
                trueWords = word.split(splitButSaveDelimiter);
                //System.out.println("MASUK SPLIT");
            }
            tokens.addAll(Arrays.asList(trueWords));
        }
        
        String nonwordSeries = "[\\W_]+";
        
        for(String word : tokens) {
            if(word.matches(nonwordSeries)){
                token.add(word);
            }
            else {
                token.addAll(tokenizeWord(word));
            }
        }

        return token;
    }
    
    /*public ArrayList<String> tokenizeSentenceWithCompositeWords(String sentence){
        ArrayList<String> token = new ArrayList<String>();

        sentence = sentence.replaceAll("-", " - ");
        String[] tokens = sentence.split("\\s+");
        
        String pattern0 = "\\";
        String pattern1 = "\\w{1}.*\\w{1}";
        String pattern2 = "\\W{1}.*\\W{1}";
        String pattern3 = "\\W{1}.{1,}";
        String pattern4 = ".{1,}\\W{1}";
        String pattern5 = "[\\w{1}\\W{1}]";

        for(int i = 0; i < tokens.length; i++){
            if(tokens[i].matches(pattern1)){
                token.add(tokens[i]);
            }
            else if(tokens[i].matches(pattern2)){
                if(tokens[i].length() <= 2){
                    token.add(tokens[i]);
                }
                else {
                    String opunc = tokens[i].substring(0, 1);
                    String word = tokens[i].substring(1, tokens[i].length() - 1);
                    String epunc = tokens[i].substring(tokens[i].length() - 1);

                    token.add(opunc);
                    token.add(word);
                    token.add(epunc);
                }
            }
            else if(tokens[i].matches(pattern3)){
                if(tokens[i].matches("-[^a-zA-Z]+")){
                    token.add(tokens[i]);
                }
                else {
                    token.add(tokens[i].substring(0, 1));

                    token.add(tokens[i].substring(1));
                }
            }
            else if(tokens[i].matches(pattern4)){
                if(this.listAkronim.contains(tokens[i])){
                    token.add(tokens[i]);
                }
                else {
                    String word = tokens[i].substring(0, tokens[i].length() - 1);
                    String punc = tokens[i].substring(tokens[i].length() - 1);

                    token.add(word);

                    token.add(punc);
                }
            }
            else if(tokens[i].matches(pattern5)){
                token.add(tokens[i]);
            }
            else {
                token.add(tokens[i]);
            }
        }

        int i = 0;
        while(i < token.size()){
            if(i < (token.size() - 1)){
                String kataMajemuk = token.get(i) + " " + token.get(i+1);
                if(this.listMajemuk.contains(kataMajemuk)){
                    token.set(i, kataMajemuk);
                    token.remove(i+1);
                }
                
                i += 1;
            }
            else {
                i += 1;
            }
        }
        
        return token;
    }*/
    
    public static void main(String[] args){
        IndonesianSentenceTokenizer tokenizer = new IndonesianSentenceTokenizer();
        ArrayList<String> token = tokenizer.tokenizeSentence("2015/2016 20/12/2014 -20 bdg-jkt @t.co,pukul-memukul.#p4d4, 2015-04-06 a9809sdasd-yui+24.");
        for(int i = 0; i < token.size(); i++){
            System.out.println(token.get(i));
        }
        
        System.out.println("==================================");
        
       /* token = tokenizer.tokenizeSentenceWithCompositeWords("Alvin pergi ke bagian kanan rumah sakit Prof. Iping.");
        for(int i = 0; i < token.size(); i++){
            System.out.println(token.get(i));
        } */
    }
}
