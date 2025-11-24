package use_case.filter_players;

import data_access.PlayerDataAccessInterface;
import entity.Player;
import entity.SeasonStats;

import java.util.*;
import java.util.stream.Collectors;

public class FilterPlayersInteractor implements FilterPlayersInputBoundary {

    // Hard cap to avoid flooding the table.
    private static final int PAGE_LIMIT = 200;

    private final PlayerDataAccessInterface playerDAO;
    private final FilterPlayersOutputBoundary presenter;

    public FilterPlayersInteractor(PlayerDataAccessInterface playerDAO,
                                   FilterPlayersOutputBoundary presenter) {
        this.playerDAO = playerDAO;
        this.presenter = presenter;
    }

    @Override
    public void execute(FilterPlayersInputData inputData) {
        List<Player> allPlayers = playerDAO.findAll();

        Set<String> teams = inputData.getTeams();
        Set<String> positions = inputData.getPositions();
        Optional<Integer> seasonMin = inputData.getSeasonMin();
        Optional<Integer> seasonMax = inputData.getSeasonMax();

        List<String[]> rows = new ArrayList<>();

        for (Player player : allPlayers) {
            if (!matchesTeam(player, teams)) {
                continue;
            }
            if (!matchesPosition(player, positions)) {
                continue;
            }
            if (!matchesSeasonRange(player, seasonMin, seasonMax)) {
                continue;
            }

            // Build the season range string for this player.
            Set<Integer> years = player.getCareerStats().stream()
                    .map(SeasonStats::getSeasonYear)
                    .collect(Collectors.toSet());

            String teamName = player.getTeam() != null ? player.getTeam().getName() : "";
            String seasonText = compressYears(years);

            rows.add(new String[]{
                    player.getName(),
                    player.getPosition(),
                    teamName,
                    seasonText
            });

            if (rows.size() >= PAGE_LIMIT) {
                break;
            }
        }

        if (rows.isEmpty()) {
            presenter.presentEmptyState("No players match the selected filters.");
        } else if (rows.size() >= PAGE_LIMIT) {
            presenter.presentLargeResultNotice(
                    new FilterPlayersOutputData(rows),
                    "Showing first " + rows.size() + " matching players."
            );
        } else {
            presenter.present(new FilterPlayersOutputData(rows));
        }
    }

    @Override
    public void clear() {
        presenter.cleared();
    }

    private boolean matchesTeam(Player player, Set<String> teams) {
        if (teams == null || teams.isEmpty()) {
            return true;
        }
        if (player.getTeam() == null) {
            return false;
        }
        String teamName = player.getTeam().getName();
        for (String t : teams) {
            if (teamName != null && teamName.equalsIgnoreCase(t)) {
                return true;
            }
        }
        return false;
    }

    private boolean matchesPosition(Player player, Set<String> positions) {
        if (positions == null || positions.isEmpty()) {
            return true;
        }
        String pos = player.getPosition();
        if (pos == null) {
            return false;
        }
        for (String p : positions) {
            if (pos.equalsIgnoreCase(p)) {
                return true;
            }
        }
        return false;
    }

    private boolean matchesSeasonRange(Player player,
                                       Optional<Integer> seasonMin,
                                       Optional<Integer> seasonMax) {
        if (!seasonMin.isPresent() && !seasonMax.isPresent()) {
            return true;
        }
        if (player.getCareerStats() == null) {
            return false;
        }

        for (SeasonStats stats : player.getCareerStats()) {
            int year = stats.getSeasonYear();
            if (seasonMin.isPresent() && year < seasonMin.get()) {
                continue;
            }
            if (seasonMax.isPresent() && year > seasonMax.get()) {
                continue;
            }
            // At least one season is within the range.
            return true;
        }
        return false;
    }

    private static String compressYears(Collection<Integer> years) {
        if (years == null || years.isEmpty()) {
            return "";
        }
        List<Integer> ys = years.stream()
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        if (ys.isEmpty()) {
            return "";
        }

        List<String> parts = new ArrayList<>();
        int start = ys.get(0);
        int prev = ys.get(0);

        for (int i = 1; i < ys.size(); i++) {
            int y = ys.get(i);
            if (y == prev + 1) {
                prev = y;
            } else {
                parts.add(rangeStr(start, prev));
                start = prev = y;
            }
        }
        parts.add(rangeStr(start, prev));
        return String.join(", ", parts);
    }

    private static String rangeStr(int a, int b) {
        return (a == b) ? String.valueOf(a) : (a + "–" + b);
    }
}
