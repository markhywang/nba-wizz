package use_case.generate_insights;

import entity.Player;
import entity.Team;

import java.util.Optional;

/**
 * Data access interface used by the GenerateInsights use case.
 * Placing this interface in the use_case layer follows Clean Architecture
 * by letting the interactor depend on an abstraction that the data layer
 * implements.
 */
public interface GenerateInsightsDataAccessInterface {
    Optional<Player> getPlayerByName(String playerName);
    Optional<Team> getTeamByName(String teamName);
    String getAiInsight(String prompt);
}
