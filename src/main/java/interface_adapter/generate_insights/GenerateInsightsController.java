package interface_adapter.generate_insights;

import interface_adapter.ViewManagerModel;
import interface_adapter.main_menu.MainMenuViewModel;
import use_case.generate_insights.GenerateInsightsInputBoundary;
import use_case.generate_insights.GenerateInsightsInputData;

public class GenerateInsightsController {
    private final GenerateInsightsInputBoundary generateInsightsInteractor;
    private final ViewManagerModel viewManagerModel;
    private final MainMenuViewModel mainMenuViewModel;

    public GenerateInsightsController(GenerateInsightsInputBoundary generateInsightsInteractor, ViewManagerModel viewManagerModel, MainMenuViewModel mainMenuViewModel) {
        this.generateInsightsInteractor = generateInsightsInteractor;
        this.viewManagerModel = viewManagerModel;
        this.mainMenuViewModel = mainMenuViewModel;
    }

    public void execute(String entityName, String entityType) {
        GenerateInsightsInputData inputData = new GenerateInsightsInputData(entityName, entityType);
        generateInsightsInteractor.execute(inputData);
    }

    public void goBack() {
        viewManagerModel.setActiveView(mainMenuViewModel.getViewName());
        viewManagerModel.firePropertyChanged();
    }
}
