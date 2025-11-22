package use_case.compare;

import data_access.PlayerDataAccessInterface;
import data_access.TeamDataAccessInterface;
import entity.Normalization;

import java.util.*;

public class CompareInteractor implements CompareInputBoundary {

    private final PlayerDataAccessInterface playerDAO;
    private final TeamDataAccessInterface teamDAO;
    private final CompareOutputBoundary presenter;

    // Which metrics belong to which preset (must match CSV columns).
    private static final Map<String, List<String>> PRESETS = Map.of(
            "Basic", List.of("PTS", "TRB", "AST", "STL", "BLK"),
            "Efficiency", List.of("FG%", "3P%", "FT%")
    );

    public CompareInteractor(PlayerDataAccessInterface playerDAO,
                             TeamDataAccessInterface teamDAO,
                             CompareOutputBoundary presenter) {
        this.playerDAO = playerDAO;
        this.teamDAO = teamDAO;
        this.presenter = presenter;
    }

    @Override
    public void execute(CompareInputData in) {
        List<String> names = in.getEntities();
        if (names == null || names.size() < 2) {
            presenter.presentError("Select at least two players or teams.");
            return;
        }
        if (names.size() > 5) {
            presenter.presentError("You can compare at most five.");
            return;
        }
        if (in.getSeasonStart() > in.getSeasonEnd()) {
            presenter.presentError("Season start must be <= season end.");
            return;
        }

        // CHeck that each entered name exits
        if (in.getEntityType() == CompareInputData.EntityType.PLAYER) {
            for (String name : names) {
                // case-insensitive
                if (playerDAO.getPlayerByName(name) == null) {
                    presenter.presentError("Player " + name + " not found.");
                    return;
                }
            }
        } else { // Team
            for (String name : names) {
                if (teamDAO.getTeamByName(name) == null) {
                    presenter.presentError("Team " + name + " not found.");
                    return;
                }
            }
        }

        List<String> metrics =
                PRESETS.getOrDefault(in.getStatPreset(), PRESETS.get("Basic"));

        List<Map<String, Double>> perEntity = new ArrayList<>();
        List<String> notices = new ArrayList<>();

        for (String name : names) {
            Map<String, Double> stats;
            try {
                if (in.getEntityType() == CompareInputData.EntityType.PLAYER) {
                    stats = playerDAO.getAggregatedMetrics(
                            name,
                            in.getSeasonStart(),
                            in.getSeasonEnd(),
                            in.getNormalization(),
                            metrics);
                } else {
                    stats = teamDAO.getAggregatedMetrics(
                            name,
                            in.getSeasonStart(),
                            in.getSeasonEnd(),
                            in.getNormalization(),
                            metrics);
                }
            } catch (UnsupportedOperationException uoe) {
                presenter.presentError("Comparison not supported by data source.");
                return;
            } catch (Exception e) {
                e.printStackTrace();
                stats = Collections.emptyMap();
                notices.add("Could not load data for " + name + ".");
            }
            perEntity.add(stats);
        }

        // Build rows
        List<CompareOutputData.Row> rows = new ArrayList<>();
        for (String metric : metrics) {
            List<Double> values = new ArrayList<>();
            for (Map<String, Double> m : perEntity) {
                values.add(m.getOrDefault(metric, null));
            }

            Integer bestIdx = null;
            double best = Double.NEGATIVE_INFINITY;
            for (int i = 0; i < values.size(); i++) {
                Double v = values.get(i);
                if (v != null && v > best) {
                    best = v;
                    bestIdx = i;
                }
            }
            rows.add(new CompareOutputData.Row(metric, values, bestIdx));
        }

        String seasonLabel = (in.getSeasonStart() == in.getSeasonEnd())
                ? String.valueOf(in.getSeasonStart())
                : in.getSeasonStart() + "–" + in.getSeasonEnd();

        CompareOutputData out = new CompareOutputData(
                names, seasonLabel, rows, notices, null);

        presenter.present(out);
    }

    @Override
    public void switchToMainMenu() {
        presenter.switchToMainMenu();
    }
}
