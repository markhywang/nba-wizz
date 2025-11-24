package use_case.filter_players;

import data_access.PlayerDataAccessInterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class FilterPlayersInteractor implements FilterPlayersInputBoundary {

    private static final int PAGE_LIMIT = 200;
    private static final String CSV_RESOURCE = "/data/PlayerStatsDataset.csv";

    @SuppressWarnings("unused")
    private final PlayerDataAccessInterface playerDAO; // kept to respect constructor signature
    private final FilterPlayersOutputBoundary presenter;

    // Cached rows from CSV. Each row has length 17, same order as the header.
    private final List<String[]> allRows;

    public FilterPlayersInteractor(PlayerDataAccessInterface playerDAO,
                                   FilterPlayersOutputBoundary presenter) {
        this.playerDAO = playerDAO;
        this.presenter = presenter;
        this.allRows = loadAllRows();
    }

    @Override
    public void execute(FilterPlayersInputData inputData) {
        if (allRows.isEmpty()) {
            presenter.presentError("Player stats data is not available.");
            return;
        }

        Set<String> teams = inputData.getTeams();
        Set<String> positions = inputData.getPositions();
        Optional<Integer> seasonMin = inputData.getSeasonMin();
        Optional<Integer> seasonMax = inputData.getSeasonMax();

        List<String[]> result = new ArrayList<>();

        for (String[] row : allRows) {
            if (row == null || row.length < 17) {
                continue;
            }

            String team = row[3];   // Team column
            String pos = row[1];    // Pos column
            String seasonStr = row[4]; // Season column

            int seasonYear = tryParseInt(seasonStr);

            if (!matchesTeam(team, teams)) {
                continue;
            }
            if (!matchesPosition(pos, positions)) {
                continue;
            }
            if (!matchesSeason(seasonYear, seasonMin, seasonMax)) {
                continue;
            }

            // Keep the full 17-column row as-is
            result.add(Arrays.copyOf(row, 17));

            if (result.size() >= PAGE_LIMIT) {
                break;
            }
        }

        if (result.isEmpty()) {
            presenter.presentEmptyState("No players match the selected filters.");
        } else if (result.size() >= PAGE_LIMIT) {
            presenter.presentLargeResultNotice(
                    new FilterPlayersOutputData(result),
                    "Showing first " + result.size() + " matching rows."
            );
        } else {
            presenter.present(new FilterPlayersOutputData(result));
        }
    }

    @Override
    public void clear() {
        presenter.cleared();
    }

    // ---------- Helpers ----------

    private List<String[]> loadAllRows() {
        List<String[]> rows = new ArrayList<>();

        try (InputStream inputStream = getClass().getResourceAsStream(CSV_RESOURCE)) {
            if (inputStream == null) {
                presenter.presentError("Could not find CSV resource: " + CSV_RESOURCE);
                return Collections.emptyList();
            }

            try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                // Skip header
                br.readLine();

                while ((line = br.readLine()) != null) {
                    // Simple CSV split; dataset does not contain commas inside fields.
                    String[] tokens = line.split(",", -1);
                    if (tokens.length < 17) {
                        continue;
                    }
                    rows.add(Arrays.copyOf(tokens, 17));
                }
            }
        } catch (IOException e) {
            presenter.presentError("Failed to load player stats data.");
            return Collections.emptyList();
        }

        return rows;
    }

    private boolean matchesTeam(String team, Set<String> teams) {
        if (teams == null || teams.isEmpty()) {
            return true;
        }
        if (team == null) {
            return false;
        }
        for (String t : teams) {
            if (t != null && !t.isEmpty() && team.equalsIgnoreCase(t)) {
                return true;
            }
        }
        return false;
    }

    private boolean matchesPosition(String pos, Set<String> positions) {
        if (positions == null || positions.isEmpty()) {
            return true;
        }
        if (pos == null) {
            return false;
        }
        for (String p : positions) {
            if (p != null && !p.isEmpty() && pos.equalsIgnoreCase(p)) {
                return true;
            }
        }
        return false;
    }

    private boolean matchesSeason(int seasonYear,
                                  Optional<Integer> seasonMin,
                                  Optional<Integer> seasonMax) {
        if (!seasonMin.isPresent() && !seasonMax.isPresent()) {
            return true;
        }
        if (seasonYear == Integer.MIN_VALUE) {
            return false;
        }
        if (seasonMin.isPresent() && seasonYear < seasonMin.get()) {
            return false;
        }
        if (seasonMax.isPresent() && seasonYear > seasonMax.get()) {
            return false;
        }
        return true;
    }

    private int tryParseInt(String value) {
        if (value == null || value.isEmpty()) {
            return Integer.MIN_VALUE;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return Integer.MIN_VALUE;
        }
    }
}
