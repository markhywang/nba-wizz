package interface_adapter.search_player;

import interface_adapter.ViewManagerModel;
import org.junit.jupiter.api.Test;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

public class SearchPlayerViewModelTest {

    @Test
    void defaultStateIsNotNull() {
        SearchPlayerViewModel viewModel = new SearchPlayerViewModel();
        assertNotNull(viewModel.getState());
        assertNotNull(viewModel.getViewManagerModel());
    }

    @Test
    void setStateFiresPropertyChange() {
        SearchPlayerViewModel viewModel = new SearchPlayerViewModel();
        AtomicReference<PropertyChangeEvent> eventRef = new AtomicReference<>();

        PropertyChangeListener listener = eventRef::set;
        viewModel.addPropertyChangeListener(listener);

        SearchPlayerState newState = new SearchPlayerState();
        newState.setErrorMessage("test");
        viewModel.setState(newState);

        PropertyChangeEvent event = eventRef.get();
        assertNotNull(event);
        assertEquals("state", event.getPropertyName());
        assertNull(event.getOldValue());
        assertEquals(newState, event.getNewValue());
        assertEquals(newState, viewModel.getState());
    }

    @Test
    void viewManagerModelCanBeSetAndRetrieved() {
        SearchPlayerViewModel viewModel = new SearchPlayerViewModel();
        ViewManagerModel viewManagerModel = new ViewManagerModel();
        viewModel.setViewManagerModel(viewManagerModel);
        assertSame(viewManagerModel, viewModel.getViewManagerModel());
    }
}
