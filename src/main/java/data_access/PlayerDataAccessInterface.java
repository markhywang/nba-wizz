package data_access;

import entity.Player;
import entity.Normalization;
import java.util.List;
import java.util.Map;

public interface PlayerDataAccessInterface extends DataAccessInterface<Player> {
    Player getPlayerByName(String playerName);
    List<Player> findByTeam(String teamName);
    List<Player> findByPosition(String position);
    List<Player> findBySeason(int seasonYear);

    /**
     * Returns a map of aggregated for a given player across a season range.
     * Keys = metric names such as "PTS", "TRB", "AST"
     * Values = aggregated values
     */
    Map<String, Double> getAggregatedMetrics (
            String playerName,
            int seasonStartInclusive,
            int seasonEndInclusive,
            Normalization normalization,
            List<String> metrics
    );

    Map<String, Double> getAggregateMetrics(
            String playerName,
            int seasonStartInclusive,
            int seasonEndInclusive,
            Normalization normalization,
            List<String> metrics);
}


