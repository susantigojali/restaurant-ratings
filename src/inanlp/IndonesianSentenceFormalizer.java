package inanlp;

import IndonesianNLP.IndonesianSentenceFormalization;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author ALVIN
 */
public class IndonesianSentenceFormalizer {
    private HashMap<String,String> listUnformal;
    private ArrayList<String> listRule;
    private ArrayList<String> listStopword;

    public IndonesianSentenceFormalizer() {
        this.listUnformal = new HashMap<String, String>();
        this.listRule = new ArrayList<String>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader("./resource/formalizer/formalizationDict.txt"));

            String line = null;
            while ((line = reader.readLine()) != null) {
                String[] entry = line.split("\t");
                this.listUnformal.put(entry[0], entry[1]);
            }
            reader.close();
            
            reader = new BufferedReader(new FileReader("./resource/formalizer/formalizationRule.txt"));
            line = null;
            while ((line = reader.readLine()) != null) {
                this.listRule.add(line);
            }
            reader.close();
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(IndonesianSentenceFormalizer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(IndonesianSentenceFormalizer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void initStopword(){
        this.setListStopword(new ArrayList<String>());
        try {
            BufferedReader reader = new BufferedReader(new FileReader("./resource/formalizer/stopword.txt"));
            String line = null;
            while ((line = reader.readLine()) != null) {
                this.getListStopword().add(line);
            }
            reader.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(IndonesianSentenceFormalizer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(IndonesianSentenceFormalizer.class.getName()).log(Level.SEVERE, null, ex);
        }    
    }
    
    public String deleteStopword(String sentence){
        IndonesianSentenceTokenizer tokenizer = new IndonesianSentenceTokenizer();
        ArrayList<String> listToken = tokenizer.tokenizeSentence(sentence);
        String noSw = "";
        for(int i = 0; i < listToken.size(); i++){
            String word = listToken.get(i);
            if (!getListStopword().contains(word))
            {   noSw = noSw + word + " ";
            }
        }
        return noSw;    
    }

    public ArrayList<String> formalizeTokens(ArrayList<String> tokens){
        ArrayList<String> formalTokens = new ArrayList();
        
        for(int i = 0; i < tokens.size(); i++){
            String word = tokens.get(i);
            if(word.matches("[a-eA-E]{1,2}\\d{1,4}[a-zA-Z]{0,3}")) {
                if(i > 0) {
                    String prevWord = formalTokens.get(i - 1);
                    if(prevWord.equalsIgnoreCase("nomor") ||
                            prevWord.equalsIgnoreCase("pelat") ||
                            prevWord.equalsIgnoreCase("nopol") ||
                            prevWord.equalsIgnoreCase("polisi")) {
                        word = "nomor" + word;
                    }
                    else if(prevWord.equalsIgnoreCase("bernomor") ||
                            prevWord.equalsIgnoreCase("berpelat") ||
                            prevWord.equalsIgnoreCase("bernopol")) {
                        word = "nomor" + word;
                    }
                }
            }
            String formalWord = formalizeWord(word);
            formalTokens.add(formalWord);
        }
        
        return formalTokens;
    }
    
    public String formalizeSentence(String sentence){
        IndonesianSentenceTokenizer tokenizer = new IndonesianSentenceTokenizer();
        
        ArrayList<String> listToken = tokenizer.tokenizeSentence(sentence);
        listToken = formalizeTokens(listToken);
        
        String formalSentence = "";
        for(String token : listToken) {
            formalSentence += token.trim() + ' ';
        }
        
        return formalSentence.trim();
    }
    
    public String formalizeWord(String word){
        String ruleName = "";
        String ruleCondition = "";
        String[] rule = new String[2];
        
        int i = 0;
        
        while(i < this.listRule.size()){
            ruleName = this.listRule.get(i);

            if(ruleName.startsWith("@@")){
                i += 1;
                ruleCondition = this.listRule.get(i);

                if(ruleCondition.startsWith("##")){
                    boolean match = false;
                    boolean newRule = false;

                    while(!match && !newRule && (i < this.listRule.size())){
                        ruleCondition = this.listRule.get(i);

                        if(ruleCondition.startsWith("##")){
                            ruleCondition = ruleCondition.substring(2);
                            match = word.matches(ruleCondition);

                            if(!match){
                                i += 1;
                            }
                        }
                        else if(ruleCondition.startsWith("@@")){
                            newRule = true;
                        }
                        else {
                            i += 1;
                        }
                    }

                    if(match){
                        i += 1;
                        while((i < this.listRule.size()) && (!this.listRule.get(i).startsWith("@@")) && (!this.listRule.get(i).startsWith("##"))){
                            rule = this.listRule.get(i).split(">>>");
                            word = word.replaceAll(rule[0], rule[1]);

                            i += 1;
                        }

                        if(i < this.listRule.size()){
                            if(this.listRule.get(i).startsWith("##")){
                                i += 1;
                                while((i < this.listRule.size()) && (!this.listRule.get(i).startsWith("@@"))){
                                    i += 1;
                                }
                            }
                        }
                    }
                }
                else if(ruleCondition.startsWith("~~")) {
                    boolean match = false;
                    boolean newRule = false;
                    while(!match && !newRule && i < this.listRule.size()){
                        ruleCondition = this.listRule.get(i);

                        if(ruleCondition.startsWith("~~")){
                            rule = ruleCondition.substring(2).split(">>>");
                            match = word.matches(rule[0]);

                            if(!match){
                                i++;
                            }
                        }
                        else if(ruleCondition.startsWith("@@")){
                            newRule = true;
                        }
                        else {
                            i++;
                        }
                    }

                    if(match){
                        word = word.replaceAll(rule[0], rule[1]);
                        i = listRule.size();
                    }
                }
                else {
                    while((i < this.listRule.size()) && (!this.listRule.get(i).startsWith("@@"))){
                        rule = this.listRule.get(i).split(">>>");
                        word = word.replaceAll(rule[0], rule[1]);
                        i += 1;
                    }
                }
            }
        }

        if(this.listUnformal.containsKey(word)){
            word = this.listUnformal.get(word);
        }
        
        return word;
    }
    
    public static void main(String[] args){
        IndonesianSentenceFormalizer formalizer = new IndonesianSentenceFormalizer();
        
        String sentence = "mobil2nya berplat nomer ab1234ba t4ku b4u donk loecoe 4l4y bangedh gt di bank BNI.";
        System.out.println(sentence);
        System.out.println("1 :"+ formalizer.formalizeSentence(sentence));
        formalizer.initStopword();
        System.out.println("1 sw: "+formalizer.deleteStopword(sentence));
        
        IndonesianSentenceFormalization formalization = new IndonesianSentenceFormalization();
        System.out.println("2: " + formalization.formalizeSentence(sentence));
        
        
    }

	public ArrayList<String> getListStopword() {
		return listStopword;
	}

	public void setListStopword(ArrayList<String> listStopword) {
		this.listStopword = listStopword;
	}
}