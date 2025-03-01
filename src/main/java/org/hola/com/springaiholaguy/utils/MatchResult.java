package org.hola.com.springaiholaguy.utils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MatchResult {
    private final String match;
    private final double confidence;

    public MatchResult(String match, double confidence) {
        this.match = match;
        this.confidence = confidence;
    }

    public String getMatch() {
        return match;
    }

    public double getConfidence() {
        return confidence;
    }

    @Override
    public String toString() {
        return "MatchResult{match='" + match + "', confidence=" + confidence + "}";
    }
}
