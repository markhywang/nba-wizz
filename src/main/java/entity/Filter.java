package entity;

public class Filter {
    private final int filterID;
    private String teamName;
    private String position;
    private double minPoints;
    private double maxPoints;
    private int seasonYear;
    private String sortBy;
    private String sortOrder;

    public Filter(int filterID, String teamName, String position, double minPoints, double maxPoints, int seasonYear, String sortBy, String sortOrder) {
        this.filterID = filterID;
        this.teamName = teamName;
        this.position = position;
        this.minPoints = minPoints;
        this.maxPoints = maxPoints;
        this.seasonYear = seasonYear;
        this.sortBy = sortBy;
        this.sortOrder = sortOrder;
    }

    // Getters
    public int getFilterID() {
        return filterID;
    }

    public String getTeamName() {
        return teamName;
    }

    public String getPosition() {
        return position;
    }

    public double getMinPoints() {
        return minPoints;
    }

    public double getMaxPoints() {
        return maxPoints;
    }

    public int getSeasonYear() {
        return seasonYear;
    }

    public String getSortBy() {
        return sortBy;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    // Setters
    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setMinPoints(double minPoints) {
        this.minPoints = minPoints;
    }

    public void setMaxPoints(double maxPoints) {
        this.maxPoints = maxPoints;
    }

    public void setSeasonYear(int seasonYear) {
        this.seasonYear = seasonYear;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }
}
