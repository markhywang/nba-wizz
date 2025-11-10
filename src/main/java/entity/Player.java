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
    private int favouritesCount;

    public Player(int playerID, String name, Team team, String position, int age, double height, double weight, List<SeasonStats> careerStats) {
        this.playerID = playerID;
        this.name = name;
        this.team = team;
        this.position = position;
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.careerStats = careerStats;
        this.favouritesCount = 0; // Default
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

    public int getAge() {
        return age;
    }

    public double getHeight() {
        return height;
    }

    public double getWeight() {
        return weight;
    }

    public List<SeasonStats> getCareerStats() {
        return careerStats;
    }

    public int getFavouritesCount() {
        return favouritesCount;
    }

    // Setters
    public void setTeam(Team team) {
        this.team = team;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void setCareerStats(List<SeasonStats> careerStats) {
        this.careerStats = careerStats;
    }

    public void setFavouritesCount(int favouritesCount) {
        this.favouritesCount = favouritesCount;
    }
}
