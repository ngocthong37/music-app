package com.vanvan.musicapp.service;

import com.vanvan.musicapp.entity.Song;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SongVectorService {

    private Directory directory;
    private Analyzer analyzer;
    @Getter
    private Map<Integer, Map<String, Double>> songVectors; // Lưu trữ vector TF-IDF

    @PostConstruct
    public void init() throws IOException {
        directory = new RAMDirectory();
        analyzer = new StandardAnalyzer();
        songVectors = new HashMap<>();
    }

    // Xây dựng vector TF-IDF cho tất cả bài hát
    public void buildVectors(List<Song> songs) throws IOException {
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter writer = new IndexWriter(directory, config);

        for (Song song : songs) {
            Document doc = new Document();
            doc.add(new StringField("id", song.getId().toString(), Field.Store.YES));

            StringBuilder combinedText = new StringBuilder();
            combinedText.append(song.getTitle()).append(" ");
            if (song.getArtist() != null) combinedText.append(song.getArtist().getName()).append(" ");
            if (song.getGenre() != null) combinedText.append(song.getGenre().getName());

            // Dùng field "content" để chứa toàn bộ văn bản
            doc.add(new TextField("content", combinedText.toString(), Field.Store.YES));
            writer.addDocument(doc);
        }
        writer.close();

        IndexReader reader = DirectoryReader.open(directory);
        int numDocs = reader.numDocs();

        for (int i = 0; i < numDocs; i++) {
            Document doc = reader.document(i);
            String docId = doc.get("id");
            String content = doc.get("content");

            // Tính TF cho tài liệu này
            Map<String, Integer> termFreqMap = new HashMap<>();
            TokenStream tokenStream = analyzer.tokenStream("content", content);
            tokenStream.reset();
            while (tokenStream.incrementToken()) {
                String term = tokenStream.getAttribute(CharTermAttribute.class).toString();
                termFreqMap.put(term, termFreqMap.getOrDefault(term, 0) + 1);
            }
            tokenStream.end();
            tokenStream.close();

            // Tính TF-IDF
            Map<String, Double> vector = new HashMap<>();
            for (String term : termFreqMap.keySet()) {
                int tf = termFreqMap.get(term);
                int df = reader.docFreq(new Term("content", term));
                double idf = Math.log((double)(1 + numDocs) / (1 + df)) + 1.0;
                double tfidf = tf * idf;
                vector.put(term, tfidf);
            }

            songVectors.put(Integer.valueOf(docId), vector);
        }

        reader.close();
    }


    // Tính độ tương tự Cosine giữa hai vector
    public double calculateCosineSimilarity(Map<String, Double> v1, Map<String, Double> v2) {
        double dotProduct = 0.0, norm1 = 0.0, norm2 = 0.0;

        for (String key : v1.keySet()) {
            if (v2.containsKey(key)) {
                dotProduct += v1.get(key) * v2.get(key);
            }
            norm1 += Math.pow(v1.get(key), 2);
        }

        for (Double value : v2.values()) {
            norm2 += Math.pow(value, 2);
        }

        norm1 = Math.sqrt(norm1);
        norm2 = Math.sqrt(norm2);

        if (norm1 == 0 || norm2 == 0) return 0.0;
        return dotProduct / (norm1 * norm2);
    }
}