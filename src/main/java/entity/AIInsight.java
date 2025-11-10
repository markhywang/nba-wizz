package entity;

import java.time.LocalDateTime;
import java.util.Map;

public class AIInsight {
    private final int id;
    private final String entityType;
    private final String entityName;
    private String summaryText;
    private Map<String, Double> prediction;
    private final LocalDateTime timestamp;
    private double confidenceScore;

    public AIInsight(int id, String entityType, String entityName, String summaryText, Map<String, Double> prediction, double confidenceScore) {
        this.id = id;
        this.entityType = entityType;
        this.entityName = entityName;
        this.summaryText = summaryText;
        this.prediction = prediction;
        this.timestamp = LocalDateTime.now();
        this.confidenceScore = confidenceScore;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getEntityType() {
        return entityType;
    }

    public String getEntityName() {
        return entityName;
    }

    public String getSummaryText() {
        return summaryText;
    }

    public Map<String, Double> getPrediction() {
        return prediction;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public double getConfidenceScore() {
        return confidenceScore;
    }

    // Setters
    public void setSummaryText(String summaryText) {
        this.summaryText = summaryText;
    }

    public void setPrediction(Map<String, Double> prediction) {
        this.prediction = prediction;
    }

    public void setConfidenceScore(double confidenceScore) {
        this.confidenceScore = confidenceScore;
    }
}
