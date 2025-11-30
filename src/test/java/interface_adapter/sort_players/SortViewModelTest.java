package interface_adapter.sort_players;

import interface_adapter.ViewManagerModel;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class SortViewModelTest {

    @Test
    void stateAndViewManagerModelAreStored() {
        SortViewModel viewModel = new SortViewModel();

        // default state is not null
        assertNotNull(viewModel.getState());

        // set and get state
        SortState newState = new SortState();
        newState.setErrorMessage("test");
        viewModel.setState(newState);
        assertSame(newState, viewModel.getState());

        // set and get view manager model
        ViewManagerModel manager = new ViewManagerModel();
        viewModel.setViewManagerModel(manager);
        assertSame(manager, viewModel.getViewManagerModel());
    }

    @Test
    void firePropertyChangedNotifiesListeners() {
        SortViewModel viewModel = new SortViewModel();

        AtomicReference<String> propertyName = new AtomicReference<>();
        AtomicReference<Object> newValue = new AtomicReference<>();

        viewModel.addPropertyChangeListener(evt -> {
            propertyName.set(evt.getPropertyName());
            newValue.set(evt.getNewValue());
        });

        viewModel.firePropertyChanged();

        assertEquals("state", propertyName.get());
        assertSame(viewModel.getState(), newValue.get());
    }
}
