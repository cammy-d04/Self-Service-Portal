package model;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;

import java.io.IOException;
import java.util.*;

public class PageSearch {
    private StandardAnalyzer analyzer = new StandardAnalyzer();
    private Directory index = new ByteBuffersDirectory();
    private Collection<Page> pageList;

    /**
     * Creates a PageSearch given a list of pages
     * @param pageList list of pages to index
     * @throws IOException
     */
    public PageSearch(Collection<Page> pageList) throws IOException {
        assert pageList != null;
        this.pageList = pageList;
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter w = new IndexWriter(index, config);
        for (Page page: pageList){
            assert page != null;
            addDoc(w, page.getTitle(), page.getContent());
        }
        w.close();
    }

    /**
     * Adds a document to the index
     * @param indexWriter IndexWriter to write to
     * @param title title of page
     * @param content content of page
     * @throws IOException required for lucene
     */
    private void addDoc(IndexWriter indexWriter, String title, String content) throws IOException {
        assert title != null;
        assert content != null;
        assert !title.isEmpty();
        assert !content.isEmpty();
        //This assumes that paragraphs are split on double newlines.
        String[] paragraphs = content.split("\n\n");

        for (String paragraph: paragraphs){
            Document doc = new Document();
            //adds a paragraph from a document, and the title of the
            // document, to the index. title is not tokenized, since it
            // shouldn't searched on.
            doc.add(new StringField("title", title, Field.Store.YES));
            doc.add(new TextField("paragraph", paragraph, Field.Store.YES));
            indexWriter.addDocument(doc);
        }
    }

    /**
     * Searches the index given a user inputted query.
     * @param query user query
     * @return List of length <= 4 of search results
     * @throws ParseException Required for Lucene
     * @throws IOException Required for Lucene
     */
    public List<PageSearchResult> search(String query) throws ParseException, IOException {
        List<PageSearchResult> results = new ArrayList<>();
        Query q = new QueryParser("paragraph", analyzer).parse(query);

        int numHits = 4;
        IndexReader reader = DirectoryReader.open(index);
        IndexSearcher searcher = new IndexSearcher(reader);

        //searches for the top 4 paragraphs
        TopDocs docs = searcher.search(q,numHits);
        //adds the returned hits
        ScoreDoc[] hits = docs.scoreDocs;
        for (ScoreDoc hit : hits) {
            int docNum = hit.doc;
            Document doc = searcher.doc(docNum);
            results.add(new PageSearchResult(doc.get("title") + "\n\n" + doc.get(
                    "paragraph")));
        }
        return results;
    }
}
