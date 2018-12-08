package com.siddhantkushwaha.lucy;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.LatLonDocValuesField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
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
            query = "city of joy";

        IndexSearcher searcher = createSearcher();

        long startTime = System.nanoTime();

        TopDocs foundDocs = searchInDocuments(query, searcher);

//        long endTime = System.nanoTime();
//        long totalTime = endTime - startTime;

        ArrayList<Place> result = new ArrayList<>();
        for (ScoreDoc sd : foundDocs.scoreDocs) {

            Document d = searcher.doc(sd.doc);

            Place place = new Place();
            place.setName(d.get("name"));
            place.setDescription(d.get("abstract"));
            place.setCountry(d.get("country"));

            result.add(place);
        }
        String finalResult = CommonUtils.toJson(result);
        System.out.println(finalResult);
    }

    private static IndexSearcher createSearcher() throws IOException {
        Directory dir = FSDirectory.open(Paths.get(Writer.indexPath));

        IndexReader reader = DirectoryReader.open(dir);

        return new IndexSearcher(reader);
    }

    private static TopDocs searchInDocuments(String textToFind, IndexSearcher searcher) throws Exception {

        BooleanQuery.Builder booleanQueryBuilder = new BooleanQuery.Builder();
        Sort sort = new Sort();

        QueryParser qp = new QueryParser(Writer.PLACE_KEY_ABSTRACT, new StandardAnalyzer());
        Query phraseQuery = qp.createPhraseQuery(Writer.PLACE_KEY_ABSTRACT, textToFind, 2);
//        System.out.println(phraseQuery);
        booleanQueryBuilder.add(phraseQuery, BooleanClause.Occur.SHOULD);

        Query query = qp.parse(textToFind);
//        System.out.println(orQuery);
        booleanQueryBuilder.add(query, BooleanClause.Occur.FILTER);

        Double[] latlng = {13.080000, 80.270000};
        Integer radiusInMeters = 100000;
        Query latLngQuery = LatLonDocValuesField.newSlowDistanceQuery(Writer.PLACE_KEY_LATLNG, latlng[0], latlng[1], radiusInMeters);
        SortField locationSortField = LatLonDocValuesField.newDistanceSort(Writer.PLACE_KEY_LATLNG, latlng[0], latlng[1]);
        booleanQueryBuilder.add(latLngQuery, BooleanClause.Occur.SHOULD);
        sort.setSort(locationSortField);

        BooleanQuery finalQuery = booleanQueryBuilder.build();
//        System.out.println(finalQuery);
        return searcher.search(finalQuery, 20, sort);
    }
}
