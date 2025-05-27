package com.example.guessingnumber_fp.database;

import android.content.Context;
import android.util.Log;

import java.util.List;
import java.util.Map;

/**
 * Main database manager class that provides a unified interface for all database operations
 * This class serves as the entry point for all database interactions in the app
 */
public class GameDatabaseManager {
    private static final String TAG = "GameDatabaseManager";
    
    private static GameDatabaseManager instance;
    
    private Context context;
    private UserDAO userDAO;
    private ScoreDAO scoreDAO;
    private StatsDAO statsDAO;
    private SettingsDAO settingsDAO;
    private DatabaseMigrationHelper migrationHelper;
    
    // Singleton pattern
    public static synchronized GameDatabaseManager getInstance(Context context) {
        if (instance == null) {
            instance = new GameDatabaseManager(context.getApplicationContext());
        }
        return instance;
    }
    
    private GameDatabaseManager(Context context) {
        this.context = context;
        
        // Initialize DAOs
        userDAO = new UserDAO(context);
        scoreDAO = new ScoreDAO(context);
        statsDAO = new StatsDAO(context);
        settingsDAO = new SettingsDAO(context);
        
        // Initialize migration helper
        migrationHelper = new DatabaseMigrationHelper(context);
        
        // Perform migration if needed
        if (!migrationHelper.isMigrationComplete()) {
            migrationHelper.migrateData();
        }
    }
    
    /**
     * Get the current user
     * @return Current username, defaults to "guest" if not set
     */
    public String getCurrentUser() {
        return settingsDAO.getSetting("guest", SettingsDAO.SETTING_CURRENT_USER, "guest");
    }
    
    /**
     * Set the current user
     * @param username The username to set as current
     * @return true if successful, false otherwise
     */
    public boolean setCurrentUser(String username) {
        // Create user if it doesn't exist
        if (!userDAO.userExists(username)) {
            userDAO.createUser(username);
        }
        
        return settingsDAO.saveSetting("guest", SettingsDAO.SETTING_CURRENT_USER, username);
    }
    
    /**
     * Check if sound is enabled
     * @return true if sound is enabled, false otherwise
     */
    public boolean isSoundEnabled() {
        String currentUser = getCurrentUser();
        return settingsDAO.getBooleanSetting(currentUser, SettingsDAO.SETTING_SOUND_ENABLED, true);
    }
    
    /**
     * Set sound enabled/disabled
     * @param enabled true to enable sound, false to disable
     * @return true if successful, false otherwise
     */
    public boolean setSoundEnabled(boolean enabled) {
        String currentUser = getCurrentUser();
        return settingsDAO.saveBooleanSetting(currentUser, SettingsDAO.SETTING_SOUND_ENABLED, enabled);
    }
    
    /**
     * Check if music is enabled
     * @return true if music is enabled, false otherwise
     */
    public boolean isMusicEnabled() {
        String currentUser = getCurrentUser();
        return settingsDAO.getBooleanSetting(currentUser, SettingsDAO.SETTING_MUSIC_ENABLED, true);
    }
    
    /**
     * Set music enabled/disabled
     * @param enabled true to enable music, false to disable
     * @return true if successful, false otherwise
     */
    public boolean setMusicEnabled(boolean enabled) {
        String currentUser = getCurrentUser();
        return settingsDAO.saveBooleanSetting(currentUser, SettingsDAO.SETTING_MUSIC_ENABLED, enabled);
    }
    
    /**
     * Get all users
     * @return List of all usernames
     */
    public List<String> getAllUsers() {
        return userDAO.getAllUsers();
    }
    
    /**
     * Create a new user
     * @param username The username to create
     * @return true if successful, false otherwise
     */
    public boolean createUser(String username) {
        return userDAO.createUser(username);
    }
    
    /**
     * Delete a user and all associated data
     * @param username The username to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteUser(String username) {
        // Can't delete the guest user
        if ("guest".equals(username)) {
            return false;
        }
        
        // Delete all user data
        settingsDAO.deleteUserSettings(username);
        statsDAO.deleteUserStats(username);
        scoreDAO.deleteUserScores(username);
        
        // Delete the user
        return userDAO.deleteUser(username);
    }
    
    /**
     * Rename a user
     * @param oldUsername The current username
     * @param newUsername The new username
     * @return true if successful, false otherwise
     */
    public boolean renameUser(String oldUsername, String newUsername) {
        // Can't rename the guest user
        if ("guest".equals(oldUsername)) {
            return false;
        }
        
        return userDAO.renameUser(oldUsername, newUsername);
    }
    
