package data_access;

import entity.Player;
import entity.Team;

import java.util.Optional;

public interface GenerateInsightsDataAccessInterface {
    Optional<Player> getPlayerByName(String playerName);
    Optional<Team> getTeamByName(String teamName);
    String getAiInsight(String prompt);
}
