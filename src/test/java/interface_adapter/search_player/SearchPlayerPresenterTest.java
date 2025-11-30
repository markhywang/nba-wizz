package interface_adapter.search_player;

import interface_adapter.ViewManagerModel;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class SearchPlayerPresenterTest {

    @Test
    void presentUpdatesState() throws Exception {
        SearchPlayerViewModel viewModel = new SearchPlayerViewModel();
        ViewManagerModel viewManagerModel = new ViewManagerModel();
        SearchPlayerPresenter presenter = new SearchPlayerPresenter(viewModel, viewManagerModel);

        Class<?> outputDataClass = Class.forName("use_case.search_player.SearchPlayerOutputData");
        Constructor<?> constructor;
        try {
            constructor = outputDataClass.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            Constructor<?>[] constructors = outputDataClass.getDeclaredConstructors();
            constructor = constructors[0];
        }
        constructor.setAccessible(true);

        Class<?>[] parameterTypes = constructor.getParameterTypes();
        Object[] args = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> type = parameterTypes[i];
            if (type.isPrimitive()) {
                if (type.equals(boolean.class)) {
                    args[i] = false;
                } else if (type.equals(byte.class)) {
                    args[i] = (byte) 0;
                } else if (type.equals(short.class)) {
                    args[i] = (short) 0;
                } else if (type.equals(int.class)) {
                    args[i] = 0;
                } else if (type.equals(long.class)) {
                    args[i] = 0L;
                } else if (type.equals(float.class)) {
                    args[i] = 0f;
                } else if (type.equals(double.class)) {
                    args[i] = 0d;
                } else if (type.equals(char.class)) {
                    args[i] = '\0';
                }
            } else if (List.class.isAssignableFrom(type)) {
                args[i] = new ArrayList<>();
            } else if (Map.class.isAssignableFrom(type)) {
                args[i] = new HashMap<>();
            } else if (String.class.equals(type)) {
                args[i] = "test";
            } else {
                args[i] = null;
            }
        }

        Object outputData = constructor.newInstance(args);

        Method presentMethod = SearchPlayerPresenter.class.getMethod("present", outputDataClass);
        presentMethod.invoke(presenter, outputData);

        SearchPlayerState stateAfterPresent = viewModel.getState();
        assertNotNull(stateAfterPresent);
        assertNull(stateAfterPresent.getErrorMessage());
    }

    @Test
    void presentPlayerNotFoundSetsErrorMessage() {
        SearchPlayerViewModel viewModel = new SearchPlayerViewModel();
        ViewManagerModel viewManagerModel = new ViewManagerModel();
        SearchPlayerPresenter presenter = new SearchPlayerPresenter(viewModel, viewManagerModel);

        String message = "Player not found";
        presenter.presentPlayerNotFound(message);

        SearchPlayerState state = viewModel.getState();
        assertEquals(message, state.getErrorMessage());
        assertNull(state.getResultsTableData());
        assertNull(state.getGraphData());
    }
}
