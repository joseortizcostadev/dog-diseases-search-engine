/**
 * @author       Jose Ortiz Costa
 * File:         DocumentMatched.java
 * Date:         04/20/2017
 * Description:  After a query search, this class saves all the data results
 *               relevant to the query match for a single document matched.
 *               It will, for example, save the score, ranking, most relevant
 *               terms, URL, path and title for the matched document.
 * USAGE:        DoucumentMatched docMatched = new DocumentMatched (<ScoreHit>,<Document>,<int>);
 * double docScore = docMatched.getScore(); // gets the document score
 *
 * Important: See also Documents class
 *
 */
package Core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.ScoreDoc;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;

public class DocumentMatched {

    String documentPath;
    String documentTitle;
    String url;
    String description;
    boolean isRelevant;
    int termFrequency;
    int documentFrequency;
    double score;
    int ranking;
    ScoreDoc hit; // a representation of the score of the document
    Document doc; // the document object

    /**
     * Class constructor
     *
     * @param hit
     * @param doc
     * @param ranking
     */
    public DocumentMatched(ScoreDoc hit, Document doc, int ranking) {
        this.hit = hit;
        this.doc = doc;
        this.documentPath = doc.get("path");
        this.score = hit.score;
        this.ranking = ranking;
    }

    /**
     * Extract the document URL from the document metadata
     *
     * @param metadata the document metadata
     * @return the URL of the document
     */
    public String getUrl() {
        try (BufferedReader br = new BufferedReader(new FileReader(this.documentPath))) {
            String metadata;
            while ((metadata = br.readLine()) != null) {
                if (metadata.contains("href")) {
                    String[] urlParts = metadata.split("href=");
                    this.url = urlParts[1].replace("\"", "").trim();
                    this.url = this.url.replace("/>", "");
                    break;

                }
            }
            return this.url; // returns the url
        } catch (IOException ex) {
            System.err.println("Error: the metadata of the document does not "
                    + "contains the url of the document");
        }
        return "MALFORM_URL_EXCEPTION"; // not a valid url
    }

    /**
     * Retrieve the description of the document and assign it to the instance
     *
     * @return the document description assigned
     * @throws IOException
     */
    public String getDescription() throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(this.documentPath))) {
            String metadata;
            while ((metadata = br.readLine()) != null) {
                if (metadata.contains("description")) {
                    String[] urlParts = metadata.split("content");
                    this.description = urlParts[1].replace("\"", "").trim();
                    this.description = this.description.replace("/>", "");
                    this.description = this.description.substring(0, 50);
                    break;

                }
            }
            return this.description; // returns the url
        } catch (IOException ex) {
            System.err.println("Error: the metadata of the document does not "
                    + "contains the url of the document");
        }
        return "MALFORM_DESCRIPTION_EXCEPTION"; // not a valid url
    }

    /**
     * Getters, self explanatory names
     */
    public double getScore() {
        return this.score;
    }

    public String getDocumentPath() {
        return this.documentPath;
    }

    public String getDocumentTitle() {
        String[] pathParts = this.documentPath.split("/");
        String title = pathParts[pathParts.length - 1];
        return title.replace(".html", "");
    }

    public String getDocumentDescription() {
        return this.description;
    }

    public int getRanking() {
        return this.ranking;
    }

}
