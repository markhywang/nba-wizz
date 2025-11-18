package data_access;

import use_case.favourite.FavouriteDataAccessInterface;

import java.io.*;
import java.util.ArrayList;

public class FavouriteDataAccessObject implements FavouriteDataAccessInterface {

    private final ArrayList<String> favourites = new ArrayList<>();
    private final String filePath = "src/main/java/data/favourites.txt";

    public FavouriteDataAccessObject() {
        loadFromFile();
    }

    @Override
    public void add(String playerName) {
        favourites.add(playerName);
    }

    @Override
    public void remove(String playerName) {
        favourites.remove(playerName);
    }

    @Override
    public boolean isFavourite(String playerName) {
        return favourites.contains(playerName);
    }

    @Override
    public ArrayList<String> getFavourites() {
        return new ArrayList<>(favourites);
    }

    @Override
    public void save() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
            for (String name : favourites) {
                writer.write(name);
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void loadFromFile() {
        try {
            System.out.println("Working directory: " + new java.io.File(".").getAbsolutePath());
            System.out.println("Resolved favourites path: " + new java.io.File("src/main/java/data/favourites.txt").getAbsolutePath());
            File file = new File(filePath);
            if (!file.exists()) {
                System.out.println("No favourites.txt file in data folder.");
                return;
            }

            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty() && !favourites.contains(line.trim())) {
                    favourites.add(line.trim());
                }
            }
            reader.close();
        } catch (IOException ignored) { }
    }
}
