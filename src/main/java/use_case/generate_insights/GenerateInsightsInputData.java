package use_case.generate_insights;

public class GenerateInsightsInputData {
    private final String entityName;
    private final String entityType;

    public GenerateInsightsInputData(String entityName, String entityType) {
        this.entityName = entityName;
        this.entityType = entityType;
    }

    public String getEntityName() {
        return entityName;
    }

    public String getEntityType() {
        return entityType;
    }
}
