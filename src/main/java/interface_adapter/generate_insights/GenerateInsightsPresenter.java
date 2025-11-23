package interface_adapter.generate_insights;

import interface_adapter.ViewManagerModel;
import use_case.generate_insights.GenerateInsightsOutputBoundary;
import use_case.generate_insights.GenerateInsightsOutputData;

public class GenerateInsightsPresenter implements GenerateInsightsOutputBoundary {
    private final GenerateInsightsViewModel generateInsightsViewModel;
    private final ViewManagerModel viewManagerModel;

    public GenerateInsightsPresenter(GenerateInsightsViewModel generateInsightsViewModel, ViewManagerModel viewManagerModel) {
        this.generateInsightsViewModel = generateInsightsViewModel;
        this.viewManagerModel = viewManagerModel;
    }

    @Override
    public void prepareSuccessView(GenerateInsightsOutputData outputData) {
        GenerateInsightsState state = generateInsightsViewModel.getState();
        state.setInsight(outputData.getInsight().getSummaryText());
        state.setLoading(false);
        generateInsightsViewModel.setState(state);
        generateInsightsViewModel.firePropertyChanged();
    }

    @Override
    public void prepareFailView(String error) {
        GenerateInsightsState state = generateInsightsViewModel.getState();
        state.setError(error);
        state.setLoading(false);
        generateInsightsViewModel.setState(state);
        generateInsightsViewModel.firePropertyChanged();
    }

    @Override
    public void prepareLoadingView() {
        GenerateInsightsState state = generateInsightsViewModel.getState();
        state.setLoading(true);
        state.setError(null); // Clear previous errors
        generateInsightsViewModel.setState(state);
        generateInsightsViewModel.firePropertyChanged();
    }
}
