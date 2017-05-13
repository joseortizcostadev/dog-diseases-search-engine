/**
 * @author       Jose Ortiz Costa
 * File:         Documents.java
 * Date:         04/20/2017
 * Description:  This class creates a index from a given directory of documents
 *               This index can be used to search terms in a document or documents
 * Usage:        Documents.buildIndex(<path_to_documents_dir>, <path_to_save_index>,
 * <true for update or false for create>) Important: See also class Queries.
 *
 */
package Core;

// Lucene libraries imports
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Documents {

    boolean update; // will set the update or create flag
    File documentsDir;
    File indexPath;

    /**
     * Private class constructor Important: This constructor is not accessible
     * from outside this class
     *
     * @param _pathToDocuments
     * @param _pathToSaveIndex
     * @param _update
     */
    private Documents(String _pathToDocuments, String _pathToSaveIndex, boolean _update) {
        this.update = _update;
        this.documentsDir = new File(_pathToDocuments);
        this.indexPath = new File(_pathToSaveIndex);
    }

    /**
     * Makes sure that there are not errors in the paths
     */
    private void setDocumentsPath() {
        if (!this.documentsDir.exists() || !this.documentsDir.canRead()) {
            System.out.println("Document directory '"
                    + this.documentsDir.getAbsolutePath()
                    + "Your path don't exist, or needs read permissions");
            System.exit(1);
        }
    }

    /**
     * The indexWritter to write add documents terms to the index
     *
     * @param luceneVersion the actual Lucene version ( use Version constant)
     * @return the IndexWriter Object
     */
    private IndexWriter getIndexWriter(Version luceneVersion) {
        try {
            // Creates analylizer and configure the index writer
            Analyzer analyzer = new StandardAnalyzer(luceneVersion);
            IndexWriterConfig indexWriterConfig = new IndexWriterConfig(luceneVersion, analyzer);
            if (this.update) {
                // will update existing index
                indexWriterConfig.setOpenMode(OpenMode.APPEND);
            } else {
                // will write in a new index
                indexWriterConfig.setOpenMode(OpenMode.CREATE);
            }
            // sets the path to save the index
            Directory indexPathDir = FSDirectory.open(this.indexPath);
            return new IndexWriter(indexPathDir, indexWriterConfig);
        } catch (IOException ex) {
            System.err.println("Your path to save the index does not exist "
                    + "or you don't have the correspondent permissions");
        }
        return null; // if reached, the index writer won't be returned.
    }

    /**
     * Build the index
     *
     * @return true if the index was successfully built. Otherwise, returns
     * false
     */
    private boolean build() {
        try {
            IndexWriter indexWriter = getIndexWriter(Version.LUCENE_46);
            // add docs to the index.
            indexDocs(indexWriter, this.documentsDir);
            indexWriter.close();
            return true; // index was build
        } catch (IOException ex) {
            System.err.println("Error: Index build failed with error: "
                    + ex.getMessage());
        }
        return false; // index was not build
    }

    /**
     * Helper method to add a document to the index
     *
     * @param indexWriter the IndexWriterObject
     * @param file the document
     */
    private void addDoc(IndexWriter indexWriter, File file) {
        try {
            FileInputStream fis;
            try {
                fis = new FileInputStream(file);
            } catch (FileNotFoundException fnfe) {
                return;
            }
            Document doc = new Document();
            Field pathField = new StringField("path", file.getPath(), Field.Store.YES);
            doc.add(pathField);
            doc.add(new LongField("modified", file.lastModified(), Field.Store.NO));
            doc.add(new TextField("contents", new BufferedReader(new InputStreamReader(fis, "UTF-8"))));
            if (indexWriter.getConfig().getOpenMode() == OpenMode.CREATE) {
                // New index, so we just add the document (no old document can be there):
                System.out.println("adding " + file);
                indexWriter.addDocument(doc);
            } else {
                try {
                    // Existing index (an old copy of this document may have been indexed) so
                    // we use updateDocument instead to replace the old one matching the exact
                    // path, if present:
                    System.out.println("updating " + file);
                    indexWriter.updateDocument(new Term("path", file.getPath()), doc);
                } catch (IOException ex) {
                    Logger.getLogger(Documents.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Documents.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Documents.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Add a document to the index
     *
     * @param writer the indexWriter object
     * @param file the document to be added to the index
     * @throws IOException
     */
    private void indexDocs(IndexWriter writer, File file) throws IOException {
        // Makes sure that documents are valid ones and can be read
        if (file.canRead()) {
            if (file.isDirectory()) {
                String[] files = file.list();
                // an IO error could occur
                if (files != null) {
                    for (int i = 0; i < files.length; i++) {
                        indexDocs(writer, new File(file, files[i]));
                    }
                }
            } else {
                addDoc(writer, file);
            }
        }

    }

    /**
     * static method to create an object of this class and build the index
     *
     * @param pathToDocuments
     * @param pathToSaveIndex
     * @param update
     * @return true if the index was successfully build. Otherwise, returns
     * false
     */
    public static boolean buildIndex(String pathToDocuments, String pathToSaveIndex, boolean update) {
        Documents index = new Documents(pathToDocuments, pathToSaveIndex, update);
        index.setDocumentsPath();
        return index.build();

    }
}
