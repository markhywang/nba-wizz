package use_case.sort_players;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SortInteractorTest {

    private static class RecordingPresenter implements SortOutputBoundary {
        boolean presentCalled;
        boolean presentNoPlayersCalled;
        SortOutputData lastOutputData;
        String lastNoPlayersMessage;

        @Override
        public void present(SortOutputData outputData) {
            this.presentCalled = true;
            this.lastOutputData = outputData;
        }

        @Override
        public void presentNoPlayers(String message) {
            this.presentNoPlayersCalled = true;
            this.lastNoPlayersMessage = message;
        }
    }

    @Test
    void executeWithNullRowsCallsPresentNoPlayers() {
        RecordingPresenter presenter = new RecordingPresenter();
        SortInteractor interactor = new SortInteractor(presenter);

        SortInputData inputData = new SortInputData(null, 0, true);
        interactor.execute(inputData);

        assertFalse(presenter.presentCalled);
        assertTrue(presenter.presentNoPlayersCalled);
        assertEquals("No players to sort.", presenter.lastNoPlayersMessage);
    }

    @Test
    void executeWithEmptyRowsCallsPresentNoPlayers() {
        RecordingPresenter presenter = new RecordingPresenter();
        SortInteractor interactor = new SortInteractor(presenter);

        List<String[]> rows = new ArrayList<>();
        SortInputData inputData = new SortInputData(rows, 1, false);
        interactor.execute(inputData);

        assertFalse(presenter.presentCalled);
        assertTrue(presenter.presentNoPlayersCalled);
        assertEquals("No players to sort.", presenter.lastNoPlayersMessage);
    }

    @Test
    void executeSortsNumericValuesAscending() {
        RecordingPresenter presenter = new RecordingPresenter();
        SortInteractor interactor = new SortInteractor(presenter);

        List<String[]> rows = new ArrayList<>();
        rows.add(new String[]{"10"});
        rows.add(new String[]{"2"});
        rows.add(new String[]{"1"});

        SortInputData inputData = new SortInputData(rows, 0, true);
        interactor.execute(inputData);

        assertTrue(presenter.presentCalled);
        assertFalse(presenter.presentNoPlayersCalled);
        assertNotNull(presenter.lastOutputData);

        List<String[]> sorted = presenter.lastOutputData.sortedRows();
        assertEquals(3, sorted.size());
        assertEquals("1", sorted.get(0)[0]);
        assertEquals("2", sorted.get(1)[0]);
        assertEquals("10", sorted.get(2)[0]);

        assertEquals(0, presenter.lastOutputData.sortedColumnIndex());
        assertTrue(presenter.lastOutputData.ascending());
    }

    @Test
    void executeSortsNumericValuesDescending() {
        RecordingPresenter presenter = new RecordingPresenter();
        SortInteractor interactor = new SortInteractor(presenter);

        List<String[]> rows = new ArrayList<>();
        rows.add(new String[]{"10"});
        rows.add(new String[]{"2"});
        rows.add(new String[]{"1"});

        SortInputData inputData = new SortInputData(rows, 0, false);
        interactor.execute(inputData);

        assertTrue(presenter.presentCalled);
        assertFalse(presenter.presentNoPlayersCalled);
        assertNotNull(presenter.lastOutputData);

        List<String[]> sorted = presenter.lastOutputData.sortedRows();
        assertEquals(3, sorted.size());
        assertEquals("10", sorted.get(0)[0]);
        assertEquals("2", sorted.get(1)[0]);
        assertEquals("1", sorted.get(2)[0]);

        assertEquals(0, presenter.lastOutputData.sortedColumnIndex());
        assertFalse(presenter.lastOutputData.ascending());
    }

    @Test
    void executePlacesMissingValuesFirstWhenAscending() {
        RecordingPresenter presenter = new RecordingPresenter();
        SortInteractor interactor = new SortInteractor(presenter);

        List<String[]> rows = new ArrayList<>();
        String[] nullRow = null;
        String[] emptyRow = new String[]{""};
        String[] naRow = new String[]{"N/A"};
        String[] valueRow = new String[]{"5"};

        rows.add(valueRow);
        rows.add(naRow);
        rows.add(emptyRow);
        rows.add(nullRow);

        SortInputData inputData = new SortInputData(rows, 0, true);
        interactor.execute(inputData);

        assertTrue(presenter.presentCalled);
        assertFalse(presenter.presentNoPlayersCalled);

        List<String[]> sorted = presenter.lastOutputData.sortedRows();
        assertEquals(4, sorted.size());
        // missing rows should come before non-missing when ascending
        List<String[]> firstThree = sorted.subList(0, 3);
        assertTrue(firstThree.contains(nullRow));
        assertTrue(firstThree.contains(emptyRow));
        assertTrue(firstThree.contains(naRow));
        assertArrayEquals(valueRow, sorted.get(3));
    }

    @Test
    void executePlacesMissingValuesLastWhenDescending() {
        RecordingPresenter presenter = new RecordingPresenter();
        SortInteractor interactor = new SortInteractor(presenter);

        List<String[]> rows = new ArrayList<>();
        String[] nullRow = null;
        String[] valueRow = new String[]{"5"};

        rows.add(nullRow);
        rows.add(valueRow);

        SortInputData inputData = new SortInputData(rows, 0, false);
        interactor.execute(inputData);

        assertTrue(presenter.presentCalled);
        assertFalse(presenter.presentNoPlayersCalled);

        List<String[]> sorted = presenter.lastOutputData.sortedRows();
        assertEquals(2, sorted.size());
        // missing row should come after non-missing when descending
        assertArrayEquals(valueRow, sorted.get(0));
        assertNull(sorted.get(1));
    }

    @Test
    void executeUsesCaseInsensitiveStringComparison() {
        RecordingPresenter presenter = new RecordingPresenter();
        SortInteractor interactor = new SortInteractor(presenter);

        List<String[]> rows = new ArrayList<>();
        String[] row1 = new String[]{"Bob"};
        String[] row2 = new String[]{"alice"};
        String[] row3 = new String[]{"Charlie"};

        rows.add(row1);
        rows.add(row2);
        rows.add(row3);

        SortInputData inputData = new SortInputData(rows, 0, true);
        interactor.execute(inputData);

        assertTrue(presenter.presentCalled);
        assertFalse(presenter.presentNoPlayersCalled);

        List<String[]> sorted = presenter.lastOutputData.sortedRows();
        assertEquals(3, sorted.size());
        assertArrayEquals(row2, sorted.get(0)); // alice
        assertArrayEquals(row1, sorted.get(1)); // Bob
        assertArrayEquals(row3, sorted.get(2)); // Charlie
    }

    @Test
    void executeHandlesInvalidColumnIndexAsMissing() {
        RecordingPresenter presenter = new RecordingPresenter();
        SortInteractor interactor = new SortInteractor(presenter);

        List<String[]> rows = new ArrayList<>();
        String[] row1 = new String[]{"1"};
        String[] row2 = new String[]{"2"};

        rows.add(row1);
        rows.add(row2);

        // use a negative column index so every value is treated as missing
        SortInputData inputData = new SortInputData(rows, -1, true);
        interactor.execute(inputData);

        assertTrue(presenter.presentCalled);
        assertFalse(presenter.presentNoPlayersCalled);

        List<String[]> sorted = presenter.lastOutputData.sortedRows();
        // both rows are considered missing, so their relative order is preserved
        assertEquals(2, sorted.size());
        assertArrayEquals(row1, sorted.get(0));
        assertArrayEquals(row2, sorted.get(1));
    }

    @Test
    void recordsExposeValues() {
        List<String[]> rows = new ArrayList<>();
        rows.add(new String[]{"1", "PG"});

        SortInputData input = new SortInputData(rows, 1, true);
        assertSame(rows, input.tableRows());
        assertEquals(1, input.columnIndex());
        assertTrue(input.ascending());

        SortOutputData output = new SortOutputData(rows, 0, false);
        assertSame(rows, output.sortedRows());
        assertEquals(0, output.sortedColumnIndex());
        assertFalse(output.ascending());
    }
}
