package use_case.favourite;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class FavouriteInteractorTest {

    private static class RecordingPresenter implements FavouriteOutputBoundary {
        boolean addCalled = false;
        boolean removeCalled = false;
        FavouriteOutputData lastOutput = null;

        @Override
        public void addFavourite(FavouriteOutputData favouriteOutputData) {
            this.addCalled = true;
            this.lastOutput = favouriteOutputData;
        }

        @Override
        public void removeFavourite(FavouriteOutputData favouriteOutputData) {
            this.removeCalled = true;
            this.lastOutput = favouriteOutputData;
        }
    }

    private static class InMemoryDataAccess implements FavouriteDataAccessInterface {
        ArrayList<String> list = new ArrayList<>();
        boolean saveCalled = false;
        String currentUser = null;

        @Override
        public void add(String playerName) {
            if (!list.contains(playerName)) list.add(playerName);
        }

        @Override
        public void remove(String playerName) {
            list.remove(playerName);
        }

        @Override
        public boolean isFavourite(String playerName) {
            return list.contains(playerName);
        }

        @Override
        public ArrayList<String> getFavourites() {
            return list;
        }

        @Override
        public void save() {
            this.saveCalled = true;
        }

        @Override
        public void setCurrentUser(String username) {
            this.currentUser = username;
        }
    }

    @Test
    void addCallsDataAccessAndPresenter() {
        RecordingPresenter presenter = new RecordingPresenter();
        InMemoryDataAccess dataAccess = new InMemoryDataAccess();
        FavouriteInteractor interactor = new FavouriteInteractor(presenter, dataAccess);

        FavouriteInputData input = new FavouriteInputData("LeBron");
        interactor.add(input);

        assertTrue(dataAccess.list.contains("LeBron"));
        assertTrue(dataAccess.saveCalled);
        assertTrue(presenter.addCalled);
        assertNotNull(presenter.lastOutput);
        assertTrue(presenter.lastOutput.success());
        assertSame(dataAccess.getFavourites(), presenter.lastOutput.favourites());
    }

    @Test
    void removeCallsDataAccessAndPresenter() {
        RecordingPresenter presenter = new RecordingPresenter();
        InMemoryDataAccess dataAccess = new InMemoryDataAccess();
        dataAccess.list.add("KD");
        FavouriteInteractor interactor = new FavouriteInteractor(presenter, dataAccess);

        FavouriteInputData input = new FavouriteInputData("KD");
        interactor.remove(input);

        assertFalse(dataAccess.list.contains("KD"));
        assertTrue(dataAccess.saveCalled);
        assertTrue(presenter.removeCalled);
        assertNotNull(presenter.lastOutput);
        assertTrue(presenter.lastOutput.success());
        assertSame(dataAccess.getFavourites(), presenter.lastOutput.favourites());
    }

    @Test
    void toggleAddsWhenNotFavourite() {
        RecordingPresenter presenter = new RecordingPresenter();
        InMemoryDataAccess dataAccess = new InMemoryDataAccess();
        FavouriteInteractor interactor = new FavouriteInteractor(presenter, dataAccess);

        FavouriteInputData input = new FavouriteInputData("Curry");
        assertFalse(interactor.isFavourite(input));
        interactor.toggle(input);

        assertTrue(dataAccess.list.contains("Curry"));
        assertTrue(presenter.addCalled);
    }

    @Test
    void toggleRemovesWhenFavourite() {
        RecordingPresenter presenter = new RecordingPresenter();
        InMemoryDataAccess dataAccess = new InMemoryDataAccess();
        dataAccess.list.add("Harden");
        FavouriteInteractor interactor = new FavouriteInteractor(presenter, dataAccess);

        FavouriteInputData input = new FavouriteInputData("Harden");
        assertTrue(interactor.isFavourite(input));
        interactor.toggle(input);

        assertFalse(dataAccess.list.contains("Harden"));
        assertTrue(presenter.removeCalled);
    }

    @Test
    void delegatesGetFavouritesAndIsFavourite() {
        RecordingPresenter presenter = new RecordingPresenter();
        InMemoryDataAccess dataAccess = new InMemoryDataAccess();
        dataAccess.list.add("AD");
        FavouriteInteractor interactor = new FavouriteInteractor(presenter, dataAccess);

        FavouriteInputData input = new FavouriteInputData("AD");
        assertTrue(interactor.isFavourite(input));
        ArrayList<String> favourites = interactor.getFavourites();
        assertSame(dataAccess.list, favourites);
    }

    @Test
    void recordsAndInputExposeValues() {
        ArrayList<String> list = new ArrayList<>();
        list.add("Zion");
        FavouriteOutputData out = new FavouriteOutputData(false, list);
        assertFalse(out.success());
        assertSame(list, out.favourites());

        FavouriteInputData in = new FavouriteInputData("Young");
        assertEquals("Young", in.playerName);
    }
}
