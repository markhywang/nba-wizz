package use_case.compare;

import java.util.List;

public class CompareOutputData {

    public static class Row {
        public final String metric;
        public final List<Double> values;
        public final Integer bestIndex;

        public Row(String metric, List<Double> values, Integer bestIndex) {
            this.metric = metric;
            this.values = values;
            this.bestIndex = bestIndex;
        }
    }

    private final List<String> entities;
    private final String seasonLabel;
    private final List<Row> table;
    private final List<String> notices;
    private final String insight;

    public CompareOutputData(List<String> entities, String seasonLabel, List<Row> table, List<String> notices, String insight) {
        this.entities = entities;
        this.seasonLabel = seasonLabel;
        this.table = table;
        this.notices = notices;
        this.insight = insight;
    }

    public List<String> getEntities() { return entities; }
    public String getSeasonLabel() { return seasonLabel; }
    public List<Row> getTable() { return table; }
    public List<String> getNotices() { return notices; }
    public String getInsight() { return insight; }
}