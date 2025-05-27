package com.example.guessingnumber_fp.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Map;
import java.util.Set;

/**
 * Helper class to migrate data from SharedPreferences to SQLite database
 */
public class DatabaseMigrationHelper {
    private static final String TAG = "DatabaseMigration";
    private static final String MIGRATION_COMPLETE_KEY = "sqlite_migration_complete";
    
    private Context context;
    private SharedPreferences prefs;
    
    // DAOs
    private UserDAO userDAO;
    private ScoreDAO scoreDAO;
    private StatsDAO statsDAO;
    private SettingsDAO settingsDAO;
    
    public DatabaseMigrationHelper(Context context) {
        this.context = context;
        this.prefs = context.getSharedPreferences("game_data", Context.MODE_PRIVATE);
        
        // Initialize DAOs
        userDAO = new UserDAO(context);
        scoreDAO = new ScoreDAO(context);
        statsDAO = new StatsDAO(context);
        settingsDAO = new SettingsDAO(context);
    }
    
    /**
     * Check if migration has already been completed
     * @return true if migration is complete, false otherwise
     */
    public boolean isMigrationComplete() {
        return prefs.getBoolean(MIGRATION_COMPLETE_KEY, false);
    }
    
    /**
     * Mark migration as complete
     */
    private void markMigrationComplete() {
        prefs.edit().putBoolean(MIGRATION_COMPLETE_KEY, true).apply();
    }
    
