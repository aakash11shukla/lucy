package com.siddhantkushwaha.lucy;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Searcher {

    private static final String INDEX_DIR = "indexedFiles";

    public static void main(String[] args) throws Exception
    {

        String query = null;
        try {
            query = args[0];
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }

        if(query == null)
            query = "delhi";

        IndexSearcher searcher = createSearcher();

        TopDocs foundDocs = searchInContent(query, searcher);

        System.out.println("Total Results :: " + foundDocs.totalHits);

        for (ScoreDoc sd : foundDocs.scoreDocs)
        {
            Document d = searcher.doc(sd.doc);
            System.out.println("Path : "+ d.get("path") + ", Score : " + sd.score);
        }
    }

    private static TopDocs searchInContent(String textToFind, IndexSearcher searcher) throws Exception
    {
        QueryParser qp = new QueryParser("name", new StandardAnalyzer());
        Query query = qp.parse(textToFind);

        TopDocs hits = searcher.search(query, 20);

        return hits;
    }

    private static IndexSearcher createSearcher() throws IOException
    {
        Directory dir = FSDirectory.open(Paths.get(INDEX_DIR));

        IndexReader reader = DirectoryReader.open(dir);

        IndexSearcher searcher = new IndexSearcher(reader);
        return searcher;
    }
}
