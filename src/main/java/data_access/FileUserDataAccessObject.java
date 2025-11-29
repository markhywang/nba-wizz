package data_access;

import entity.User;
import use_case.authentication.UserDataAccessInterface;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * Simple file-backed user repository that persists credentials between runs.
 */
public class FileUserDataAccessObject implements UserDataAccessInterface {

    private final Map<String, User> users = new HashMap<>();
    private final Path storagePath;

    public FileUserDataAccessObject(String relativePath) {
        this.storagePath = Path.of(relativePath);
        loadFromDisk();
    }

    @Override
    public synchronized boolean existsByUsername(String username) {
        if (username == null) {
            return false;
        }
        return users.containsKey(normalized(username));
    }

    @Override
    public synchronized Optional<User> findByUsername(String username) {
        if (username == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(users.get(normalized(username)));
    }

    @Override
    public synchronized void save(User user) {
        users.put(normalized(user.getUsername()), user);
        persistToDisk();
    }

    private void loadFromDisk() {
        try {
            if (storagePath.getParent() != null) {
                Files.createDirectories(storagePath.getParent());
            }
            if (!Files.exists(storagePath)) {
                Files.createFile(storagePath);
                return;
            }
            for (String line : Files.readAllLines(storagePath, StandardCharsets.UTF_8)) {
                if (line.trim().isEmpty() || !line.contains(",")) {
                    continue;
                }
                String[] parts = line.split(",", 2);
                String username = parts[0].trim();
                String passwordHash = parts[1].trim();
                if (!username.isEmpty() && !passwordHash.isEmpty()) {
                    users.put(normalized(username), new User(username, passwordHash));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load users database.", e);
        }
    }

    private void persistToDisk() {
        try (BufferedWriter writer = Files.newBufferedWriter(storagePath, StandardCharsets.UTF_8)) {
            for (User user : users.values()) {
                writer.write(user.getUsername() + "," + user.getPasswordHash());
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to persist users database.", e);
        }
    }

    private String normalized(String username) {
        return username.trim().toLowerCase(Locale.ROOT);
    }
}


