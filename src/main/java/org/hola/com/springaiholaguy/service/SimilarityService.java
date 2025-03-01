package org.hola.com.springaiholaguy.service;


import org.hola.com.springaiholaguy.utils.MatchResult;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.stereotype.Service;


import java.util.List;


@Service
public class SimilarityService {
    private final EmbeddingModel embeddingModel;

    public SimilarityService(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    private double cosineSimilarity(float[] vectorA, float[] vectorB) {
        double dotProduct = 0.0, normA = 0.0, normB = 0.0;

        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += vectorA[i] * vectorB[i];
            normA += Math.pow(vectorA[i], 2);
            normB += Math.pow(vectorB[i], 2);
        }

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    public MatchResult findBestMatch(String query, List<String> options) {
        float[] queryEmbeddings = embeddingModel.embed(query);
        List<float[]> optionEmbeddings = embeddingModel.embed(options);


        double maxScore = -1;
        String bestMatch = null;

        for (int i = 0; i < optionEmbeddings.size(); i++) {
            float[] optionVector = optionEmbeddings.get(i);
            double score = cosineSimilarity(queryEmbeddings, optionVector);

            if (score > maxScore) {
                maxScore = score;
                bestMatch = options.get(i); // Get corresponding option text
            }
        }
        return new MatchResult(bestMatch, maxScore);
    }

}
