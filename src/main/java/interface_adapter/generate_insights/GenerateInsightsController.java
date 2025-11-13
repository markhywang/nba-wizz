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
        // Run the potentially-blocking interactor off the Swing EDT so the UI stays responsive.
        javax.swing.SwingWorker<Void, Void> worker = new javax.swing.SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                generateInsightsInteractor.execute(inputData);
                return null;
            }
        };
        worker.execute();
    }

    public void goBack() {
        viewManagerModel.setActiveView(mainMenuViewModel.getViewName());
        viewManagerModel.firePropertyChanged();
    }
}
