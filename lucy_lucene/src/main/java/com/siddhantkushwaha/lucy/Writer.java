package com.siddhantkushwaha.lucy;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DecimalFormat;

import com.google.gson.JsonObject;
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

            DecimalFormat decimalFormat = new DecimalFormat("0.00");
            String[] latlngStr = jsonObject.get("latlng").getAsString().replaceAll("[\"()]", "").split(" ");
            Double[] latlngDouble = {Double.valueOf(decimalFormat.format(Double.valueOf(latlngStr[1]))), Double.valueOf(decimalFormat.format(Double.valueOf(latlngStr[0])))};

            Document doc = new Document();
            doc.add(new StringField("path", file.toString(), Field.Store.YES));

            doc.add(new TextField(PLACE_KEY_NAME, jsonObject.get(PLACE_KEY_NAME).getAsString(), Field.Store.YES));
            doc.add(new TextField(PLACE_KEY_ABSTRACT, jsonObject.get(PLACE_KEY_ABSTRACT).getAsString(), Field.Store.YES));
            doc.add(new TextField(PLACE_KEY_COUNTRY, jsonObject.get(PLACE_KEY_COUNTRY).getAsString(), Field.Store.YES));

            doc.add(new LatLonDocValuesField(PLACE_KEY_LATLNG, latlngDouble[0], latlngDouble[1]));

            writer.updateDocument(new Term("path", file.toString()), doc);

            System.out.printf("%s %f %f\n", jsonObject.get(PLACE_KEY_NAME).getAsString(), latlngDouble[0], latlngDouble[1]);
        } catch (IllegalArgumentException e) {
            throw (e);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
