package use_case.compare;

import java.util.List;

public record CompareOutputData(List<String> entities, String seasonLabel, List<Row> table, List<String> notices,
                                String insight) {

    public record Row(String metric, List<Double> values, Integer bestIndex) {
    }

}