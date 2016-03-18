package inanlp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class IndonesianStemmer {
    private ArrayList<String> dictionary;
    private ArrayList<String> possessivePrefixRules;
    private ArrayList<String> reduplicationRules;
    private ArrayList<String> prefixRules;
    private ArrayList<String> suffixRules;
    private ArrayList<String> possessiveSuffixRules;
    private ArrayList<String> particleSuffixRules;
    private ArrayList<String> compoundWordRules;
    private ArrayList<String> eStartedWordException;
    private ArrayList<String> kStartedWordException;
    private ArrayList<String> keStartedWordException;
    private ArrayList<String> mStartedWordException;
    private ArrayList<String> nStartedWordException;
    private ArrayList<String> rStartedWordException;
    private ArrayList<String> perStartedWordException;
    private ArrayList<String> kEndedWordException;
    
    private final int POSSESSIVE_PREFIX_RULES = 0;
    private final int PREFIX_RULES = 1;
    private final int SUFFIX_RULES = 0;
    private final int POSSESSIVE_SUFFIX_RULES = 1;
    private final int PARTICLE_SUFFIX_RULES = 2;

    public IndonesianStemmer(){
        BufferedReader reader;
        try {
            dictionary = new ArrayList();
            reader = new BufferedReader(new FileReader("./resource/stemmer/dictionary.txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                if(! line.startsWith("##")) {
                    dictionary.add(line);
                }
            }
            
            possessivePrefixRules = new ArrayList();
            reader = new BufferedReader(new FileReader("./resource/stemmer/possessivePrefixRule.txt"));
            while ((line = reader.readLine()) != null) {
                if(! line.startsWith("##")) {
                    possessivePrefixRules.add(line);
                }
            }
            
            reduplicationRules = new ArrayList();
            reader = new BufferedReader(new FileReader("./resource/stemmer/reduplicationRule.txt"));
            while ((line = reader.readLine()) != null) {
                if(! line.startsWith("##")) {
                    reduplicationRules.add(line);
                }
            }
            
            prefixRules = new ArrayList();
            reader = new BufferedReader(new FileReader("./resource/stemmer/prefixRule.txt"));
            while ((line = reader.readLine()) != null) {
                if(! line.startsWith("##")) {
                    prefixRules.add(line);
                }
            }
            
            suffixRules = new ArrayList();
            reader = new BufferedReader(new FileReader("./resource/stemmer/suffixRule.txt"));
            while ((line = reader.readLine()) != null) {
                if(! line.startsWith("##")) {
                    suffixRules.add(line);
                }
            }
            
            possessiveSuffixRules = new ArrayList();
            reader = new BufferedReader(new FileReader("./resource/stemmer/possessiveSuffixRule.txt"));
            while ((line = reader.readLine()) != null) {
                if(! line.startsWith("##")) {
                    possessiveSuffixRules.add(line);
                }
            }
            
            particleSuffixRules = new ArrayList();
            reader = new BufferedReader(new FileReader("./resource/stemmer/particleSuffixRule.txt"));
            while ((line = reader.readLine()) != null) {
                if(! line.startsWith("##")) {
                    particleSuffixRules.add(line);
                }
            }
            
            compoundWordRules = new ArrayList();
            reader = new BufferedReader(new FileReader("./resource/stemmer/compoundWordRule.txt"));
            while ((line = reader.readLine()) != null) {
                if(! line.startsWith("##")) {
                    compoundWordRules.add(line);
                }
            }
            
            eStartedWordException = new ArrayList();
            reader = new BufferedReader(new FileReader("./resource/stemmer/exceptionEStartedWord.txt"));
            while ((line = reader.readLine()) != null) {
                eStartedWordException.add(line);
            }
            
            kStartedWordException = new ArrayList();
            reader = new BufferedReader(new FileReader("./resource/stemmer/exceptionKStartedWord.txt"));
            while ((line = reader.readLine()) != null) {
                kStartedWordException.add(line);
            }
            
            keStartedWordException = new ArrayList();
            reader = new BufferedReader(new FileReader("./resource/stemmer/exceptionKeStartedWord.txt"));
            while ((line = reader.readLine()) != null) {
                keStartedWordException.add(line);
            }
            
            mStartedWordException = new ArrayList();
            reader = new BufferedReader(new FileReader("./resource/stemmer/exceptionMStartedWord.txt"));
            while ((line = reader.readLine()) != null) {
                mStartedWordException.add(line);
            }
            
            nStartedWordException = new ArrayList();
            reader = new BufferedReader(new FileReader("./resource/stemmer/exceptionNStartedWord.txt"));
            while ((line = reader.readLine()) != null) {
                nStartedWordException.add(line);
            }
            
            perStartedWordException = new ArrayList();
            reader = new BufferedReader(new FileReader("./resource/stemmer/exceptionPerStartedWord.txt"));
            while ((line = reader.readLine()) != null) {
                perStartedWordException.add(line);
            }
            
            rStartedWordException = new ArrayList();
            reader = new BufferedReader(new FileReader("./resource/stemmer/exceptionRStartedWord.txt"));
            while ((line = reader.readLine()) != null) {
                rStartedWordException.add(line);
            }
            
            kEndedWordException = new ArrayList();
            reader = new BufferedReader(new FileReader("./resource/stemmer/exceptionKEndedWord.txt"));
            while ((line = reader.readLine()) != null) {
                kEndedWordException.add(line);
            }

            reader.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(IndonesianStemmer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(IndonesianStemmer.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
    
    public static void makeDictionary(){
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader("./resource/stemmer/basicword.txt"));
            BufferedWriter writer = new BufferedWriter(new FileWriter("./resource/stemmer/dictionary.txt"));
            
            String line;
            while ((line = reader.readLine()) != null) {
                String[] arr = line.split(" ");

                writer.write(arr[0] + "\n");
                writer.flush();
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(IndonesianStemmer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(IndonesianStemmer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void makeExceptionWords() {
    //ALWAYS RUN THIS AFTER EVERY CHANGE OF THE DICTIONARY WORD LIST
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader("./resource/stemmer/dictionary.txt"));
            BufferedWriter eStartedWord = new BufferedWriter(new FileWriter("./resource/stemmer/exceptionEStartedWord.txt"));
            BufferedWriter kStartedWord = new BufferedWriter(new FileWriter("./resource/stemmer/exceptionKStartedWord.txt"));
            BufferedWriter keStartedWord = new BufferedWriter(new FileWriter("./resource/stemmer/exceptionKeStartedWord.txt"));
            BufferedWriter mStartedWord = new BufferedWriter(new FileWriter("./resource/stemmer/exceptionMStartedWord.txt"));
            BufferedWriter nStartedWord = new BufferedWriter(new FileWriter("./resource/stemmer/exceptionNStartedWord.txt"));
            BufferedWriter perStartedWord = new BufferedWriter(new FileWriter("./resource/stemmer/exceptionPerStartedWord.txt"));
            BufferedWriter rStartedWord = new BufferedWriter(new FileWriter("./resource/stemmer/exceptionRStartedWord.txt"));
            BufferedWriter kEndedWord = new BufferedWriter(new FileWriter("./resource/stemmer/exceptionKEndedWord.txt"));
            
            String line;
            while ((line = reader.readLine()) != null) {
                if(line.charAt(0) == 'e' && !line.equals("e")) {
                    eStartedWord.write(line + '\n');
                }
                if(line.charAt(0) == 'k' && !line.equals("k")) {
                    if(line.charAt(1) != 'e') {
                        kStartedWord.write(line + '\n');
                    }
                    else {
                        keStartedWord.write(line + '\n');
                    }
                }
                if(line.charAt(0) == 'm' && !line.equals("m")) {
                    mStartedWord.write(line + '\n');
                }
                if(line.charAt(0) == 'n' &&
                        !line.startsWith("ng") &&
                        !line.startsWith("ny") &&
                        !line.equals("n")) {
                    nStartedWord.write(line + '\n');
                }
                if(line.startsWith("per")) {
                    perStartedWord.write(line + '\n');
                }
                if(line.charAt(0) == 'r' && !line.equals("r")) {
                    rStartedWord.write(line + '\n');
                }
                if(line.charAt(line.length() - 1) == 'k' && !line.equals("k") && line.length() > 3) {
                    kEndedWord.write(line + '\n');
                }
                
                eStartedWord.flush();
                kStartedWord.flush();
                keStartedWord.flush();
                mStartedWord.flush();
                nStartedWord.flush();
                perStartedWord.flush();
                rStartedWord.flush();
                kEndedWord.flush();
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(IndonesianStemmer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(IndonesianStemmer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private boolean isBasicWord(String word){
        return dictionary.contains(word);
    }
    
    private boolean canBeStemmed(String word){
        return word.length() > 3;
    }
    
    private boolean isConfixValid(String prefix, String suffix) {
        String pre, suf;
        if(prefix.endsWith("-")) {
            pre = prefix.substring(0, prefix.length() - 1);
        }
        else {
            pre = prefix;
        }
        if(suffix.startsWith("-")) {
            suf = suffix.substring(1);
        }
        else {
            suf = suffix;
        }
        return !(pre.equals("di") && suf.equals("an") ||
                pre.equals("ke") && suf.equals("kan") ||
                pre.equals("se") && suf.equals("kan") ||
                pre.equals("se") && suf.equals("an") ||
                pre.equals("se") && suf.equals("i") ||
                pre.equals("ter") && suf.equals("an") ||
                pre.equals("ber") && suf.equals("kan") ||
                pre.equals("ber") && suf.equals("i") ||
                pre.equals("memper") && suf.equals("an") ||
                pre.equals("me") && suf.equals("an") ||
                pre.equals("pe") && suf.equals("kan") ||
                pre.equals("pe") && suf.equals("i") ||
                pre.equals("per") && suf.equals("i"));
    }
    
    private boolean isSuffixedBasicWord(ArrayList<String> wordList, String word) {
        int i = 0;
        boolean match = false;
        String dictionaryWord;
        while(i < wordList.size() && !match) {
            dictionaryWord = wordList.get(i++);
            if(word.equals(dictionaryWord)) {
                match = true;
            }
            else if(word.equals(dictionaryWord + "an")) {
                match = true;
            }
            else if(word.equals(dictionaryWord + "kan")) {
                match = true;
            }
            else if(word.equals(dictionaryWord + "i")) {
                match = true;
            }
        }
        return match;
    }
    
    private boolean isPrefixedBasicWord(ArrayList<String> wordList, String word) {
        int i = 0, idx, length = 0;
        boolean match = false;
        String dictionaryWord, clippedWord, selectedWord;
        while(i < wordList.size()) {
            dictionaryWord = wordList.get(i++);
            clippedWord = dictionaryWord.substring(1);
            if(word.endsWith(clippedWord)) {
                idx = word.indexOf(clippedWord);
                if(idx > 0) {
                    if(dictionaryWord.charAt(0) == word.charAt(idx - 1)) {
                        match = true;
                        
                    }
                    else if(dictionaryWord.charAt(0) == 'k' &&
                            word.charAt(idx - 1) == 'g') {
                        if(idx > 1) {
                            if(word.charAt(idx - 2) == 'n') {
                                match = true;
                            }
                        }
                    }
                    else if(dictionaryWord.charAt(0) == 'p' &&
                            word.charAt(idx - 1) == 'm') {
                        match = true;
                    }
                    else if(dictionaryWord.charAt(0) == 's' &&
                            word.charAt(idx - 1) == 'y') {
                        if(idx > 1) {
                            if(word.charAt(idx - 2) == 'n') {
                                match = true;
                            }
                        }
                    }
                    else if(dictionaryWord.charAt(0) == 't' &&
                            word.charAt(idx - 1) == 'n') {
                        match = true;
                    }
                }
            }
        }
        return match;
    }

    private StemmedWord applyReduplicationRule(StemmedWord word) {
        StemmedWord resultWord = new StemmedWord(word);
        
        int i = 0;
        boolean match = false;
        String rule[] = new String[2], result[];
        while(i < reduplicationRules.size() && !match){
            rule = reduplicationRules.get(i++).split(">>>");
            match = word.root.matches(rule[0]);
        }
        
        if(match) {
            result = rule[1].split("&");
            resultWord.root = word.root.replaceFirst(rule[0], result[1]);
            switch(Integer.parseInt(result[0])) {
                case 0:
                    resultWord.reduplicationType = 0;
                    resultWord.isChanged = true;
                    break;
                case 1:
                    resultWord.reduplicationType = 1;
                    resultWord.isChanged = true;
                    break;
                case 2:
                    resultWord.reduplicationType = 2;
                    resultWord.isChanged = true;
                    break;
                case 3:
                    resultWord.reduplicationType = 3;
                    resultWord.prefixes.add("me-");
                    resultWord.isChanged = true;
                    break;
                case 4:
                    resultWord.reduplicationType = 4;
                    resultWord.prefixes.add("me-");
                    resultWord.suffix = "-i";
                    resultWord.isChanged = true;
                    break;
                default: break;
            }
        }
        
        return resultWord;
    }
    
    private StemmedWord applyPrefixRule(int ruleNumber, StemmedWord word) {
    //ruleNumber = POSSESSIVE_PREFIX_RULES: possesivePrefixRules
    //ruleNumber = PREFIX_RULES: prefixRules
        ArrayList<String> rules;
        StemmedWord resultWord = new StemmedWord(word);
        
        switch(ruleNumber) {
            case POSSESSIVE_PREFIX_RULES: rules = possessivePrefixRules; break;
            case PREFIX_RULES: rules = prefixRules; break;
            default: rules = new ArrayList(); break;
        }
        
        int i = 0;
        boolean match = false;
        String rule[] = new String[2], result[];
        while(i < rules.size() && !match){
            rule = rules.get(i++).split(">>>");
            match = word.root.matches(rule[0]);
        }
        
        if(match) {
            switch(ruleNumber) {
                case POSSESSIVE_PREFIX_RULES:
                    result = rule[1].split("&");
                    resultWord.root = word.root.replaceFirst(rule[0], result[1]);
                    resultWord.possessivePrefix = result[0] + '-';
                    resultWord.isChanged = true;
                    break;
                case PREFIX_RULES:
                    result = rule[1].split("&");
                    String temp = word.root.replaceFirst(rule[0], result[1]);
                    if((result[0].equals("ter") || result[0].equals("ber")) && temp.charAt(0) == 'r') {
                        resultWord.prefixes.add(result[0] + '-');
                        if(isSuffixedBasicWord(rStartedWordException, temp)) {
                            resultWord.root = temp;
                        }
                        else {
                            resultWord.root = temp.substring(1);
                        }
                    }
                    else if(result[0].equals("me") && temp.startsWith("per")) {
                        if(isSuffixedBasicWord(perStartedWordException, temp)) {
                            resultWord.prefixes.add(result[0] + '-');
                            resultWord.root = temp;
                        }
                        else if(isSuffixedBasicWord(rStartedWordException, temp.substring(2))) {
                            resultWord.prefixes.add("memper-");
                            resultWord.root = temp.substring(2);
                        }
                        else {
                            resultWord.prefixes.add("memper-");
                            resultWord.root = temp.substring(3);
                        }
                    }
                    else if((result[0].equals("me") || result[0].equals("pe"))&& temp.startsWith("ke")) {
                        resultWord.prefixes.add(result[0] + '-');
                        if(isSuffixedBasicWord(keStartedWordException, temp)) {
                            resultWord.root = temp;
                        }
                        else if(isSuffixedBasicWord(eStartedWordException, temp)) {
                            resultWord.root = temp.substring(1);
                        }
                        else {
                            resultWord.root = temp.substring(2);
                        }
                    }
                    else if((result[0].equals("me") || result[0].equals("pe"))&& temp.charAt(0) == 'k') {
                        resultWord.prefixes.add(result[0] + '-');
                        if(isSuffixedBasicWord(kStartedWordException, temp)) {
                            resultWord.root = temp;
                        }
                        else {
                            resultWord.root = temp.substring(1);
                        }
                    }
                    else if((result[0].equals("me") || result[0].equals("pe")) && temp.charAt(0) == 'm') {
                        resultWord.prefixes.add(result[0] + '-');
                        if(isSuffixedBasicWord(mStartedWordException, temp)) {
                            resultWord.root = temp;
                        }
                        else {
                            resultWord.root = "p" + temp.substring(1);
                        }
                    }
                    else if((result[0].equals("me") || result[0].equals("pe")) && temp.charAt(0) == 'n') {
                        resultWord.prefixes.add(result[0] + '-');
                        if(temp.startsWith("ng") || temp.startsWith("ny")) {
                            resultWord.root = temp;
                        }
                        else if(isSuffixedBasicWord(nStartedWordException, temp)) {
                            resultWord.root = temp;
                        }
                        else {
                            resultWord.root = "t" + temp.substring(1);
                        }
                    }
                    else if(result[0].equals("pe") && temp.charAt(0) == 'r') {
                        resultWord.prefixes.add(result[0] + '-');
                        if(isSuffixedBasicWord(rStartedWordException, temp)) {
                            resultWord.root = temp;
                        }
                        else {
                            resultWord.root = temp.substring(1);
                        }
                    }
                    else {
                        resultWord.prefixes.add(result[0] + '-');
                        resultWord.root = temp;
                    }
                    resultWord.isChanged = true;
                    break;
                default:
                    break;
            }
        }
        
        return resultWord;
    }
    
    private StemmedWord applySuffixRule(int ruleNumber, StemmedWord word) {
    //ruleNumber = SUFFIX_RULES: suffixRules
    //ruleNumber = POSSESSIVE_SUFFIX_RULES: possesiveSuffixRules
    //ruleNumber = PARTICLE_RULES: particleRules
        ArrayList<String> rules;
        StemmedWord resultWord = new StemmedWord(word);
        
        switch(ruleNumber) {
            case SUFFIX_RULES: rules = suffixRules; break;
            case POSSESSIVE_SUFFIX_RULES: rules = possessiveSuffixRules; break;
            case PARTICLE_SUFFIX_RULES: rules = particleSuffixRules; break;
            default: rules = new ArrayList(); break;
        }
        
        int i = 0;
        boolean match = false;
        String rule[] = new String[2], result[];
        while(i < rules.size() && !match){
            rule = rules.get(i++).split(">>>");
            match = word.root.matches(rule[0]);
        }
        
        if(match) {
            switch(ruleNumber) {
                case SUFFIX_RULES:
                    result = rule[1].split("&");
                    String temp = word.root.replaceFirst(rule[0], result[0]);
                    if(result[1].equals("an") && temp.endsWith("k")) {
                        if(isPrefixedBasicWord(kEndedWordException, temp) &&
                                isConfixValid(word.prefixes.size() > 0 ? word.prefixes.get(0) : "", "an")) {
                            resultWord.suffix = "-" + result[1];
                            resultWord.root = temp;
                        }
                        else if (isConfixValid(word.prefixes.size() > 0 ? word.prefixes.get(0) : "", "kan")){
                            resultWord.suffix = "-kan";
                            resultWord.root = temp.substring(0, temp.length() - 1);
                        }
                    }
                    else {
                        resultWord.suffix = "-" + result[1];
                        resultWord.root = temp;
                    }
                    resultWord.isChanged = true;
                    break;
                case POSSESSIVE_SUFFIX_RULES:
                    result = rule[1].split("&");
                    resultWord.root = word.root.replaceFirst(rule[0], result[0]);
                    resultWord.possessiveSuffix = "-" + result[1];
                    resultWord.isChanged = true;
                    break;
                case PARTICLE_SUFFIX_RULES:
                    result = rule[1].split("&");
                    resultWord.root = word.root.replaceFirst(rule[0], result[0]);
                    resultWord.particleSuffix = "-" + result[1];
                    resultWord.isChanged = true;
                    break;
                default: break;
            }
        }
        
        return resultWord;
    }
    
    public StemmedWord stemWord(String word){
        ArrayList<StemmedWord> queue = new ArrayList();
        StemmedWord initial = new StemmedWord(word);
        
        if(isBasicWord(initial.root)) {
            return initial;
        }
        
        StemmedWord stemmed1 = applyPrefixRule(POSSESSIVE_PREFIX_RULES, initial);
        StemmedWord stemmed2;
        queue.add(stemmed1);
        if(stemmed1.isChanged) {
            queue.add(initial); 
        }
        
        int queueSize = queue.size();
        for(int i = 0; i < queueSize; i++) {
            stemmed1 = queue.get(i);
//            System.out.println(stemmed1);
            stemmed2 = applySuffixRule(PARTICLE_SUFFIX_RULES, stemmed1);
            if(stemmed2.isChanged) {
                queue.add(0, stemmed2);
                i++;
            }
        }
//        System.out.println();
        
        queueSize = queue.size();
        for(int i = 0; i < queueSize; i++) {
            stemmed1 = queue.get(i);
//            System.out.println(stemmed1);
            stemmed2 = applySuffixRule(POSSESSIVE_SUFFIX_RULES, stemmed1);
            if(stemmed2.isChanged) {
                queue.add(0, stemmed2);
                i++; queueSize++;
            }
        }
//        System.out.println();
        
        queueSize = queue.size();
        for(int i = 0; i < queueSize; i++) {
            stemmed1 = queue.get(i);
//            System.out.println(stemmed1);
            if(stemmed1.root.contains("-")) {
                stemmed2 = applyReduplicationRule(stemmed1);
                if(stemmed2.isChanged) {
                    queue.add(0, stemmed2);
                    i++; queueSize++;
                }
            }
        }
//        System.out.println();
        
        for(StemmedWord queueElmt : queue) {
            if(isBasicWord(queueElmt.root)) {
                return queueElmt;
            }
        }
        
        queueSize = queue.size();
        for(int i = 0; i < queueSize; i++) {
            stemmed1 = queue.get(i);
//            System.out.println(stemmed1);
            stemmed2 = applyPrefixRule(PREFIX_RULES, stemmed1);
            if(stemmed2.isChanged) {
                queue.add(0, stemmed2);
                i++; queueSize++;
            }
        }
//        System.out.println();
        
        queueSize = queue.size();
        for(int i = 0; i < queueSize; i++) {
            stemmed1 = queue.get(i);
//            System.out.println(stemmed1);
            stemmed2 = applySuffixRule(SUFFIX_RULES, stemmed1);
            if(stemmed2.isChanged) {
                queue.add(0, stemmed2);
                i++; queueSize++;
            }
        }
//        System.out.println();
        
        for(StemmedWord queueElmt : queue) {
            if(isBasicWord(queueElmt.root)) {
                return queueElmt;
            }
            else {
                int i = 0;
                boolean match = false;
                String rule[] = new String[2];
                while(i < compoundWordRules.size() && !match){
                    rule = compoundWordRules.get(i++).split(">>>");
                    match = queueElmt.root.equals(rule[0]);
                }
                
                if(match) {
                    queueElmt.root = queueElmt.root.replaceAll(rule[0], rule[1]);
                    return queueElmt;
                }
            }
        }
        
        queueSize = queue.size();
        for(int i = 0; i < queueSize; i++) {
            stemmed1 = queue.get(i);
//            System.out.println(stemmed1);
            stemmed2 = applyPrefixRule(PREFIX_RULES, stemmed1);
            if(stemmed2.isChanged) {
                queue.add(0, stemmed2);
                i++; queueSize++;
            }
        }
//        System.out.println();
        
        for(StemmedWord queueElmt : queue) {
            if(isBasicWord(queueElmt.root)) {
                return queueElmt;
            }
        }
        
        queueSize = queue.size();
        for(int i = 0; i < queueSize; i++) {
            stemmed1 = queue.get(i);
//            System.out.println(stemmed1);
            stemmed2 = applyPrefixRule(PREFIX_RULES, stemmed1);
            if(stemmed2.isChanged) {
                queue.add(0, stemmed2);
                i++; queueSize++;
            }
        }
//        System.out.println();
        
        for(StemmedWord queueElmt : queue) {
            if(isBasicWord(queueElmt.root)) {
                return queueElmt;
            }
            else {
                int i = 0;
                boolean match = false;
                String rule[] = new String[2];
                while(i < compoundWordRules.size() && !match){
                    rule = compoundWordRules.get(i++).split(">>>");
                    match = queueElmt.root.equals(rule[0]);
                }
                
                if(match) {
                    queueElmt.root = queueElmt.root.replaceAll(rule[0], rule[1]);
                    return queueElmt;
                }
            }
        }
        
        return initial;
    }
    
    public String stemSentence(String sentence) {
        IndonesianSentenceTokenizer tokenizer = new IndonesianSentenceTokenizer();
        String result = "";
        
        ArrayList<String> words = tokenizer.tokenizeSentence(sentence);
        for(String word : words){
            StemmedWord stemmedWord = stemWord(word);
            result = result + stemmedWord.root + ' ';
        }

        return result.trim();
    }

    public static void main(String args[]){
//        IndonesianStemmer.makeExceptionWords();
        IndonesianStemmer stemmer = new IndonesianStemmer();
        String word = "kupertanggungjawabkan";
        StemmedWord stem = stemmer.stemWord(word);
        
        System.out.println("Kata dasar: " + stem.root);
        System.out.print("Jenis kata ulang: ");
        switch(stem.reduplicationType) {
            case 0: System.out.println("kata ulang murni"); break;
            case 1: System.out.println("kata ulang berubah bunyi"); break;
            case 2: System.out.println("kata ulang berimbuhan pada keseluruhan kata ulang"); break;
            case 3: 
            case 4: System.out.println("kata ulang berimbuhan pada lingga kedua"); break;
            default: System.out.println("bukan kata ulang");
        }
        System.out.println("Awalan posesif: " + stem.possessivePrefix);
        System.out.print("Awalan: ");
        for (String prefix : stem.prefixes) {
            System.out.print(prefix + ' ');
        }
        System.out.println();
        System.out.println("Akhiran: " + stem.suffix);
        System.out.println("Partikel akhiran: " + stem.particleSuffix);
        System.out.println("Akhiran posesif: " + stem.possessiveSuffix);
    }
    
    public class StemmedWord {
        public String possessivePrefix;
        public int reduplicationType;
        public ArrayList<String> prefixes;
        public String root;
        public String suffix;
        public String particleSuffix;
        public String possessiveSuffix;
        public boolean isChanged;
        
        public StemmedWord() {
            possessivePrefix = "";
            reduplicationType = -1;
            prefixes = new ArrayList();
            root = "";
            suffix = "";
            particleSuffix = "";
            possessiveSuffix = "";
            isChanged = false;
        }
        
        public StemmedWord(String word) {
            possessivePrefix = "";
            reduplicationType = -1;
            prefixes = new ArrayList();
            root = word;
            suffix = "";
            particleSuffix = "";
            possessiveSuffix = "";
            isChanged = false;
        }
        
        public StemmedWord(StemmedWord word) {
            possessivePrefix = word.possessivePrefix;
            reduplicationType = word.reduplicationType;
            prefixes = new ArrayList(word.prefixes);
            root = word.root;
            suffix = word.suffix;
            particleSuffix = word.particleSuffix;
            possessiveSuffix = word.possessiveSuffix;
            isChanged = false;
        }
    }
}