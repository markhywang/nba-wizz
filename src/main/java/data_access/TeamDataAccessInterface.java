package data_access;

import entity.Team;
import entity.Normalization;
import java.util.List;
import java.util.Map;

public interface TeamDataAccessInterface extends DataAccessInterface<Team> {
    Team getTeamByName(String teamName);

    List<Team> findByConference(String conference);

    Map<String, Double> getAggregatedMetrics (
            String teamName,
            int seasonStartInclusive,
            int seasonEndInclusive,
            Normalization normalization,
            List<String> metrics);
}
