package data_access;

import entity.Player;
import java.util.List;

public interface PlayerDataAccessInterface extends DataAccessInterface<Player> {
    Player getPlayerByName(String playerName);
    List<Player> findByTeam(String teamName);
    List<Player> findByPosition(String position);
    List<Player> findBySeason(int seasonYear);
}
