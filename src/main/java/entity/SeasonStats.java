package entity;

public class SeasonStats {
    private final int seasonYear;
    private double pointsPerGame;
    private double assistsPerGame;
    private double reboundsPerGame;
    private double fieldGoalPercentage;
    private int gamesPlayed;
    private double minutesPerGame;
    private double threePointPercentage;
    private final Player player;

    public SeasonStats(int seasonYear, double pointsPerGame, double assistsPerGame, double reboundsPerGame, double fieldGoalPercentage, int gamesPlayed, double minutesPerGame, double threePointPercentage, Player player) {
        this.seasonYear = seasonYear;
        this.pointsPerGame = pointsPerGame;
        this.assistsPerGame = assistsPerGame;
        this.reboundsPerGame = reboundsPerGame;
        this.fieldGoalPercentage = fieldGoalPercentage;
        this.gamesPlayed = gamesPlayed;
        this.minutesPerGame = minutesPerGame;
        this.threePointPercentage = threePointPercentage;
        this.player = player;
    }

    // Getters
    public int getSeasonYear() {
        return seasonYear;
    }

    public double getPointsPerGame() {
        return pointsPerGame;
    }

    public double getAssistsPerGame() {
        return assistsPerGame;
    }

    public double getReboundsPerGame() {
        return reboundsPerGame;
    }

    public double getFieldGoalPercentage() {
        return fieldGoalPercentage;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public double getMinutesPerGame() {
        return minutesPerGame;
    }

    public double getThreePointPercentage() {
        return threePointPercentage;
    }

    public Player getPlayer() {
        return player;
    }

    // Setters
    public void setPointsPerGame(double pointsPerGame) {
        this.pointsPerGame = pointsPerGame;
    }

    public void setAssistsPerGame(double assistsPerGame) {
        this.assistsPerGame = assistsPerGame;
    }

    public void setReboundsPerGame(double reboundsPerGame) {
        this.reboundsPerGame = reboundsPerGame;
    }

    public void setFieldGoalPercentage(double fieldGoalPercentage) {
        this.fieldGoalPercentage = fieldGoalPercentage;
    }

    public void setGamesPlayed(int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    public void setMinutesPerGame(double minutesPerGame) {
        this.minutesPerGame = minutesPerGame;
    }

    public void setThreePointPercentage(double threePointPercentage) {
        this.threePointPercentage = threePointPercentage;
    }
}
