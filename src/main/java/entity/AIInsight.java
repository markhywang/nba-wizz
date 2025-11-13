package entity;

import java.time.LocalDateTime;

public class AIInsight {
    private final int id;
    private final String entityType;
    private final String entityName;
    private final String summaryText;
    private final LocalDateTime timestamp;

    public AIInsight(int id, String entityType, String entityName, String summaryText, LocalDateTime timestamp) {
        this.id = id;
        this.entityType = entityType;
        this.entityName = entityName;
        this.summaryText = summaryText;
        this.timestamp = timestamp;
    }

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

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}