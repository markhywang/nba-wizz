package entity;

import java.util.List;

public class Player {
    private final int playerID;
    private final String name;
    private Team team;
    private String position;
    private int age;
    private double height;
    private double weight;
    private List<SeasonStats> careerStats;

    public Player(int playerID, String name, Team team, String position, int age, double height, double weight, List<SeasonStats> careerStats) {
        this.playerID = playerID;
        this.name = name;
        this.team = team;
        this.position = position;
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.careerStats = careerStats;
    }

    // Getters
    public int getPlayerID() {
        return playerID;
    }

    public String getName() {
        return name;
    }

    public Team getTeam() {
        return team;
    }

    public String getPosition() {
        return position;
    }

    public List<SeasonStats> getCareerStats() {
        return careerStats;
    }

    // Setters
    public void setTeam(Team team) {
        this.team = team;
    }

}
