package use_case.sort_players;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SortPlayersInteractorTest {

    private static class TestPresenter implements SortOutputBoundary {
        SortOutputData lastOutput;
        String lastNoPlayersMessage;

        @Override
        public void present(SortOutputData outputData) {
            this.lastOutput = outputData;
        }

        @Override
        public void presentNoPlayers(String message) {
            this.lastNoPlayersMessage = message;
        }
    }

    @Test
    void executeWithNullRowsCallsNoPlayers() {
        TestPresenter presenter = new TestPresenter();
        SortInputBoundary interactor = new SortInteractor(presenter);

        SortInputData input = new SortInputData(null, 0, true);
        interactor.execute(input);

        assertNull(presenter.lastOutput);
        assertNotNull(presenter.lastNoPlayersMessage);
    }

    @Test
    void executeWithEmptyRowsCallsNoPlayers() {
        TestPresenter presenter = new TestPresenter();
        SortInteractor interactor = new SortInteractor(presenter);

        List<String[]> rows = new ArrayList<>();
        SortInputData input = new SortInputData(rows, 1, false);
        interactor.execute(input);

        assertNull(presenter.lastOutput);
        assertNotNull(presenter.lastNoPlayersMessage);
    }

    @Test
    void executeSortsNumericValuesAscending() {
        TestPresenter presenter = new TestPresenter();
        SortInteractor interactor = new SortInteractor(presenter);

        List<String[]> rows = new ArrayList<>();
        rows.add(new String[]{"3"});
        rows.add(new String[]{"10"});
        SortInputData input = new SortInputData(rows, 0, true);

        interactor.execute(input);

        assertNotNull(presenter.lastOutput);
        SortOutputData outputData = presenter.lastOutput;
        assertTrue(outputData.isAscending());
        assertEquals(0, outputData.getSortedColumnIndex());
        List<String[]> sortedRows = outputData.getSortedRows();
        assertEquals(2, sortedRows.size());
        assertArrayEquals(new String[]{"3"}, sortedRows.get(0));
        assertArrayEquals(new String[]{"10"}, sortedRows.get(1));
    }

    @Test
    void executeSortsNumericValuesDescending() {
        TestPresenter presenter = new TestPresenter();
        SortInteractor interactor = new SortInteractor(presenter);

        List<String[]> rows = new ArrayList<>();
        rows.add(new String[]{"3"});
        rows.add(new String[]{"10"});
        SortInputData input = new SortInputData(rows, 0, false);

        interactor.execute(input);

        assertNotNull(presenter.lastOutput);
        SortOutputData outputData = presenter.lastOutput;
        assertFalse(outputData.isAscending());
        List<String[]> sortedRows = outputData.getSortedRows();
        assertEquals(2, sortedRows.size());
        assertArrayEquals(new String[]{"10"}, sortedRows.get(0));
        assertArrayEquals(new String[]{"3"}, sortedRows.get(1));
    }

    @Test
    void executeUsesStringComparisonWhenNotNumeric() {
        TestPresenter presenter = new TestPresenter();
        SortInteractor interactor = new SortInteractor(presenter);

        List<String[]> rows = new ArrayList<>();
        rows.add(new String[]{"beta"});
        rows.add(new String[]{"alpha"});
        SortInputData input = new SortInputData(rows, 0, true);

        interactor.execute(input);

        SortOutputData outputData = presenter.lastOutput;
        List<String[]> sortedRows = outputData.getSortedRows();
        assertArrayEquals(new String[]{"alpha"}, sortedRows.get(0));
        assertArrayEquals(new String[]{"beta"}, sortedRows.get(1));
    }

    @Test
    void privateHelperMethodsAreCoveredViaReflection() throws Exception {
        SortInteractor interactor = new SortInteractor(new TestPresenter());

        Method createComparatorMethod =
                SortInteractor.class.getDeclaredMethod("createComparator", int.class, boolean.class);
        createComparatorMethod.setAccessible(true);

        Comparator<String[]> ascComparator =
                (Comparator<String[]>) createComparatorMethod.invoke(interactor, 0, true);
        Comparator<String[]> descComparator =
                (Comparator<String[]>) createComparatorMethod.invoke(interactor, 0, false);

        String[] nullRow = null;
        String[] emptyRow = new String[0];
        String[] missingRow = new String[]{""};
        String[] nonMissingRow = new String[]{"5"};

        int bothMissing = ascComparator.compare(emptyRow, nullRow);
        assertEquals(0, bothMissing);

        ascComparator.compare(missingRow, nonMissingRow);
        descComparator.compare(missingRow, nonMissingRow);
        ascComparator.compare(nonMissingRow, missingRow);
        descComparator.compare(nonMissingRow, missingRow);

        Method getColumnValueMethod =
                SortInteractor.class.getDeclaredMethod("getColumnValue", String[].class, int.class);
        getColumnValueMethod.setAccessible(true);

        String[] row = new String[]{"x", "y"};
        assertNull(getColumnValueMethod.invoke(interactor, new Object[]{null, 0}));
        assertNull(getColumnValueMethod.invoke(interactor, new Object[]{row, -1}));
        assertNull(getColumnValueMethod.invoke(interactor, new Object[]{row, 2}));
        assertEquals("x", getColumnValueMethod.invoke(interactor, new Object[]{row, 0}));
        assertEquals("y", getColumnValueMethod.invoke(interactor, new Object[]{row, 1}));

        Method isMissingMethod =
                SortInteractor.class.getDeclaredMethod("isMissing", String.class);
        isMissingMethod.setAccessible(true);

        assertTrue((Boolean) isMissingMethod.invoke(interactor, new Object[]{null}));
        assertTrue((Boolean) isMissingMethod.invoke(interactor, ""));
        assertTrue((Boolean) isMissingMethod.invoke(interactor, "   "));
        assertTrue((Boolean) isMissingMethod.invoke(interactor, "n/a"));
        assertTrue((Boolean) isMissingMethod.invoke(interactor, "NA"));
        assertTrue((Boolean) isMissingMethod.invoke(interactor, "Null"));
        assertFalse((Boolean) isMissingMethod.invoke(interactor, "value"));

        Method tryParseDoubleMethod =
                SortInteractor.class.getDeclaredMethod("tryParseDouble", String.class);
        tryParseDoubleMethod.setAccessible(true);

        Double parsedNumber = (Double) tryParseDoubleMethod.invoke(interactor, " 3.5 ");
        Object parsedInvalid = tryParseDoubleMethod.invoke(interactor, "abc");
        assertEquals(3.5, parsedNumber);
        assertNull(parsedInvalid);
    }

    @Test
    void sortInputAndOutputDataGettersWork() {
        List<String[]> rows = new ArrayList<>();
        rows.add(new String[]{"1", "2"});
        SortInputData inputData = new SortInputData(rows, 1, false);
        assertEquals(rows, inputData.getTableRows());
        assertEquals(1, inputData.getColumnIndex());
        assertFalse(inputData.isAscending());

        List<String[]> sortedRows = new ArrayList<>();
        sortedRows.add(new String[]{"a", "b"});
        SortOutputData outputData = new SortOutputData(sortedRows, 0, true);
        assertEquals(sortedRows, outputData.getSortedRows());
        assertEquals(0, outputData.getSortedColumnIndex());
        assertTrue(outputData.isAscending());
    }
}
