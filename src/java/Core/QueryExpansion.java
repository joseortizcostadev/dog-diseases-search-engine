package Core;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.lemurproject.kstem.KrovetzStemmer;

/**
 * @author Jose Ortiz Costa File: QueryExpansion.java Date: 04/20/2017
 * Description: This class expands a query in order to retrieve more relevant
 * Documents. According to the option passes, this class can expand a query by
 * term score or by semantic relationship relevance documents. Important: See
 * also class Queries.
 *
 */
public class QueryExpansion {

    Map<String, Integer> indexTF;
    String queryToBeExpanded;
    String document;
    String semanticIndexPath = "";
    int[] tf;
    boolean isSemanticIndex = false;
    static String[] wordRemovalList = {"the", "is", "at", "of", "on", "and",
        "a", "in", "to", "for", "that",
        "email", "password", "your", "not",
        "this", "their", "those", "you",
        "center", "sign", "account", "with",
        "or", "more", "pet", "dog", "read",
        "can", "be", "dogs", "were", "case", "was",
        "1", "view", "2", "fig", "disease", "cat",
        "petmd", "have", "tool", "verify", "may", "address",
        "bg", "scholar", "0", "articlegoogle", "by", "j", "pubmedview", "as", "3"};

    /**
     * Class Constructor
     *
     * @param queryToBeExpanded
     * @param mostRelevantDocumentPath
     * @param maxNumberTerms
     * @param isSemanticIndex
     * @throws IOException
     */
    public QueryExpansion(String queryToBeExpanded, String mostRelevantDocumentPath, int maxNumberTerms, boolean isSemanticIndex) throws IOException {
        this.queryToBeExpanded = queryToBeExpanded;
        this.document = FileUtils.readFileToString(new File(mostRelevantDocumentPath), "UTF-8");
        this.tf = new int[maxNumberTerms];
        this.isSemanticIndex = isSemanticIndex;
        indexTF = new HashMap<String, Integer>();
        createTFIndex();
    }

    /**
     * Determine and set the path to the semantic index
     *
     * @param semanticIndex
     */
    public void setSemanticIndexPath(String semanticIndex) {
        this.semanticIndexPath = semanticIndex;
    }

    public boolean isRemovalWord(String term) {
        for (String word : QueryExpansion.wordRemovalList) {
            if (term.equals(word)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Creates the tf score index
     */
    public void createTFIndex() {
        /*use the Krovetz stemmer to stem word*/
        KrovetzStemmer stemmer = new KrovetzStemmer();
        String docText = Jsoup.parse(this.document).text();
        String[] terms = docText.toLowerCase().split("[^\\w']+");
        for (String term : terms) {
            term = stemmer.stem(term);
            if (!isRemovalWord(term)) {
                if (indexTF.containsKey(term)) {
                    int tf = indexTF.get(term);
                    tf++;
                    indexTF.put(term, tf);
                } else {
                    indexTF.put(term, 1);
                }
            }

        }

    }

    /**
     * Sorts a map
     *
     * @param <K>
     * @param <V>
     * @param map
     * @return
     */
    static public <K, V extends Comparable<? super V>>
            SortedSet<Map.Entry<K, V>> entriesSortedByValues(Map<K, V> map) {
        SortedSet<Map.Entry<K, V>> sortedEntries = new TreeSet<Map.Entry<K, V>>(
                new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> e1, Map.Entry<K, V> e2) {
                int res = e2.getValue().compareTo(e1.getValue());
                return res != 0 ? res : 1;
            }
        }
        );
        sortedEntries.addAll(map.entrySet());
        return sortedEntries;
    }

    /**
     * Get top terms from most relevant document
     *
     * @param maxNumTopTerms
     * @return
     */
    public String[] getTopTerms(int maxNumTopTerms) {
        String[] topTerms = new String[maxNumTopTerms];
        int tf_index = 0;
        for (Entry<String, Integer> entry : entriesSortedByValues(this.indexTF)) {
            if (tf_index == maxNumTopTerms) {
                break;
            }
            topTerms[tf_index] = entry.getKey();
            this.tf[tf_index] = entry.getValue();
            tf_index++;
        }
        return topTerms;
    }

    public int[] getTermsFrequency() {
        return this.tf;
    }

    public int getTermFrequency() throws IOException {
        String tmp_query = this.queryToBeExpanded;
        String[] queryParts = tmp_query.split(" ");
        int tf = 0;
        for (String query : queryParts) {
            if (indexTF.containsKey(query)) {
                tf += indexTF.get(query);
            }
        }
        return tf;
    }

    public String topTermsToString(int numMaxTopTerms) {
        String top = "";
        int index = 0;
        for (String term : getTopTerms(numMaxTopTerms)) {
            top += term + ":" + this.tf[index] + "   ";
            index++;
        }
        return top;

    }

    /**
     * Gets the query expanded either by term score or by semantic relationship
     *
     * @param numTopTerms
     * @return a string representing the query expansion
     * @throws IOException
     */
    public String getQueryExpanded(int numTopTerms) throws IOException {
        String expandedQuery = this.queryToBeExpanded + " ";
        if (this.isSemanticIndex) {
            SemanticTermIndexReader semanticReader = new SemanticTermIndexReader(this.semanticIndexPath, this.queryToBeExpanded);
            String[] topTerms = semanticReader.getMostSemanticRelevantTerms();
            int semanticIndex = 0;
            for (String term : topTerms) {
                if (semanticIndex == numTopTerms) {
                    break;
                }
                if (term != null) {
                    expandedQuery += term + " ";
                    semanticIndex++;
                }
            }
        } else {
            String[] topTerms = getTopTerms(numTopTerms);

            for (int i = 0; i < this.tf.length; i++) {
                if (!expandedQuery.contains(topTerms[i])) {
                    expandedQuery += topTerms[i] + " ";
                }
            }
        }
        return expandedQuery;
    }

}
