package org.hola.com.springaiholaguy.service;


import org.hola.com.springaiholaguy.utils.MatchResult;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Map;


@Service
public class SimilarityService {
    private final EmbeddingModel embeddingModel;
    private final ChatModel chatModel;

    @Value("classpath:/templates/rag-prompt-template.st")
    private Resource ragPromptTemplate;

    public SimilarityService(EmbeddingModel embeddingModel,
                             ChatModel chatModel) {
        this.embeddingModel = embeddingModel;
        this.chatModel = chatModel;
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

    public MatchResult findBestMatchFromModel(String query, List<String> options) {
        PromptTemplate promptTemplate = new PromptTemplate(ragPromptTemplate);
        Message userMessage = promptTemplate.createMessage(Map.of("input", query, "documents",
                String.join("\n", options)));;
        ChatResponse response = chatModel.call(new Prompt(List.of(userMessage)));
        var outputConverter = new BeanOutputConverter<>(MatchResult.class);
        String content = response.getResult().getOutput().getText();
        return outputConverter.convert(content);
    }
}
