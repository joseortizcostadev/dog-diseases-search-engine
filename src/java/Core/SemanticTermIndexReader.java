/**
 * @author       Jose Ortiz Costa
 * File:         SemanticTermIndexReader.java
 * Date:         04/20/2017
 * Description:  Reads from term semantic index and performs evaluations
 * Important: See also QueryExpansion class
 *
 */
package Core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SemanticTermIndexReader {

    String path;
    String root_term;

    /**
     * Class Constructor
     *
     * @param index_path
     * @param root_term
     */
    public SemanticTermIndexReader(String index_path, String root_term) {
        this.path = index_path;
        this.root_term = root_term;

    }

    /**
     * Gets all the docs from a dir
     *
     * @param folder
     * @return all the docs
     */
    public String[] getDocs(final File folder) {
        String[] filesNames = new String[folder.listFiles().length];
        int index = 0;
        for (final File fileEntry : folder.listFiles()) {

            filesNames[index] = fileEntry.getName();
            index++;

        }
        return filesNames;
    }

    /**
     * Gets the invertex index term which has the original query as a root
     *
     * @return the ivertex index
     * @throws IOException
     */
    public String getRootTermIndex() throws IOException {

        try (BufferedReader br = new BufferedReader(new FileReader(this.path))) {
            for (String line; (line = br.readLine()) != null;) {
                String root = line.split("=")[0];
                if (root.equals(this.root_term)) {
                    return line;
                }
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(SemanticTermIndexReader.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;

    }

    /**
     * Gets the most relevant semantic terms from the index
     *
     * @return relevant semantic terms
     * @throws IOException
     */
    public String[] getMostSemanticRelevantTerms() throws IOException {
        //insulin=[term1,1,2,3];[term2,1,2,1];

        String termIndex = getRootTermIndex();
        String[] termIndexParts = termIndex.split("=");
        String term = termIndexParts[0];
        String[] topTermStructs = termIndexParts[1].split(";");
        String[] topSemanticTerms = new String[topTermStructs.length];
        int semanticIndex = 0;
        for (int i = 0; i < topTermStructs.length; i++) {
            String tmp_struct = topTermStructs[i].replace("]", "");
            tmp_struct = tmp_struct.replace("[", "");
            String[] parts = tmp_struct.split(",");
            if (parts[1].trim().equals("1")) {
                topSemanticTerms[semanticIndex] = parts[0];
            }
            semanticIndex++;
        }
        return topSemanticTerms;
    }

    /**
     * Gets all the index from all the queries listed on it
     *
     * @return the semantic index
     * @throws IOException
     */
    public ArrayList<String> indexContent() throws IOException {
        ArrayList<String> content = new ArrayList();
        try (BufferedReader br = new BufferedReader(new FileReader(this.path))) {
            for (String line; (line = br.readLine()) != null;) {
                content.add(line);
            }
            return content;

        } catch (FileNotFoundException ex) {
            Logger.getLogger(SemanticTermIndexReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return content;
    }

}
