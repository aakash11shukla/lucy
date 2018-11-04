package com.siddhantkushwaha.lucy;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Searcher {

    public static void main(String[] args) throws Exception {

        String query = null;
        try {
            query = args[0];
        } catch (Exception e) {
            // Nothing
        }

        if (query == null)
            query = "cultural capital of india";

        IndexSearcher searcher = createSearcher();

        long startTime = System.nanoTime();

        TopDocs foundDocs = searchInDocuments(query, searcher);

        long endTime = System.nanoTime();
        long totalTime = endTime - startTime;

        System.out.printf("Total Results: %s, time taken: %f\n\n", foundDocs.totalHits, totalTime / (1e9));

        for (ScoreDoc sd : foundDocs.scoreDocs) {
            Document d = searcher.doc(sd.doc);
//            System.out.println("Path : " + d.get("path") + ", Score : " + sd.score);
            System.out.printf("%-30s %-30f %s\n", d.get("name"), sd.score, d.get("abstract"));
        }
    }

    private static IndexSearcher createSearcher() throws IOException {
        Directory dir = FSDirectory.open(Paths.get(Writer.indexPath));

        IndexReader reader = DirectoryReader.open(dir);

        return new IndexSearcher(reader);
    }

    private static TopDocs searchInDocuments(String textToFind, IndexSearcher searcher) throws Exception {

        QueryParser qp = new QueryParser(Writer.PLACE_KEY_ABSTRACT, new StandardAnalyzer());
        qp.setDefaultOperator(QueryParser.Operator.AND);

        Query phraseQuery = qp.createPhraseQuery(Writer.PLACE_KEY_ABSTRACT, textToFind, 2);
        System.out.println(phraseQuery);

        Query query = qp.parse(textToFind);
        System.out.println(query);

        BooleanQuery.Builder booleanQueryBuilder = new BooleanQuery.Builder();
        booleanQueryBuilder.add(phraseQuery, BooleanClause.Occur.SHOULD);
        booleanQueryBuilder.add(query, BooleanClause.Occur.FILTER);

        BooleanQuery finalQuery = booleanQueryBuilder.build();
        System.out.println(finalQuery);

        TopDocs result = searcher.search(finalQuery, 200);
        return result;
    }
}
