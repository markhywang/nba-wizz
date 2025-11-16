package interface_adapter.compare;

import java.util.List;
import java.util.ArrayList;

public class CompareState {

    public List<String> entities =  new ArrayList<>();
    public String seasonLabel = "";
    public List<RowVM> table = new ArrayList<>();
    public List<String> notices = new ArrayList<>();
    public String insight = null;
    public String error = null;

    public static class RowVM {
        public final String metric;
        public final List<String> cells;
        public final Integer bestIndex;

        public RowVM(String metric, List<String> cells, Integer bestIndex) {
            this.metric = metric;
            this.cells = cells;
            this.bestIndex = bestIndex;
        }
    }
}
