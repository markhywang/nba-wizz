package use_case.generate_insights;

import use_case.generate_insights.GenerateInsightsDataAccessInterface;
import entity.Player;
import entity.AIInsight;
import entity.Team;
import entity.SeasonStats;

import java.time.LocalDateTime;
import java.util.Optional;

public class GenerateInsightsInteractor implements GenerateInsightsInputBoundary {
    private final GenerateInsightsDataAccessInterface dataAccess;
    private final GenerateInsightsOutputBoundary presenter;

    public GenerateInsightsInteractor(GenerateInsightsDataAccessInterface dataAccess, GenerateInsightsOutputBoundary presenter) {
        this.dataAccess = dataAccess;
        this.presenter = presenter;
    }

    @Override
    public void execute(GenerateInsightsInputData inputData) {
        if (inputData.getEntityName() == null || inputData.getEntityName().trim().isEmpty()) {
            presenter.prepareFailView("Player or team name cannot be empty.");
            return;
        }

        if ("Player".equalsIgnoreCase(inputData.getEntityType())) {
            Optional<Player> playerOptional = dataAccess.getPlayerByName(inputData.getEntityName());
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
        } else if ("Team".equalsIgnoreCase(inputData.getEntityType())) {
            Optional<Team> teamOptional = dataAccess.getTeamByName(inputData.getEntityName());
            if (teamOptional.isPresent()) {
                Team team = teamOptional.get();
                String prompt = createTeamPrompt(team);
                String insightText = dataAccess.getAiInsight(prompt);
                AIInsight insight = new AIInsight(1, "Team", team.getName(), insightText, LocalDateTime.now());
                GenerateInsightsOutputData outputData = new GenerateInsightsOutputData(insight, false);
                presenter.prepareSuccessView(outputData);
            } else {
                presenter.prepareFailView("Team not found.");
            }
        } else {
            presenter.prepareFailView("Invalid entity type.");
        }
    }

    private String createPlayerPrompt(Player player) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are a basketball analyst. Provide a concise insight into the strengths and weaknesses of the following player based on their recent season statistics. ");
        prompt.append("Keep your analysis to a maximum of three sentences.\n\n");
        prompt.append("Player: ").append(player.getName()).append("\n");
        prompt.append("Position: ").append(player.getPosition()).append("\n");

        if (player.getCareerStats() != null && !player.getCareerStats().isEmpty()) {
            SeasonStats lastSeason = player.getCareerStats().get(player.getCareerStats().size() - 1);
            prompt.append("Season: ").append(lastSeason.getSeasonYear()).append("\n");
            prompt.append(String.format("Points per game: %.2f\n", lastSeason.getPointsPerGame()));
            prompt.append(String.format("Assists per game: %.2f\n", lastSeason.getAssistsPerGame()));
            prompt.append(String.format("Rebounds per game: %.2f\n", lastSeason.getReboundsPerGame()));
            prompt.append(String.format("Field goal percentage: %.2f%%\n", lastSeason.getFieldGoalPercentage() * 100));
            prompt.append(String.format("Three-point percentage: %.2f%%\n", lastSeason.getThreePointPercentage() * 100));
        }

        prompt.append("\nAnalysis:");
        return prompt.toString();
    }

    private String createTeamPrompt(Team team) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are a basketball analyst. Provide a concise insight into the strengths and weaknesses of the following team based on its roster. ");
        prompt.append("Keep your analysis to a maximum of three sentences.\n\n");
        prompt.append("Team: ").append(team.getName()).append("\n");
        prompt.append("Roster:\n");

        if (team.getPlayers() != null && !team.getPlayers().isEmpty()) {
            for (Player player : team.getPlayers()) {
                prompt.append("- ").append(player.getName()).append(" (").append(player.getPosition()).append(")\n");
            }
        }

        prompt.append("\nAnalysis:");
        return prompt.toString();
    }
}
