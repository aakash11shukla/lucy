package com.siddhantkushwaha.lucy;

import java.io.IOException;
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

    public static void main(String[] args)
    {
        String docsPath = "inputFiles";

        String indexPath = "indexedFiles";

        final Path docDir = Paths.get(docsPath);

        try
        {
            Directory dir = FSDirectory.open( Paths.get(indexPath) );

            Analyzer analyzer = new StandardAnalyzer();

            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
            iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);

            IndexWriter writer = new IndexWriter(dir, iwc);

            indexDocs(writer, docDir);

            writer.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static void indexDocs(final IndexWriter writer, Path path) throws IOException
    {
        if (Files.isDirectory(path))
        {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>()
            {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
                {
                    try
                    {
                        indexDoc(writer, file, attrs.lastModifiedTime().toMillis());
                    }
                    catch (IOException ioe)
                    {
                        ioe.printStackTrace();
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        }
        else
        {
            indexDoc(writer, path, Files.getLastModifiedTime(path).toMillis());
        }
    }

    private static void indexDoc(IndexWriter writer, Path file, long lastModified) throws IOException
    {
        try
        {

            JsonObject jsonObject = CommonUtils.fromJsonFile(file.toString());
            if(jsonObject == null)
                return;
            Place place = CommonUtils.fromJson(jsonObject.toString(), Place.class);

            // System.out.println(place.toString());

            Document doc = new Document();
            doc.add(new StringField("path", file.toString(), Field.Store.YES));
            doc.add(new LongPoint("modified", lastModified));

            doc.add(new StringField("name", place.getName(), Field.Store.YES));
            doc.add(new StringField("abstract", place.getDescription(), Field.Store.YES));
            doc.add(new StringField("country", place.getCountry(), Field.Store.YES));

            writer.updateDocument(new Term("path", file.toString()), doc);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
