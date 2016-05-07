package Model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author susanti_2
 */
public class Csv2Arff {

    private static final String COMMA = ",";
    private static final String QUOTATION = "'";
    private static final String DELIMITER = ";";
    private static final String NEW_LINE_SEPARATOR = "\n";
    private static final String RELATION_NAME = "NBCDATA";
    private static final String FORMALIZED_SENTENCE_STRING = "formalized_sentence";
    private static final String CLASS_STRING = "class";
    private static final int FORMALIZED_SENTENCE_INDEX = 0;

    /**
     * convert from output preprocess NBC for testing (csv format) to arff format
     * @param inputCSV input filename
     * @param outputARFF output filename
     * @throws FileNotFoundException
     */
    public static void convert(String inputCSV, String outputARFF) throws FileNotFoundException {
        BufferedReader fileReader = new BufferedReader(new FileReader(inputCSV));

        try {
            fileReader.readLine(); //read separator
            String header = fileReader.readLine(); //read header
            String[] headers = header.split(DELIMITER);

            String line;
            ArrayList<ArrayList<String>> instances = new ArrayList<>();

            while ((line = fileReader.readLine()) != null) {
                //Get all tokens available in line
                String[] instance = line.split(DELIMITER);
                assert instance.length == headers.length;
                ArrayList<String> i = new ArrayList<>(Arrays.asList(instance));
                instances.add(i);
            }

            //ARFF
            File file = new File(outputARFF);

            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file);
            try (BufferedWriter bw = new BufferedWriter(fw)) {
                bw.write("@relation " + RELATION_NAME + NEW_LINE_SEPARATOR + NEW_LINE_SEPARATOR);

                for (String s : headers) {
                    bw.write("@attribute " + s);
                    if (s.equals(FORMALIZED_SENTENCE_STRING)) {
                        bw.write(" string" + NEW_LINE_SEPARATOR);
                    } else if (s.equals(CLASS_STRING)) {
                        bw.write(" {s,o}" + NEW_LINE_SEPARATOR);
                    } else {
                        bw.write(" {0,1}" + NEW_LINE_SEPARATOR);
                    }

                }

                bw.write(NEW_LINE_SEPARATOR + "@data" + NEW_LINE_SEPARATOR);

                for (ArrayList<String> inst : instances) {
                    for (int i = 0; i < inst.size(); i++) {
                        if (i == FORMALIZED_SENTENCE_INDEX) {
                            bw.write(QUOTATION + inst.get(i) + QUOTATION + COMMA);
                        } else if (i == (inst.size() - 1)) {
                            bw.write(inst.get(i) + NEW_LINE_SEPARATOR);
                        } else {
                            bw.write(inst.get(i) + COMMA);
                        }
                    }
                }
            }
        } catch (IOException ex) {
            System.out.println("IO Exception: convert Exception");
        }
    }
}
