package data_access;

import entity.Player;
import java.util.List;

public interface PlayerDataAccessInterface extends DataAccessInterface<Player> {
    List<Player> findByTeam(String teamName);
    List<Player> findByPosition(String position);
    List<Player> findBySeason(int seasonYear);
}
