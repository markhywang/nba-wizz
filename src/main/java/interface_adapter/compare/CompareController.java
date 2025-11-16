package interface_adapter.compare;

import entity.Normalization;
import use_case.compare.CompareInputBoundary;
import use_case.compare.CompareInputData;

import java.util.List;

public class CompareController {

    private final CompareInputBoundary compareInputBoundary;

    public CompareController(CompareInputBoundary compareInputBoundary) {
        this.compareInputBoundary = compareInputBoundary;
    }

    public void comparePlayers(List<String> players, int start, int end, String preset, Normalization norm) {
        compareInputBoundary.execute(new CompareInputData(CompareInputData.EntityType.PLAYER, players, start, end, preset, norm));
    }

    public void compareTeams(List<String> teams, int start, int end, String preset, Normalization norm) {
        compareInputBoundary.execute(new CompareInputData(CompareInputData.EntityType.TEAM, teams, start, end, preset, norm));
    }

}
