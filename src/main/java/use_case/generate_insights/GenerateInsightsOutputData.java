package use_case.generate_insights;

import entity.AIInsight;

public class GenerateInsightsOutputData {
    private final AIInsight insight;
    private final boolean useCaseFailed;

    public GenerateInsightsOutputData(AIInsight insight, boolean useCaseFailed) {
        this.insight = insight;
        this.useCaseFailed = useCaseFailed;
    }

    public AIInsight getInsight() {
        return insight;
    }
}
