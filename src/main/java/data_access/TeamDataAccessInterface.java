package data_access;

import entity.Team;
import java.util.List;

public interface TeamDataAccessInterface extends DataAccessInterface<Team> {
    List<Team> findByConference(String conference);
}
