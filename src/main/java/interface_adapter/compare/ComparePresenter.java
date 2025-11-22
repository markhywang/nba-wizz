package interface_adapter.compare;

import interface_adapter.ViewManagerModel;
import use_case.compare.CompareOutputBoundary;
import use_case.compare.CompareOutputData;

import java.util.List;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class ComparePresenter implements CompareOutputBoundary {

    private final CompareViewModel viewModel;
    private final ViewManagerModel viewManagerModel;
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");

    public ComparePresenter(ViewManagerModel viewManagerModel, CompareViewModel viewModel) {
        this.viewManagerModel = viewManagerModel;
        this.viewModel = viewModel;
    }

    @Override
    public void present(CompareOutputData compareOutputData) {
        CompareState state = new CompareState();
        state.entities = compareOutputData.getEntities();
        state.seasonLabel = compareOutputData.getSeasonLabel();
        state.notices = compareOutputData.getNotices();
        state.insight = compareOutputData.getInsight();

        List<CompareState.RowVM> rows = new ArrayList<>();
        for (CompareOutputData.Row row : compareOutputData.getTable()) {
            List<String> cells = new ArrayList<>();
            for (Double v : row.values) {
                cells.add(v == null ? "-" : DECIMAL_FORMAT.format(v));
            }
            rows.add(new CompareState.RowVM(row.metric, cells, row.bestIndex));
        }
        state.table = rows;

        viewModel.setState(state);
    }

    @Override
    public void presentError(String errorMessage) {
        CompareState state = new CompareState();
        state.error = errorMessage;
        viewModel.setState(state);
    }

    @Override
    public void switchToMainMenu() {
        viewManagerModel.setActiveView("main_menu");
        viewManagerModel.firePropertyChanged();
    }
}
