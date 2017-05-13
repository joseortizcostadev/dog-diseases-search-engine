/**
 * @author       Jose Ortiz Costa
 * File:         NaiveBayesClassifier.java
 * Date:         04/20/2017
 * Description:  Retrieve best probability terms from the most relevant document
 *               using a  modified version of Naive Bayes algorithm
 * Important: See also Documents class
 *
 */
package Core;

import static Core.QueryExpansion.entriesSortedByValues;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.jsoup.Jsoup;
import org.lemurproject.kstem.KrovetzStemmer;

public class NaiveBayesClassifier {

    String[] docs;
    int[] matches;
    static String[] wordRemovalList = {"the", "is", "at", "of", "on", "and",
        "a", "in", "to", "for", "that",
        "email", "password", "your", "not",
        "this", "their", "those", "you",
        "center", "sign", "account", "with",
        "or", "more", "pet", "dogs", "read"};
    String query;
    double probYes = 0.00;
    double probNo = 0.00;
    int matchesYes = 0;
    int matchesNo = 0;
    Map<Integer, Double> docsProb = new HashMap();
    /**
     * Constructor
     * @param dir
     * @param query 
     */
    public NaiveBayesClassifier(String dir, String query) {
        this.docs = getDocs(new File(dir));
        this.query = query;
        this.matches = new int[this.docs.length];
        computePcProbs();
    }
    /**
     * Gets all the docs from a dir
     * @param folder
     * @return all the docs names
     */
    public String [] getDocs(final File folder) {
        String [] filesNames = new String[folder.listFiles().length];
        int index = 0;
        for (final File fileEntry : folder.listFiles()) {
           
                filesNames[index] = fileEntry.getName();
                index++;
            
        }
        return filesNames;
    }
    
    /**
     * Gets all terms from a doc after preprocessing them.
     * @return the doc terms
     */
    public String[] getDocTerms() {

        String[] docTerms = new String[this.docs.length];
        int index = 0;
        for (String path : this.docs) {
            /*use the Krovetz stemmer to stem word*/
            KrovetzStemmer stemmer = new KrovetzStemmer();
            String terms = Jsoup.parse(path).text();
            docTerms[index] = stemmer.stem(terms);
            index++;
        }
        return docTerms;
    }
    /**
     * Creates a map to match terms and its classes conditions
     * @param docTerms
     * @param computingClassIndex
     * @return 
     */
    public Map<String, Integer> createTermsMap(String[] docTerms, int computingClassIndex) {
        Map<String, Integer> termsMatcher = new HashMap();
        for (int i = 0; i < docTerms.length; i++) {
            if (i != computingClassIndex) {
                String[] terms = docTerms[i].toLowerCase().split("[^\\w']+");
                for (int j = 0; j < terms.length; j++) {
                    if (termsMatcher.containsKey(terms[j])) {
                        int count = termsMatcher.get(terms[j]);
                        count++;
                        termsMatcher.put(terms[j] + this.matches[i], count);
                    } else {
                        termsMatcher.put(terms[j] + this.matches[i], 1);
                    }
                }
            }
        }
        return termsMatcher;
    }
    
    /**
     * Build the terms map where all the terms have its own space
     * @param computingClassIndex
     * @return 
     */
    public Map<String, Integer> createTermsMap(int computingClassIndex) {
        Map<String, Integer> termsMatcher = new HashMap();
        String[] tmp_terms = getDocTerms();
        String termsClass = tmp_terms[computingClassIndex];

        String[] terms = termsClass.toLowerCase().split("[^\\w']+");
        for (int j = 0; j < terms.length; j++) {

            if (termsMatcher.containsKey(terms[j])) {
                int count = termsMatcher.get(terms[j]);
                count++;
                termsMatcher.put(terms[j] + this.matches[computingClassIndex], count);
            } else {
                termsMatcher.put(terms[j] + this.matches[computingClassIndex], 1);
            }
        }

        return termsMatcher;
    }
   
    /**
     * Computes probability for class label
     */
    public void computePcProbs() {
        int totalDocs, prob, yes = 0, no = 0;
        String[] docTerms = getDocTerms();
        int index = 0;
        for (String terms : docTerms) {
            if (terms.contains(query)) {
                yes++;
                this.matches[index] = 1;
            } else {
                no++;
                this.matches[index] = 0;
            }
            index++;
        }
        totalDocs = this.docs.length;
        this.probYes = (yes / totalDocs);
        this.probNo = (no / totalDocs);
    }
    /**
     * Computes document probability
     * @param docIndex
     * @return  the probability of the document
     */
    public double docProbability(int docIndex) {
        double prob = 0.00;
        this.docsProb.put(docIndex, 0.00);
        double probYes = this.probYes;
        double probNo = this.probNo;
        int termInYesNotDocIndex = 0;
        int termInNoNotDocIndex = 0;
        int termYesTotal = 0;
        int termNoTotal = 0;
        Map<String, Integer> docPositionalIndex = createTermsMap(docIndex);
        Map<String, Integer> allDocsPositionalIndex = createTermsMap(this.getDocTerms(), docIndex);
        for (Map.Entry<String, Integer> entry : docPositionalIndex.entrySet()) {
            String term = entry.getKey();
            int tf = entry.getValue();
            termInYesNotDocIndex = allDocsPositionalIndex.containsKey(term + "1") ? allDocsPositionalIndex.get(term + "1") + 1 : 1;
            termInNoNotDocIndex = allDocsPositionalIndex.containsKey(term + "0") ? allDocsPositionalIndex.get(term + "0") + 1 : 1;
            termYesTotal = (termInYesNotDocIndex - 1) + tf + (termInYesNotDocIndex - 1) + (termInNoNotDocIndex - 1);
            termNoTotal = (termInNoNotDocIndex - 1) + tf + (termInYesNotDocIndex - 1) + (termInNoNotDocIndex - 1);
            probYes *= (termInYesNotDocIndex / termYesTotal) * tf;
            probNo *= (termInNoNotDocIndex / termNoTotal) * tf;

        }
        return (probYes > probNo ? probYes : probNo);

    }
    
    
    /**
     * Gets the document with more probabilities of all the matches documents
     * @param maxNumTopDocuments
     * @return 
     */
    public Map<Integer, Double> getTopClassifiedDocId(int maxNumTopDocuments) {
        String[] terms = getDocTerms();
        for (int i = 0; i < terms.length; i++) {
            if (i == maxNumTopDocuments) {
                break;
            }
            this.docsProb.put(i, docProbability(i));
        }

        for (Map.Entry<Integer, Double> entry : entriesSortedByValues(this.docsProb)) {
            // this loop is for ordering values only. Nothing to do inside.
        }
        return this.docsProb;

    }

}
