package use_case.generate_insights;

import data_access.GenerateInsightsDataAccessInterface;
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
        if ("Player".equalsIgnoreCase(inputData.getEntityType())) {
            Optional<Player> playerOptional = dataAccess.getPlayerByName(inputData.getEntityName());
            if (playerOptional.isPresent()) {
                Player player = playerOptional.get();
                String prompt = createPlayerPrompt(player);
                String insightText = dataAccess.getAiInsight(prompt);
                AIInsight insight = new AIInsight(1, "Player", player.getName(), insightText, LocalDateTime.now());
                GenerateInsightsOutputData outputData = new GenerateInsightsOutputData(insight, false);
                presenter.prepareSuccessView(outputData);
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
        prompt.append("Generate a short insight about the NBA player ").append(player.getName()).append(".\\n");
        prompt.append("Here are some of his stats from his last season:\\n");

        if (player.getCareerStats() != null && !player.getCareerStats().isEmpty()) {
            SeasonStats lastSeason = player.getCareerStats().get(player.getCareerStats().size() - 1);
            prompt.append("Season: ").append(lastSeason.getSeasonYear()).append("\\n");
            prompt.append("Points per game: ").append(lastSeason.getPointsPerGame()).append("\\n");
            prompt.append("Assists per game: ").append(lastSeason.getAssistsPerGame()).append("\\n");
            prompt.append("Rebounds per game: ").append(lastSeason.getReboundsPerGame()).append("\\n");
            prompt.append("Field goal percentage: ").append(lastSeason.getFieldGoalPercentage()).append("\\n");
        }

        prompt.append("\\nBased on these stats, what can you tell me about his performance and what do you predict for his future performance in the next season?");
        return prompt.toString();
    }

    private String createTeamPrompt(Team team) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Generate a short insight about the NBA team ").append(team.getName()).append(".\\n");
        prompt.append("Here is their roster:\\n");

        if (team.getPlayers() != null && !team.getPlayers().isEmpty()) {
            for (Player player : team.getPlayers()) {
                prompt.append("- ").append(player.getName()).append("\\n");
            }
        }

        prompt.append("\\nBased on this roster, what can you tell me about the team's performance and what do you predict for their performance in the next season?");
        return prompt.toString();
    }
}
