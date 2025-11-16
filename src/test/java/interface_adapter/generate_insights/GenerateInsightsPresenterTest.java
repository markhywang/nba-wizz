package interface_adapter.generate_insights;

import interface_adapter.ViewManagerModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import use_case.generate_insights.GenerateInsightsOutputData;
import entity.AIInsight;
import java.time.LocalDateTime;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GenerateInsightsPresenterTest {

    @Mock
    private GenerateInsightsViewModel generateInsightsViewModel;

    @Mock
    private ViewManagerModel viewManagerModel;

    @InjectMocks
    private GenerateInsightsPresenter generateInsightsPresenter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testPrepareSuccessView() {
        AIInsight insight = new AIInsight(1, "player", "LeBron James", "LeBron James is a great player.", LocalDateTime.now());
        GenerateInsightsOutputData outputData = new GenerateInsightsOutputData(insight, false);

        GenerateInsightsState state = new GenerateInsightsState();
        when(generateInsightsViewModel.getState()).thenReturn(state);

        generateInsightsPresenter.prepareSuccessView(outputData);

        verify(generateInsightsViewModel).setState(state);
        verify(generateInsightsViewModel).firePropertyChanged();
    }

    @Test
    void testPrepareFailView() {
        String error = "Failed to generate insights.";

        GenerateInsightsState state = new GenerateInsightsState();
        when(generateInsightsViewModel.getState()).thenReturn(state);

        generateInsightsPresenter.prepareFailView(error);

        verify(generateInsightsViewModel).setState(state);
        verify(generateInsightsViewModel).firePropertyChanged();
    }
}
