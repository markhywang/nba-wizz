package use_case.generate_insights;

import entity.Player;
import entity.AIInsight;
import entity.Team;
import entity.SeasonStats;

import java.time.LocalDateTime;
import java.util.Optional;

import java.util.HashSet;
import java.util.Set;

public class GenerateInsightsInteractor implements GenerateInsightsInputBoundary {
    private final GenerateInsightsDataAccessInterface dataAccess;
    private final GenerateInsightsOutputBoundary presenter;

    public GenerateInsightsInteractor(GenerateInsightsDataAccessInterface dataAccess, GenerateInsightsOutputBoundary presenter) {
        this.dataAccess = dataAccess;
        this.presenter = presenter;
    }

    @Override
    public void execute(GenerateInsightsInputData inputData) {
        if (inputData.entityName() == null || inputData.entityName().trim().isEmpty()) {
            presenter.prepareFailView("Player or team name cannot be empty.");
            return;
        }

        presenter.prepareLoadingView();

        if ("Player".equalsIgnoreCase(inputData.entityType())) {
            Optional<Player> playerOptional = dataAccess.getPlayerByName(inputData.entityName());
            if (playerOptional.isPresent()) {
                Player player = playerOptional.get();
                String prompt = createPlayerPrompt(player);
                try {
                    String insightText = dataAccess.getAiInsight(prompt);
                    AIInsight insight = new AIInsight(1, "Player", player.getName(), insightText, LocalDateTime.now());
                    GenerateInsightsOutputData outputData = new GenerateInsightsOutputData(insight, false);
                    presenter.prepareSuccessView(outputData);
                } catch (Exception e) {
                    presenter.prepareFailView(e.getMessage());
                }
            } else {
                presenter.prepareFailView("Player not found.");
            }
        } else if ("Team".equalsIgnoreCase(inputData.entityType())) {
            Optional<Team> teamOptional = dataAccess.getTeamByName(inputData.entityName());
            if (teamOptional.isPresent()) {
                Team team = teamOptional.get();
                String prompt = createTeamPrompt(team);
                try {
                    String insightText = dataAccess.getAiInsight(prompt);
                    AIInsight insight = new AIInsight(1, "Team", team.getName(), insightText, LocalDateTime.now());
                    GenerateInsightsOutputData outputData = new GenerateInsightsOutputData(insight, false);
                    presenter.prepareSuccessView(outputData);
                } catch (Exception e) {
                    presenter.prepareFailView(e.getMessage());
                }
            } else {
                presenter.prepareFailView("Team not found.");
            }
        } else {
            presenter.prepareFailView("Invalid entity type.");
        }
    }

    private String createPlayerPrompt(Player player) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are a basketball analyst. Provide a detailed insight (2-3 paragraphs) into the strengths, weaknesses, and play-style of the following player. ");
        prompt.append("Use the provided statistics from all available seasons as a foundation, but also incorporate your own basketball knowledge about the player's career, impact, and reputation.\n\n");
        prompt.append("Player: ").append(player.getName()).append("\n");
        prompt.append("Position: ").append(player.getPosition()).append("\n");

        if (player.getCareerStats() != null && !player.getCareerStats().isEmpty()) {
            prompt.append("Career Statistics (Season by Season):\n");
            for (SeasonStats stats : player.getCareerStats()) {
                prompt.append("- Season ").append(stats.getSeasonYear()).append(": ");
                prompt.append(String.format("PTS: %.1f, AST: %.1f, TRB: %.1f, FG%%: %.1f%%, 3P%%: %.1f%%, GP: %d\n",
                        stats.getPointsPerGame(),
                        stats.getAssistsPerGame(),
                        stats.getReboundsPerGame(),
                        stats.getFieldGoalPercentage(),
                        stats.getThreePointPercentage(),
                        stats.getGamesPlayed()));
            }
        }

        prompt.append("\nAnalysis:");
        return prompt.toString();
    }

    private String createTeamPrompt(Team team) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are a basketball analyst. Provide a detailed insight (2-3 paragraphs) into the strengths and weaknesses of the following team based on its roster. ");
        prompt.append("Use the provided roster as a foundation, but also incorporate your own basketball knowledge about the team's performance, coaching, and history.\n\n");
        prompt.append("Team: ").append(team.getName()).append("\n");
        prompt.append("Roster:\n");

        if (team.getPlayers() != null && !team.getPlayers().isEmpty()) {
            Set<Integer> processedPlayerIds = new HashSet<>();
            for (Player player : team.getPlayers()) {
                if (processedPlayerIds.contains(player.getPlayerID())) {
                    continue;
                }
                processedPlayerIds.add(player.getPlayerID());
                
                prompt.append("- ").append(player.getName()).append(" (").append(player.getPosition()).append(")");
                // Add current/latest stats for the player if available to give more context
                if (player.getCareerStats() != null && !player.getCareerStats().isEmpty()) {
                    SeasonStats lastSeason = player.getCareerStats().get(player.getCareerStats().size() - 1);
                    prompt.append(String.format(" - Last Season: PTS: %.1f, AST: %.1f, TRB: %.1f",
                            lastSeason.getPointsPerGame(), lastSeason.getAssistsPerGame(), lastSeason.getReboundsPerGame()));
                }
                prompt.append("\n");
            }
        }

        prompt.append("\nAnalysis:");
        return prompt.toString();
    }
}
