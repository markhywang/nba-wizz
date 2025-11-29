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

    public record RowVM(String metric, List<String> cells, Integer bestIndex) {
    }
}
