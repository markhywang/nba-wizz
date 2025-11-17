package use_case.compare_players;

import entity.Answer;

public class ComparePlayersOutputData {
    private final Answer comparison;
    private final boolean useCaseFailed;

    public ComparePlayersOutputData(Answer comparison, boolean useCaseFailed) {
        this.comparison = comparison;
        this.useCaseFailed = useCaseFailed;
    }

    public Answer getComparison() {
        return comparison;
    }
}