    /**
     * Perform the migration from SharedPreferences to SQLite
     * @return true if migration was successful, false otherwise
     */
    public boolean migrateData() {
        if (isMigrationComplete()) {
            Log.i(TAG, "Migration already completed. Skipping.");
            return true;
        }
        
        try {
            Log.i(TAG, "Starting migration from SharedPreferences to SQLite...");
            
            // 1. Migrate users
            migrateUsers();
            
            // 2. Migrate settings
            migrateSettings();
            
            // 3. Migrate statistics and scores
            migrateStatsAndScores();
            
            // Mark migration as complete
            markMigrationComplete();
            
            Log.i(TAG, "Migration completed successfully!");
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Migration failed: " + e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Migrate users from SharedPreferences to SQLite
     */
    private void migrateUsers() {
        Log.i(TAG, "Migrating users...");
        
        // Get current user
        String currentUser = prefs.getString("current_user", "guest");
        
        // Create default guest user
        userDAO.createUser("guest");
        
        // Create current user if not guest
        if (!currentUser.equals("guest")) {
            userDAO.createUser(currentUser);
        }
        
        // Save current user setting
        settingsDAO.saveSetting("guest", SettingsDAO.SETTING_CURRENT_USER, currentUser);
        
        Log.i(TAG, "Users migration complete");
    }
    
    /**
     * Migrate settings from SharedPreferences to SQLite
     */
    private void migrateSettings() {
        Log.i(TAG, "Migrating settings...");
        
        String currentUser = prefs.getString("current_user", "guest");
        
        // Migrate sound settings
        boolean soundEnabled = prefs.getBoolean("sound_enabled", true);
        settingsDAO.saveBooleanSetting(currentUser, SettingsDAO.SETTING_SOUND_ENABLED, soundEnabled);
        
        // Migrate music settings
        boolean musicEnabled = prefs.getBoolean("music_enabled", true);
        settingsDAO.saveBooleanSetting(currentUser, SettingsDAO.SETTING_MUSIC_ENABLED, musicEnabled);
        
        Log.i(TAG, "Settings migration complete");
    }
    
    /**
     * Migrate statistics and scores from SharedPreferences to SQLite
     */
    private void migrateStatsAndScores() {
        Log.i(TAG, "Migrating statistics and scores...");
        
        // Get all keys from SharedPreferences
        Map<String, ?> allPrefs = prefs.getAll();
        Set<String> keys = allPrefs.keySet();
        
        // Migrate user statistics
        migrateUserStats(keys);
        
        // Migrate highscores
        migrateHighscores(keys);
        
        Log.i(TAG, "Statistics and scores migration complete");
    }
    
    /**
     * Migrate user statistics from SharedPreferences to SQLite
     * @param keys All keys from SharedPreferences
     */
    private void migrateUserStats(Set<String> keys) {
        String[] difficulties = {"easy", "medium", "hard", "impossible"};
        
        for (String key : keys) {
            // Process user statistics
            for (String difficulty : difficulties) {
                String username = extractUsername(key, "games_played_" + difficulty + "_");
                if (username != null) {
                    // Create user if not exists
                    userDAO.createUser(username);
                    
                    // Migrate statistics
                    int gamesPlayed = prefs.getInt("games_played_" + difficulty + "_" + username, 0);
                    int gamesWon = prefs.getInt("games_won_" + difficulty + "_" + username, 0);
                    int gamesLost = prefs.getInt("games_lost_" + difficulty + "_" + username, 0);
                    int hintsUsed = prefs.getInt("hints_used_" + difficulty + "_" + username, 0);
                    
                    statsDAO.incrementGamesPlayed(username, difficulty);
                    statsDAO.incrementGamesWon(username, difficulty);
                    statsDAO.incrementGamesLost(username, difficulty);
                    statsDAO.incrementHintsUsed(username, difficulty, hintsUsed);
                    
                    break;
                }
            }
        }
    }
    
    /**
     * Migrate highscores from SharedPreferences to SQLite
     * @param keys All keys from SharedPreferences
     */
    private void migrateHighscores(Set<String> keys) {
        String[] difficulties = {"easy", "medium", "hard", "impossible"};
        
        for (String key : keys) {
            // Process highscores
            for (String difficulty : difficulties) {
                String username = extractUsername(key, "highscore_" + difficulty + "_");
                if (username != null) {
                    // Create user if not exists
                    userDAO.createUser(username);
                    
                    // Migrate highscore
                    int score = prefs.getInt("highscore_" + difficulty + "_" + username, 0);
                    if (score > 0) {
                        scoreDAO.addScore(username, difficulty, score);
                    }
                    
                    break;
                }
            }
        }
    }
    
    /**
     * Extract username from a key with a specific prefix
     * @param key The key to extract from
     * @param prefix The prefix to look for
     * @return The extracted username, or null if prefix not found
     */
    private String extractUsername(String key, String prefix) {
        if (key.startsWith(prefix)) {
            return key.substring(prefix.length());
        }
        return null;
    }
    
    /**
     * Transfer all data from one username to another in SQLite database
     * @param oldUsername The source username
     * @param newUsername The target username
     * @return true if transfer was successful, false otherwise
     */
    public boolean transferUserData(String oldUsername, String newUsername) {
        Log.i(TAG, "Transferring data from " + oldUsername + " to " + newUsername);
        
        try {
            // 1. Create the new user if it doesn't exist
            userDAO.createUser(newUsername);
            
            // 2. Transfer scores
            String[] difficulties = {"easy", "medium", "hard", "impossible"};
            for (String difficulty : difficulties) {
                // Get best score for old username
                int bestScore = scoreDAO.getBestScore(oldUsername, difficulty);
                if (bestScore > 0) {
                    // Add score for new username
                    scoreDAO.addScore(newUsername, difficulty, bestScore);
                }
            }
            
            // 3. Transfer statistics
            for (String difficulty : difficulties) {
                // Get stats for old username
                Map<String, Integer> stats = statsDAO.getStats(oldUsername, difficulty);
                
                // Set stats for new username
                for (Map.Entry<String, Integer> entry : stats.entrySet()) {
                    String key = entry.getKey();
                    int value = entry.getValue();
                    
                    if (key.equals("games_played") && value > 0) {
                        // Use public method for games played
                        for (int i = 0; i < value; i++) {
                            statsDAO.incrementGamesPlayed(newUsername, difficulty);
                        }
                    } else if (key.equals("games_won") && value > 0) {
                        // Use public method for games won
                        for (int i = 0; i < value; i++) {
                            statsDAO.incrementGamesWon(newUsername, difficulty);
                        }
                    } else if (key.equals("games_lost") && value > 0) {
                        // Use public method for games lost
                        for (int i = 0; i < value; i++) {
                            statsDAO.incrementGamesLost(newUsername, difficulty);
                        }
                    } else if (key.equals("hints_used") && value > 0) {
                        // Use public method for hints used
                        statsDAO.incrementHintsUsed(newUsername, difficulty, value);
                    } else if (key.equals("best_score") && value > 0) {
                        // Use public method for best score
                        statsDAO.setStat(newUsername, difficulty, "best_score", value);
                    }
                }
            }
            
            // 4. Transfer settings
            Map<String, String> settings = settingsDAO.getAllSettings(oldUsername);
            for (Map.Entry<String, String> entry : settings.entrySet()) {
                settingsDAO.saveSetting(newUsername, entry.getKey(), entry.getValue());
            }
            
            Log.i(TAG, "Data transfer completed successfully");
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Data transfer failed: " + e.getMessage(), e);
            return false;
        }
    }
}
