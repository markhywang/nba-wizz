package use_case.filter_players;

import java.util.List;

/**
 * @param rows Table-friendly rows: [Name, Team, Pos, Seasons]
 */
public record FilterPlayersOutputData(List<String[]> rows) {
}