    /**
     * Record a game result
     * @param username The username
     * @param difficulty The difficulty level
     * @param won Whether the game was won
     * @param score The score (0 if game was lost)
     * @param hintsUsed Number of hints used
     * @return true if successful, false otherwise
     */
    public boolean recordGameResult(String username, String difficulty, boolean won, int score, int hintsUsed) {
        try {
            // Update games played
            statsDAO.incrementGamesPlayed(username, difficulty);
            
            // Update games won/lost
            if (won) {
                statsDAO.incrementGamesWon(username, difficulty);
                
                // Add score only if game was won
                if (score > 0) {
                    scoreDAO.addScore(username, difficulty, score);
                }
            } else {
                statsDAO.incrementGamesLost(username, difficulty);
            }
            
            // Update hints used
            if (hintsUsed > 0) {
                statsDAO.incrementHintsUsed(username, difficulty, hintsUsed);
            }
            
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error recording game result: " + e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Get statistics for a user and difficulty
     * @param username The username
     * @param difficulty The difficulty level
     * @return Map containing all statistics
     */
    public Map<String, Integer> getStats(String username, String difficulty) {
        return statsDAO.getStats(username, difficulty);
    }
    
    /**
     * Get top scores for a difficulty
     * @param difficulty The difficulty level
     * @param limit Maximum number of scores to return
     * @return List of maps containing username and score
     */
    public List<Map<String, Object>> getTopScores(String difficulty, int limit) {
        return scoreDAO.getTopScores(difficulty, limit);
    }
    
    /**
     * Get the best score for a user in a specific difficulty
     * @param username The username
     * @param difficulty The difficulty level
     * @return The best score, or 0 if no scores exist
     */
    public int getBestScore(String username, String difficulty) {
        return scoreDAO.getBestScore(username, difficulty);
    }
    
    /**
     * Save a custom setting
     * @param key The setting key
     * @param value The setting value
     * @return true if successful, false otherwise
     */
    public boolean saveSetting(String key, String value) {
        String currentUser = getCurrentUser();
        return settingsDAO.saveSetting(currentUser, key, value);
    }
    
    /**
     * Get a custom setting
     * @param key The setting key
     * @param defaultValue The default value if setting doesn't exist
     * @return The setting value, or defaultValue if not found
     */
    public String getSetting(String key, String defaultValue) {
        String currentUser = getCurrentUser();
        return settingsDAO.getSetting(currentUser, key, defaultValue);
    }
    
    /**
     * Save a boolean setting
     * @param key The setting key
     * @param value The boolean value
     * @return true if successful, false otherwise
     */
    public boolean saveBooleanSetting(String key, boolean value) {
        String currentUser = getCurrentUser();
        return settingsDAO.saveBooleanSetting(currentUser, key, value);
    }
    
    /**
     * Get a boolean setting
     * @param key The setting key
     * @param defaultValue The default value if setting doesn't exist
     * @return The boolean value, or defaultValue if not found
     */
    public boolean getBooleanSetting(String key, boolean defaultValue) {
        String currentUser = getCurrentUser();
        return settingsDAO.getBooleanSetting(currentUser, key, defaultValue);
    }
    
    /**
     * Save an integer setting
     * @param key The setting key
     * @param value The integer value
     * @return true if successful, false otherwise
     */
    public boolean saveIntSetting(String key, int value) {
        String currentUser = getCurrentUser();
        return settingsDAO.saveIntSetting(currentUser, key, value);
    }
    
    /**
     * Get an integer setting
     * @param key The setting key
     * @param defaultValue The default value if setting doesn't exist
     * @return The integer value, or defaultValue if not found
     */
    public int getIntSetting(String key, int defaultValue) {
        String currentUser = getCurrentUser();
        return settingsDAO.getIntSetting(currentUser, key, defaultValue);
    }
}
