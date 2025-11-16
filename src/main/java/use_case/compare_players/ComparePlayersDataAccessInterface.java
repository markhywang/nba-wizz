package use_case.compare_players;

import entity.Player;
import java.util.Optional;

public interface ComparePlayersDataAccessInterface {
    Optional<Player> getPlayerByName(String playerName);
    String getPlayerComparison(Player player1, Player player2);
}
