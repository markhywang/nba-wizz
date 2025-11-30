package entity;

import java.time.LocalDateTime;

public record AIInsight(int id, String entityType, String entityName, String summaryText, LocalDateTime timestamp) {
}