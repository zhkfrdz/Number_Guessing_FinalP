package com.example.guessingnumber_fp.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * GameDataManager provides a unified interface for data storage operations
 * It supports both SharedPreferences and SQLite, allowing for a gradual transition
 */
public class GameDataManager {
    private static final String TAG = "GameDataManager";
    private static final String PREFS_NAME = "game_data";
    
    // Storage mode flags
    public static final int MODE_SHARED_PREFS = 0;
    public static final int MODE_SQLITE = 1;
    public static final int MODE_DUAL = 2; // Read from both, write to both
    
    // Current storage mode - start with SharedPreferences by default
    private static int currentMode = MODE_SHARED_PREFS;
    
    private static GameDataManager instance;
    private Context context;
    private SharedPreferences prefs;
    private GameDatabaseHelper dbHelper;
    private SQLiteDatabase database;
    
    // Singleton pattern
    public static synchronized GameDataManager getInstance(Context context) {
        if (instance == null) {
            instance = new GameDataManager(context.getApplicationContext());
        }
        return instance;
    }
    
    private GameDataManager(Context context) {
        this.context = context;
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        
        // Initialize SQLite database if in SQLite or dual mode
        if (currentMode == MODE_SQLITE || currentMode == MODE_DUAL) {
            this.dbHelper = new GameDatabaseHelper(context);
        }
    }
    
    /**
     * Set the storage mode to use
     * @param mode The storage mode (MODE_SHARED_PREFS, MODE_SQLITE, or MODE_DUAL)
     */
    public static void setStorageMode(int mode) {
        if (mode >= MODE_SHARED_PREFS && mode <= MODE_DUAL) {
            currentMode = mode;
            Log.i(TAG, "Storage mode set to: " + getModeName(mode));
        }
    }
    
    /**
     * Get the current storage mode
     * @return The current storage mode
     */
    public static int getStorageMode() {
        return currentMode;
    }
    
    /**
     * Get the name of a storage mode
     * @param mode The storage mode
     * @return The name of the storage mode
     */
    private static String getModeName(int mode) {
        switch (mode) {
            case MODE_SHARED_PREFS: return "SharedPreferences";
            case MODE_SQLITE: return "SQLite";
            case MODE_DUAL: return "Dual (SharedPreferences + SQLite)";
            default: return "Unknown";
        }
    }
    
    /**
     * Initialize SQLite database if not already initialized
     */
    private void initDatabaseIfNeeded() {
        if (dbHelper == null) {
            dbHelper = new GameDatabaseHelper(context);
        }
    }
    
    /**
     * Open database connection
     */
    private void openDatabase() {
        if (database == null || !database.isOpen()) {
            database = dbHelper.getWritableDatabase();
        }
    }
    
    /**
     * Close database connection
     */
    private void closeDatabase() {
        if (database != null && database.isOpen()) {
            database.close();
        }
    }
    
    // ====== SharedPreferences wrapper methods ======
    
    /**
     * Get a string value
     * @param key The key
     * @param defaultValue The default value
     * @return The string value
     */
    public String getString(String key, String defaultValue) {
        return prefs.getString(key, defaultValue);
    }
    
    /**
     * Get an integer value
     * @param key The key
     * @param defaultValue The default value
     * @return The integer value
     */
    public int getInt(String key, int defaultValue) {
        return prefs.getInt(key, defaultValue);
    }
    
    /**
     * Get a boolean value
     * @param key The key
     * @param defaultValue The default value
     * @return The boolean value
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        return prefs.getBoolean(key, defaultValue);
    }
    
    /**
     * Get a long value
     * @param key The key
     * @param defaultValue The default value
     * @return The long value
     */
    public long getLong(String key, long defaultValue) {
        return prefs.getLong(key, defaultValue);
    }
    
    /**
     * Get a float value
     * @param key The key
     * @param defaultValue The default value
     * @return The float value
     */
    public float getFloat(String key, float defaultValue) {
        return prefs.getFloat(key, defaultValue);
    }
    
    /**
     * Put a string value
     * @param key The key
     * @param value The value
     */
    public void putString(String key, String value) {
        prefs.edit().putString(key, value).apply();
    }
    
