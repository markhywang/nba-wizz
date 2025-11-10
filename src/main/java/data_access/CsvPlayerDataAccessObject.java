package data_access;

import entity.Player;
import entity.SeasonStats;
import entity.Team;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CsvPlayerDataAccessObject implements PlayerDataAccessInterface {
    private final String csvFile;
    private final List<Player> players = new ArrayList<>();
    private final Map<String, Player> playerMap = new HashMap<>();
    private int nextPlayerId = 1;

    public CsvPlayerDataAccessObject(String csvFile) {
        this.csvFile = csvFile;
        load();
    }

    private void load() {
        String line;
        String cvsSplitBy = ",";

        try (InputStream inputStream = getClass().getResourceAsStream("/data/" + csvFile);
             BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            br.readLine(); // Skip header
            while ((line = br.readLine()) != null) {
                String[] data = line.split(cvsSplitBy);

                try {
                    String name = data[0];
                    String pos = data[1];
                    int age = Integer.parseInt(data[2]);
                    String teamName = data[3];
                    int season = Integer.parseInt(data[4]);
                    int gamesPlayed = Integer.parseInt(data[5]);
                    double minutesPlayed = Double.parseDouble(data[6]);
                    double fgPercentage = Double.parseDouble(data[7]);
                    double threePtPercentage = Double.parseDouble(data[8]);
                    double ftPercentage = Double.parseDouble(data[9]);
                    double trb = Double.parseDouble(data[10]);
                    double ast = Double.parseDouble(data[11]);
                    double tov = Double.parseDouble(data[12]);
                    double stl = Double.parseDouble(data[13]);
                    double blk = Double.parseDouble(data[14]);
                    double pf = Double.parseDouble(data[15]);
                    double pts = Double.parseDouble(data[16]);

                    Player player = playerMap.get(name);
                    if (player == null) {
                        // Create a dummy team for now.
                        Team team = new Team(0, teamName, "N/A", new ArrayList<>(), 0, 0, "N/A", new HashMap<>());
                        player = new Player(nextPlayerId++, name, team, pos, age, 0, 0, new ArrayList<>());
                        playerMap.put(name, player);
                        players.add(player);
                    }

                    SeasonStats seasonStats = new SeasonStats(season, pts, ast, trb, fgPercentage, gamesPlayed, minutesPlayed, threePtPercentage, player);
                    player.getCareerStats().add(seasonStats);

                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    System.out.println("Skipping malformed line: " + line);
                }
            }
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save(Player entity) {
        players.add(entity);
        // In a real application, you would write to the CSV file.
    }

    @Override
    public Optional<Player> findById(int id) {
        return players.stream().filter(player -> player.getPlayerID() == id).findFirst();
    }

    @Override
    public List<Player> findAll() {
        return new ArrayList<>(players);
    }

    @Override
    public void update(Player entity) {
        // Find the player and update their details.
    }

    @Override
    public void delete(Player entity) {
        players.remove(entity);
    }

    @Override
    public List<Player> findByTeam(String teamName) {
        // TODO: Implement this method
        return new ArrayList<>();
    }

    @Override
    public List<Player> findByPosition(String position) {
        // TODO: Implement this method
        return new ArrayList<>();
    }

    @Override
    public List<Player> findBySeason(int seasonYear) {
        // TODO: Implement this method
        return new ArrayList<>();
    }
}
