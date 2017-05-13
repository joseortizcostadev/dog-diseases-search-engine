/**
 * @author       Jose Ortiz Costa
 * File:         SearchEngine.java
 * Date:         04/20/2017
 * Description:  This class performs a query search into a index to match
 *               documents and terms. It has options to provide the documents
 *               matched, terms matched, ranking, scores....etc.
 *               In addition, it has an option ( disabled by default ) that
 *               provides automatize evaluation report using trec_eval program
 *               for a query or group of queries.
 * USAGE:
 *
 * Important:    See also Documents class
 *
 */
package Core;

// Java libraries
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
// Lucene libraires
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;

public class SearchEngine {

    File indexPath; // the path to the index
    String field; // the field to look into e.g title
    String queriesPath; // the path to the dir that contains all the queries files
    String query; // a single query string
    int hitsPerPage;
    String internal_path;

    /**
     * Class constructor
     *
     * @param indexPath the path to the index
     * @param internal_path
     */
    public SearchEngine(String indexPath) {
        this.indexPath = new File(indexPath);
        this.field = "contents"; // by default contents
        this.queriesPath = null;
        this.query = null;
        hitsPerPage = 5; // default value of hits per page

    }

    /*
       Setters and getters. 
       No needed to comment since function names are fully meanfull
     */
    public void setMaxHitsPerPage(int hitsPerPage) {
        this.hitsPerPage = hitsPerPage;
    }

    /**
     * Search the index to match documents containing the query
     *
     * @param query
     * @return the results of the query such as score, doc_path, url, doc_title,
     * ranking....etc.
     */
    public DocumentMatched[] searchQuery(String query) {
        DocumentMatched[] results = null;
        try {
            IndexReader reader = DirectoryReader.open(FSDirectory.open(this.indexPath));
            IndexSearcher searcher = new IndexSearcher(reader);
            Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_46);
            QueryParser parser = new QueryParser(Version.LUCENE_46, field, analyzer);
            // Apply streemer and other prepocessing task to the query
            Query queryObj = parser.parse(query);
            // get the results of the search
            results = doPagingSearch(searcher, queryObj, hitsPerPage);
            reader.close();
            return results;
        } catch (IOException ex) {
            System.err.println("Error: Index has been corrupted. Please, re-create it");
        } catch (ParseException ex) {
            System.err.println("Error: Index has been corrupted. Please, re-create it");
        }
        return results; // if reach, some error ocurred.
    }

    /**
     * Does paging search using Lucene API.
     *
     * @param searcher
     * @param query
     * @param hitsPerPage the number of documents needed to show per page
     * @return a list of objects DocumentMatched containing all the data from
     * the search
     * @throws IOException
     */
    public DocumentMatched[] doPagingSearch(IndexSearcher searcher, Query query,
            int hitsPerPage) throws IOException {
        // Collect all relevant documents
        TopDocs results = searcher.search(query, 5 * hitsPerPage);
        ScoreDoc[] hits = results.scoreDocs; // get most relevant scored docs
        int numTotalHits = results.totalHits; // total number of matches
        if (numTotalHits == 0) {
            return null; // no matches found
        } else {
            hits = searcher.search(query, numTotalHits).scoreDocs; // matches found
        }
        DocumentMatched[] resultsData = new DocumentMatched[hits.length]; // save results list
        int ranking = 0;
        // iterate over all the score docs and save data from results
        for (ScoreDoc hit : hits) {
            Document doc = searcher.doc(hit.doc);
            resultsData[ranking] = new DocumentMatched(hit, doc, ranking + 1);
            ranking++;
        }
        return resultsData;
    }

    public ArrayList<String> getTerms() throws IOException {
        ArrayList<String> t = new ArrayList();
        IndexReader reader = DirectoryReader.open(FSDirectory.open(this.indexPath));
        Fields fields = MultiFields.getFields(reader);
        Terms terms = fields.terms("field");
        TermsEnum iterator = terms.iterator(null);
        BytesRef byteRef = null;
        while ((byteRef = iterator.next()) != null) {
            String term = byteRef.utf8ToString();
            t.add(term);
        }
        return t;
    }

}