    /**
     * Put an integer value
     * @param key The key
     * @param value The value
     */
    public void putInt(String key, int value) {
        prefs.edit().putInt(key, value).apply();
    }
    
    /**
     * Put a boolean value
     * @param key The key
     * @param value The value
     */
    public void putBoolean(String key, boolean value) {
        prefs.edit().putBoolean(key, value).apply();
    }
    
    /**
     * Put a long value
     * @param key The key
     * @param value The value
     */
    public void putLong(String key, long value) {
        prefs.edit().putLong(key, value).apply();
    }
    
    /**
     * Put a float value
     * @param key The key
     * @param value The value
     */
    public void putFloat(String key, float value) {
        prefs.edit().putFloat(key, value).apply();
    }
    
    /**
     * Check if a key exists
     * @param key The key
     * @return true if the key exists, false otherwise
     */
    public boolean contains(String key) {
        return prefs.contains(key);
    }
    
    /**
     * Remove a value
     * @param key The key
     */
    public void remove(String key) {
        prefs.edit().remove(key).apply();
    }
    
    /**
     * Clear all values
     */
    public void clear() {
        prefs.edit().clear().apply();
    }
    
    // ====== Game-specific convenience methods ======
    
    /**
     * Get the current user
     * @return The current user, defaults to "guest"
     */
    public String getCurrentUser() {
        return getString("current_user", "guest");
    }
    
    /**
     * Set the current user
     * @param username The username
     */
    public void setCurrentUser(String username) {
        putString("current_user", username);
    }
    
    /**
     * Check if sound is enabled
     * @return true if sound is enabled, false otherwise
     */
    public boolean isSoundEnabled() {
        return getBoolean("sound_on", true);
    }
    
    /**
     * Set sound enabled/disabled
     * @param enabled true to enable sound, false to disable
     */
    public void setSoundEnabled(boolean enabled) {
        putBoolean("sound_on", enabled);
    }
    
    /**
     * Check if music is enabled
     * @return true if music is enabled, false otherwise
     */
    public boolean isMusicEnabled() {
        return getBoolean("music_on", true);
    }
    
    /**
     * Set music enabled/disabled
     * @param enabled true to enable music, false to disable
     */
    public void setMusicEnabled(boolean enabled) {
        putBoolean("music_on", enabled);
    }
    
    /**
     * Get a game statistic
     * @param statName The statistic name
     * @param difficulty The difficulty level
     * @param username The username
     * @param defaultValue The default value
     * @return The statistic value
     */
    public int getGameStat(String statName, String difficulty, String username, int defaultValue) {
        String key = statName + "_" + difficulty + "_" + username;
        return getInt(key, defaultValue);
    }
    
    /**
     * Set a game statistic
     * @param statName The statistic name
     * @param difficulty The difficulty level
     * @param username The username
     * @param value The value
     */
    public void setGameStat(String statName, String difficulty, String username, int value) {
        String key = statName + "_" + difficulty + "_" + username;
        putInt(key, value);
    }
    
    /**
     * Increment a game statistic
     * @param statName The statistic name
     * @param difficulty The difficulty level
     * @param username The username
     * @param increment The amount to increment by
     * @return The new value
     */
    public int incrementGameStat(String statName, String difficulty, String username, int increment) {
        String key = statName + "_" + difficulty + "_" + username;
        int currentValue = getInt(key, 0);
        int newValue = currentValue + increment;
        putInt(key, newValue);
        return newValue;
    }
    
    /**
     * Get the highscore for a user and difficulty
     * @param difficulty The difficulty level
     * @param username The username
     * @return The highscore, or 0 if none exists
     */
    public int getHighscore(String difficulty, String username) {
        return getGameStat("highscore", difficulty, username, 0);
    }
    
    /**
     * Set the highscore for a user and difficulty
     * @param difficulty The difficulty level
     * @param username The username
     * @param score The score
     */
    public void setHighscore(String difficulty, String username, int score) {
        int currentHighscore = getHighscore(difficulty, username);
        if (score > currentHighscore) {
            setGameStat("highscore", difficulty, username, score);
        }
    }
    
