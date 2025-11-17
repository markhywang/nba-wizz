package use_case.filter_players;

import data_access.PlayerDataAccessInterface;
import entity.Player;
import entity.SeasonStats;

import java.util.*;
import java.util.stream.Collectors;

public class FilterPlayersInteractor implements FilterPlayersInputBoundary {
    private static final int PAGE_LIMIT = 100;

    private final PlayerDataAccessInterface playerDAO;
    private final FilterPlayersOutputBoundary presenter;

    public FilterPlayersInteractor(PlayerDataAccessInterface playerDAO,
                                   FilterPlayersOutputBoundary presenter) {
        this.playerDAO = playerDAO;
        this.presenter = presenter;
    }

    @Override
    public void execute(FilterPlayersInputData in) {
        List<Player> all;
        try {
            all = playerDAO.findAll();
        } catch (Exception e) {
            presenter.presentError("Unable to load results. Please try again.");
            return;
        }

        // Validate seasons early (non-blocking warning if impossible combination)
        if (in.getSeasonMin().isPresent() && in.getSeasonMax().isPresent()
                && in.getSeasonMin().get() > in.getSeasonMax().get()) {
            presenter.presentWarning("Season min is greater than max.");
        }

        final int min = in.getSeasonMin().orElse(Integer.MIN_VALUE);
        final int max = in.getSeasonMax().orElse(Integer.MAX_VALUE);

        // Filter pipeline
        List<Player> filtered = all.stream()
                .filter(p -> in.getTeams().isEmpty() || in.getTeams().contains(safeTeamCode(p)))
                .filter(p -> in.getPositions().isEmpty() || in.getPositions().contains(safePos(p)))
                .filter(p -> {
                    if (!in.getSeasonMin().isPresent() && !in.getSeasonMax().isPresent()) return true;
                    return seasons(p).stream().anyMatch(y -> y >= min && y <= max);
                })
                .collect(Collectors.toList());

        // Empty result
        if (filtered.isEmpty()) {
            presenter.presentEmptyState("No players match your filters. Adjust filters and try again.");
            return;
        }

        // Build table rows
        List<String[]> rows = filtered.stream()
                .map(p -> new String[]{
                        safeName(p),
                        safeTeamCode(p),
                        safePos(p),
                        compressSeasons(seasons(p)) // e.g., "2018–2020, 2022"
                })
                .collect(Collectors.toList());

        // Large result notice
        if (rows.size() > PAGE_LIMIT) {
            presenter.presentLargeResultNotice(
                    new FilterPlayersOutputData(rows.subList(0, PAGE_LIMIT)),
                    "Displaying first " + PAGE_LIMIT + " of " + rows.size() + " players."
            );
        } else {
            presenter.present(new FilterPlayersOutputData(rows));
        }
    }

    @Override
    public void clear() {
        // return full list by default
        List<Player> all = playerDAO.findAll();
        List<String[]> rows = all.stream()
                .map(p -> new String[]{
                        safeName(p), safeTeamCode(p), safePos(p), compressSeasons(seasons(p))
                }).collect(Collectors.toList());
        presenter.cleared();
        presenter.present(new FilterPlayersOutputData(rows));
    }

    // Helpers
    private static String safeName(Player p) { try { return p.getName(); } catch(Exception e){ return ""; } }
    private static String safePos(Player p) { try { return p.getPosition(); } catch(Exception e){ return ""; } }
    private static String safeTeamCode(Player p) {
        try { return p.getTeam() != null ? p.getTeam().getName() : ""; } catch(Exception e){ return ""; }
    }
    private static List<Integer> seasons(Player p) {
        try {
            List<SeasonStats> cs = p.getCareerStats();
            if (cs == null) return List.of();
            return cs.stream().map(SeasonStats::getSeasonYear).collect(Collectors.toList());
        } catch (Exception e) { return List.of(); }
    }
    private static String compressSeasons(List<Integer> years) {
        if (years.isEmpty()) return "";
        List<Integer> ys = years.stream().distinct().sorted().collect(Collectors.toList());
        List<String> parts = new ArrayList<>();
        int start = ys.get(0), prev = ys.get(0);
        for (int i = 1; i < ys.size(); i++) {
            int y = ys.get(i);
            if (y == prev + 1) { prev = y; continue; }
            parts.add(rangeStr(start, prev));
            start = prev = y;
        }
        parts.add(rangeStr(start, prev));
        return String.join(", ", parts);
    }
    private static String rangeStr(int a, int b) { return a == b ? String.valueOf(a) : (a + "–" + b); }
}