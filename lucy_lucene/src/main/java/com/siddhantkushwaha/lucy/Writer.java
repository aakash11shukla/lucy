package com.siddhantkushwaha.lucy;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import com.google.gson.JsonObject;;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;


public class Writer {

    static String indexPath = "indexedFiles";

    static String PLACE_KEY_NAME = "name";
    static String PLACE_KEY_ABSTRACT = "abstract";
    static String PLACE_KEY_COUNTRY = "country";
    static String PLACE_KEY_POPULATION = "population";
    static String PLACE_KEY_IS_PART_OF = "isPartOf";
    static String PLACE_KEY_UTC_OFFSET = "utcOffset";
    static String PLACE_KEY_LATLNG = "latlng";

    public static void main(String[] args) {

        String docsPath = "inputFiles";
        final Path docDir = Paths.get(docsPath);

        try {
            Directory dir = FSDirectory.open(Paths.get(indexPath));

            Analyzer analyzer = new StandardAnalyzer();

            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
            iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);

            IndexWriter writer = new IndexWriter(dir, iwc);

            indexDocs(writer, docDir);

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void indexDocs(final IndexWriter writer, Path path) throws IOException {
        if (Files.isDirectory(path)) {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    try {
                        indexDoc(writer, file);
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } else {
            indexDoc(writer, path);
        }
    }

    private static void indexDoc(IndexWriter writer, Path file) throws IOException {
        try {

            JsonObject jsonObject = CommonUtils.fromJsonFile(file.toString());
            if (jsonObject == null)
                return;

//            LatLonPoint
//            Place place = CommonUtils.fromJson(jsonObject.toString(), Place.class);
//
//            Document doc = new Document();
//            doc.add(new StringField("path", file.toString(), Field.Store.YES));
//
//            doc.add(new TextField(PLACE_KEY_NAME, place.getName(), Field.Store.YES));
//            doc.add(new TextField(PLACE_KEY_ABSTRACT, place.getDescription(), Field.Store.YES));
//            doc.add(new TextField(PLACE_KEY_COUNTRY, place.getCountry(), Field.Store.YES));
//
//            writer.updateDocument(new Term("path", file.toString()), doc);

            String[] latlng = jsonObject.get("latlng").toString().replaceAll("[\"()]", "").split(" ");

            System.out.printf("%s %s\n", latlng[0], latlng[1]);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