    /**
     * Get all statistics for a user and difficulty level
     * @param username The username
     * @param difficulty The difficulty level
     * @return A map of statistic names to values
     */
    public Map<String, Integer> getStatsForUser(String username, String difficulty) {
        Map<String, Integer> stats = new HashMap<>();
        
        // If using SQLite, get stats from database
        if (currentMode == MODE_SQLITE || currentMode == MODE_DUAL) {
            try {
                initDatabaseIfNeeded();
                openDatabase();
                
                // Use StatsDAO to get stats
                StatsDAO statsDAO = new StatsDAO(context);
                stats = statsDAO.getStats(username, difficulty);
                
                closeDatabase();
                
                // If we got stats from SQLite and we're not in dual mode, return them
                if (!stats.isEmpty() && currentMode != MODE_DUAL) {
                    return stats;
                }
            } catch (Exception e) {
                Log.e(TAG, "Error getting stats from SQLite: " + e.getMessage());
                // Fall back to SharedPreferences if there was an error
            }
        }
        
        // If using SharedPreferences or if SQLite failed or returned empty results
        if (currentMode == MODE_SHARED_PREFS || currentMode == MODE_DUAL || stats.isEmpty()) {
            // Get stats from SharedPreferences
            stats.put("games_played", getInt("games_played_" + difficulty + "_" + username, 0));
            stats.put("games_won", getInt("games_won_" + difficulty + "_" + username, 0));
            stats.put("games_lost", getInt("games_lost_" + difficulty + "_" + username, 0));
            stats.put("hints_used", getInt("hints_used_" + difficulty + "_" + username, 0));
            stats.put("best_score", getInt("highscore_" + difficulty + "_" + username, 0));
        }
        
        return stats;
    }
    
    /**
     * Get the top scores for a difficulty level
     * @param difficulty The difficulty level
     * @param limit The maximum number of scores to return
     * @return A list of maps containing username and score
     */
    public List<Map<String, Object>> getTopScores(String difficulty, int limit) {
        List<Map<String, Object>> results = new ArrayList<>();
        
        // If using SQLite, get scores from database
        if (currentMode == MODE_SQLITE || currentMode == MODE_DUAL) {
            try {
                initDatabaseIfNeeded();
                openDatabase();
                
                // Use ScoreDAO to get top scores
                ScoreDAO scoreDAO = new ScoreDAO(context);
                results = scoreDAO.getTopScores(difficulty, limit);
                
                closeDatabase();
            } catch (Exception e) {
                Log.e(TAG, "Error getting top scores from SQLite: " + e.getMessage());
                // Fall back to SharedPreferences if there was an error
            }
        }
        
        // If using SharedPreferences or if SQLite failed, get scores from SharedPreferences
        if ((currentMode == MODE_SHARED_PREFS || currentMode == MODE_DUAL) && results.isEmpty()) {
            Map<String, Integer> bestScores = new HashMap<>();
            Map<String, ?> allEntries = prefs.getAll();
            
            // First pass: collect all scores
            for (String key : allEntries.keySet()) {
                if (key.startsWith("highscore_" + difficulty + "_") && !key.equals("highscore_" + difficulty + "_user")) {
                    String username = key.substring(("highscore_" + difficulty + "_").length());
                    int score = 0;
                    try {
                        score = Integer.parseInt(allEntries.get(key).toString());
                    } catch (Exception ignored) {}
                    
                    // Keep the highest score for each username
                    if (!bestScores.containsKey(username) || score > bestScores.get(username)) {
                        bestScores.put(username, score);
                    }
                }
            }
            
            // Convert to list for sorting
            List<Map.Entry<String, Integer>> entries = new ArrayList<>(bestScores.entrySet());
            Collections.sort(entries, (a, b) -> Integer.compare(b.getValue(), a.getValue()));
            
            // Convert to result format
            int count = 0;
            for (Map.Entry<String, Integer> entry : entries) {
                if (count >= limit) break;
                
                Map<String, Object> scoreData = new HashMap<>();
                scoreData.put("username", entry.getKey());
                scoreData.put("score", entry.getValue());
                results.add(scoreData);
                
                count++;
            }
        }
        
        return results;
    }
}
