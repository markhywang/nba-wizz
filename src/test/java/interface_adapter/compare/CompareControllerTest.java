package interface_adapter.compare;

import entity.Normalization;
import org.junit.Test;
import use_case.compare.CompareInputBoundary;
import use_case.compare.CompareInputData;

import java.util.List;

import static org.junit.Assert.*;

public class CompareControllerTest {

    private static class CompareInputBoundarySpy implements CompareInputBoundary {
        CompareInputData lastInput;
        boolean switchCalled = false;

        @Override
        public void execute(CompareInputData inputData) {
            this.lastInput = inputData;
        }

        @Override
        public void switchToMainMenu() {
            this.switchCalled = true;
        }
    }

    @Test
    public void comparePlayers_buildsPlayerInputData() {
        CompareInputBoundarySpy spy = new CompareInputBoundarySpy();
        CompareController controller = new CompareController(spy);

        List<String> players = List.of("Larry Bird", "Magic Johnson");

        controller.comparePlayers(players, 1980, 1985, "Basic", Normalization.PER_GAME);

        assertNotNull(spy.lastInput);
        assertEquals(CompareInputData.EntityType.PLAYER, spy.lastInput.getEntityType());
        assertEquals(players, spy.lastInput.getEntities());
        assertEquals(1980, spy.lastInput.getSeasonStart());
        assertEquals(1985, spy.lastInput.getSeasonEnd());
        assertEquals("Basic", spy.lastInput.getStatPreset());
        assertEquals(Normalization.PER_GAME, spy.lastInput.getNormalization());
    }

    @Test
    public void compareTeams_buildsTeamInputData() {
        CompareInputBoundarySpy spy = new CompareInputBoundarySpy();
        CompareController controller = new CompareController(spy);

        List<String> teams = List.of("bos", "lal");

        controller.compareTeams(teams, 1990, 1998, "Efficiency", Normalization.PER_36);

        assertNotNull(spy.lastInput);
        assertEquals(CompareInputData.EntityType.TEAM, spy.lastInput.getEntityType());
        assertEquals(teams, spy.lastInput.getEntities());
        assertEquals(1990, spy.lastInput.getSeasonStart());
        assertEquals(1998, spy.lastInput.getSeasonEnd());
        assertEquals("Efficiency", spy.lastInput.getStatPreset());
        assertEquals(Normalization.PER_36, spy.lastInput.getNormalization());
    }

    @Test
    public void switchToMainMenu_delegatesToBoundary() {
        CompareInputBoundarySpy spy = new CompareInputBoundarySpy();
        CompareController controller = new CompareController(spy);

        controller.switchToMainMenu();

        assertTrue(spy.switchCalled);
    }
}
