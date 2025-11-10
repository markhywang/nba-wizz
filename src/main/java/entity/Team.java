package entity;

import java.util.List;
import java.util.Map;

public class Team {
    private final int teamID;
    private final String name;
    private String city;
    private List<Player> players;
    private int wins;
    private int losses;
    private String conference;
    private Map<String, Double> teamStats;

    public Team(int teamID, String name, String city, List<Player> players, int wins, int losses, String conference, Map<String, Double> teamStats) {
        this.teamID = teamID;
        this.name = name;
        this.city = city;
        this.players = players;
        this.wins = wins;
        this.losses = losses;
        this.conference = conference;
        this.teamStats = teamStats;
    }

    // Getters
    public int getTeamID() {
        return teamID;
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public int getWins() {
        return wins;
    }

    public int getLosses() {
        return losses;
    }

    public String getConference() {
        return conference;
    }

    public Map<String, Double> getTeamStats() {
        return teamStats;
    }

    // Setters
    public void setCity(String city) {
        this.city = city;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    public void setConference(String conference) {
        this.conference = conference;
    }

    public void setTeamStats(Map<String, Double> teamStats) {
        this.teamStats = teamStats;
    }
}
