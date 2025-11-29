package interface_adapter.compare;

import interface_adapter.ViewManagerModel;
import org.junit.Test;
import use_case.compare.CompareOutputData;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ComparePresenterTest {

    @Test
    public void present_updatesViewModelState() {
        // Arrange
        CompareViewModel viewModel = new CompareViewModel();
        ViewManagerModel viewManagerModel = new ViewManagerModel();
        ComparePresenter presenter = new ComparePresenter(viewManagerModel, viewModel);

        List<String> entities = List.of("Larry Bird", "Magic Johnson");

        List<Double> ptsValues = List.of(25.0, 20.1234);
        CompareOutputData.Row ptsRow =
                new CompareOutputData.Row("PTS", ptsValues, 0);

        List<Double> astValues = new ArrayList<>();
        astValues.add(7.0);
        astValues.add(null); // should become "-"
        CompareOutputData.Row astRow =
                new CompareOutputData.Row("AST", astValues, 0);

        List<CompareOutputData.Row> rows = List.of(ptsRow, astRow);

        CompareOutputData outputData = new CompareOutputData(
                entities,
                "1980-1985",
                rows,
                List.of("Note 1"),
                "Insight text"
        );

        // Act
        presenter.present(outputData);

        // Assert
        CompareState state = viewModel.getState();

        assertEquals(entities, state.entities);
        assertEquals("1980-1985", state.seasonLabel);
        assertEquals(List.of("Note 1"), state.notices);
        assertEquals("Insight text", state.insight);

        assertEquals(2, state.table.size());

        // PTS row – numbers formatted
        CompareState.RowVM ptsVm = state.table.get(0);
        assertEquals("PTS", ptsVm.metric());
        assertEquals(List.of("25", "20.12"), ptsVm.cells());
        assertEquals(Integer.valueOf(0), ptsVm.bestIndex());

        // AST row – second value was null -> "-"
        CompareState.RowVM astVm = state.table.get(1);
        assertEquals("AST", astVm.metric());
        assertEquals(List.of("7", "-"), astVm.cells());
        assertEquals(Integer.valueOf(0), astVm.bestIndex());
    }

    @Test
    public void presentError_setsErrorInState() {
        CompareViewModel viewModel = new CompareViewModel();
        ViewManagerModel viewManagerModel = new ViewManagerModel();
        ComparePresenter presenter = new ComparePresenter(viewManagerModel, viewModel);

        presenter.presentError("Something went wrong.");

        CompareState state = viewModel.getState();
        assertEquals("Something went wrong.", state.error);
        // Optional: other fields still default
        assertTrue(state.entities.isEmpty());
        assertTrue(state.table.isEmpty());
    }
}
