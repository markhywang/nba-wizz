package data_access;

import use_case.favourite.FavouriteDataAccessInterface;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FavouriteDataAccessObject implements FavouriteDataAccessInterface {

    // Map of username -> list of favourite player names
    private final Map<String, ArrayList<String>> userFavourites = new HashMap<>();
    private String currentUser = null;
    private final String filePath = "src/main/java/data/favourites.txt";

    public FavouriteDataAccessObject() {
        loadFromFile();
    }

    @Override
    public synchronized void add(String playerName) {
        if (currentUser == null) return;
        ArrayList<String> favourites = userFavourites.computeIfAbsent(currentUser, k -> new ArrayList<>());
        if (!favourites.contains(playerName)) favourites.add(playerName);
    }

    @Override
    public synchronized void remove(String playerName) {
        if (currentUser == null) return;
        List<String> favourites = userFavourites.get(currentUser);
        if (favourites != null) favourites.remove(playerName);
    }

    @Override
    public synchronized boolean isFavourite(String playerName) {
        if (currentUser == null) return false;
        List<String> favourites = userFavourites.get(currentUser);
        return favourites != null && favourites.contains(playerName);
    }

    @Override
    public synchronized ArrayList<String> getFavourites() {
        if (currentUser == null) return new ArrayList<>();
        return new ArrayList<>(userFavourites.getOrDefault(currentUser, new ArrayList<>()));
    }

    @Override
    public synchronized void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Map.Entry<String, ArrayList<String>> entry : userFavourites.entrySet()) {
                String username = entry.getKey();
                for (String player : entry.getValue()) {
                    writer.write(username + "," + player);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public synchronized void setCurrentUser(String username) {
        if (username == null) {
            this.currentUser = null;
        } else {
            this.currentUser = username.trim().toLowerCase();
            userFavourites.computeIfAbsent(this.currentUser, k -> new ArrayList<>());
        }
    }

    private void loadFromFile() {
        File file = new File(filePath);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || !line.contains(",")) continue;
                String[] parts = line.split(",", 2);
                String username = parts[0].trim().toLowerCase();
                String player = parts[1].trim();
                if (username.isEmpty() || player.isEmpty()) continue;
                userFavourites.computeIfAbsent(username, k -> new ArrayList<>()).add(player);
            }
        } catch (IOException ignored) { }
    }
}
