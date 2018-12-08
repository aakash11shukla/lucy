package com.siddhantkushwaha.lucy;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

import com.google.gson.JsonObject;
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
            // System.out.println(query);
        } catch (Exception e) {
            // Nothing
        }

        if (query == null)
            return;

        JsonObject queryObj = CommonUtils.fromJson(query, JsonObject.class);

        IndexSearcher searcher = createSearcher();

        // long startTime = System.nanoTime();

        TopDocs foundDocs = searchInDocuments(queryObj, searcher);

        // long endTime = System.nanoTime();
        // long totalTime = endTime - startTime;

        ArrayList<Place> result = new ArrayList<>();
        for (ScoreDoc sd : foundDocs.scoreDocs) {

            Document d = searcher.doc(sd.doc);

            Place place = new Place();
            place.setName(d.get(Writer.PLACE_KEY_NAME));
            place.setDescription(d.get(Writer.PLACE_KEY_ABSTRACT));
            place.setCountry(d.get(Writer.PLACE_KEY_COUNTRY));

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

    private static TopDocs searchInDocuments(JsonObject queryObj, IndexSearcher searcher) throws Exception {

        // System.out.println(queryObj);

        BooleanQuery.Builder booleanQueryBuilder = new BooleanQuery.Builder();
        Sort sort = new Sort();

        if (queryObj.get(Writer.PLACE_KEY_LATLNG) != null) {
            try {
                String tt = queryObj.get(Writer.PLACE_KEY_LATLNG).getAsString();
                Double[] latlng = Writer.getLatLngs(tt);

                Integer radiusInMeters = 100000;
                Query latLngQuery = LatLonDocValuesField.newSlowDistanceQuery(Writer.PLACE_KEY_LATLNG, latlng[0], latlng[1], radiusInMeters);
                // System.out.println(latLngQuery);
                SortField locationSortField = LatLonDocValuesField.newDistanceSort(Writer.PLACE_KEY_LATLNG, latlng[0], latlng[1]);

                booleanQueryBuilder.add(latLngQuery, BooleanClause.Occur.SHOULD);
                sort.setSort(locationSortField);
            } catch (Exception e) {
                // pass
            }
        }

        if (queryObj.get(Writer.PLACE_KEY_COUNTRY) != null) {
            try {
                String textToFind = queryObj.get(Writer.PLACE_KEY_COUNTRY).getAsString();

                QueryParser qp = new QueryParser(Writer.PLACE_KEY_COUNTRY, new StandardAnalyzer());
                Query query = qp.parse(textToFind);
                booleanQueryBuilder.add(query, BooleanClause.Occur.MUST);
            } catch (Exception e) {
                // pass
            }
        }

        if (queryObj.get(Writer.PLACE_KEY_NAME) != null) {
            try {
                String textToFind = queryObj.get(Writer.PLACE_KEY_NAME).getAsString();

                QueryParser qp = new QueryParser(Writer.PLACE_KEY_NAME, new StandardAnalyzer());
                Query query = qp.parse(textToFind);
                booleanQueryBuilder.add(query, BooleanClause.Occur.SHOULD);
            } catch (Exception e) {
                // pass
            }

        }

        if (queryObj.get(Writer.PLACE_KEY_ABSTRACT) != null) {
            try {
                String textToFind = queryObj.get(Writer.PLACE_KEY_ABSTRACT).getAsString();

                QueryParser qp = new QueryParser(Writer.PLACE_KEY_ABSTRACT, new StandardAnalyzer());
                Query phraseQuery = qp.createPhraseQuery(Writer.PLACE_KEY_ABSTRACT, textToFind, 2);
                // System.out.println(phraseQuery);

                booleanQueryBuilder.add(phraseQuery, BooleanClause.Occur.SHOULD);

                Query query = qp.parse(textToFind);
                // System.out.println(orQuery);

                booleanQueryBuilder.add(query, BooleanClause.Occur.FILTER);
            } catch (Exception e) {
                // pass
            }
        }

        BooleanQuery finalQuery = booleanQueryBuilder.build();
        // System.out.println(finalQuery);
        return searcher.search(finalQuery, 20, sort);
    }
}
