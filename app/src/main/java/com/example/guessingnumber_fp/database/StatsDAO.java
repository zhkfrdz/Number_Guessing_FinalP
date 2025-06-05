package com.example.guessingnumber_fp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object for Statistics-related database operations
 */
public class StatsDAO {
    private SQLiteDatabase database;
    private GameDatabaseHelper dbHelper;
    
    public StatsDAO(Context context) {
        dbHelper = new GameDatabaseHelper(context);
    }
    
    /**
     * Open database connection
     */
    public void open() {
        database = dbHelper.getWritableDatabase();
    }
    
    /**
     * Close database connection
     */
    public void close() {
        if (database != null && database.isOpen()) {
            database.close();
        }
    }
    
    /**
     * Initialize statistics for a user and difficulty if they don't exist
     * @param username The username
     * @param difficulty The difficulty level
     */
    private void initializeStats(String username, String difficulty) {
        String query = "SELECT * FROM " + GameDatabaseHelper.TABLE_STATS + 
                       " WHERE " + GameDatabaseHelper.COLUMN_USERNAME + " = ? AND " +
                       GameDatabaseHelper.COLUMN_DIFFICULTY + " = ?";
        
        Cursor cursor = database.rawQuery(query, new String[]{username, difficulty});
        
        if (!cursor.moveToFirst()) {
            // Create games_played stat
            ContentValues valuesPlayed = new ContentValues();
            valuesPlayed.put(GameDatabaseHelper.COLUMN_USERNAME, username);
            valuesPlayed.put(GameDatabaseHelper.COLUMN_DIFFICULTY, difficulty);
            valuesPlayed.put(GameDatabaseHelper.COLUMN_KEY, "games_played");
            valuesPlayed.put(GameDatabaseHelper.COLUMN_VALUE, 0);
            database.insert(GameDatabaseHelper.TABLE_STATS, null, valuesPlayed);
            
            // Create games_won stat
            ContentValues valuesWon = new ContentValues();
            valuesWon.put(GameDatabaseHelper.COLUMN_USERNAME, username);
            valuesWon.put(GameDatabaseHelper.COLUMN_DIFFICULTY, difficulty);
            valuesWon.put(GameDatabaseHelper.COLUMN_KEY, "games_won");
            valuesWon.put(GameDatabaseHelper.COLUMN_VALUE, 0);
            database.insert(GameDatabaseHelper.TABLE_STATS, null, valuesWon);
            
            // Create games_lost stat
            ContentValues valuesLost = new ContentValues();
            valuesLost.put(GameDatabaseHelper.COLUMN_USERNAME, username);
            valuesLost.put(GameDatabaseHelper.COLUMN_DIFFICULTY, difficulty);
            valuesLost.put(GameDatabaseHelper.COLUMN_KEY, "games_lost");
            valuesLost.put(GameDatabaseHelper.COLUMN_VALUE, 0);
            database.insert(GameDatabaseHelper.TABLE_STATS, null, valuesLost);
            
            // Create hints_used stat
            ContentValues valuesHints = new ContentValues();
            valuesHints.put(GameDatabaseHelper.COLUMN_USERNAME, username);
            valuesHints.put(GameDatabaseHelper.COLUMN_DIFFICULTY, difficulty);
            valuesHints.put(GameDatabaseHelper.COLUMN_KEY, "hints_used");
            valuesHints.put(GameDatabaseHelper.COLUMN_VALUE, 0);
            database.insert(GameDatabaseHelper.TABLE_STATS, null, valuesHints);
            
            // Create best_score stat
            ContentValues valuesBest = new ContentValues();
            valuesBest.put(GameDatabaseHelper.COLUMN_USERNAME, username);
            valuesBest.put(GameDatabaseHelper.COLUMN_DIFFICULTY, difficulty);
            valuesBest.put(GameDatabaseHelper.COLUMN_KEY, "best_score");
            valuesBest.put(GameDatabaseHelper.COLUMN_VALUE, 0);
            database.insert(GameDatabaseHelper.TABLE_STATS, null, valuesBest);
        }
        
        cursor.close();
    }
    
    /**
     * Increment a specific stat for a user and difficulty
     * @param username The username
     * @param difficulty The difficulty level
     * @param columnName The column to increment
     * @param value The amount to increment by
     * @return true if update was successful, false otherwise
     */
    private boolean incrementStat(String username, String difficulty, String statKey, int value) {
        if (username == null || username.isEmpty() || 
            difficulty == null || difficulty.isEmpty()) {
            return false;
        }
        
        open();
        
        // Ensure stats record exists
        initializeStats(username, difficulty);
        
        // Update the stat
        String query = "UPDATE " + GameDatabaseHelper.TABLE_STATS + 
                       " SET " + GameDatabaseHelper.COLUMN_VALUE + " = " + GameDatabaseHelper.COLUMN_VALUE + " + ? " +
                       " WHERE " + GameDatabaseHelper.COLUMN_USERNAME + " = ? AND " +
                       GameDatabaseHelper.COLUMN_DIFFICULTY + " = ? AND " +
                       GameDatabaseHelper.COLUMN_KEY + " = ?";
        
        database.execSQL(query, new Object[]{value, username, difficulty, statKey});
        close();
        
        return true;
    }
    
    /**
     * Increment games played for a user and difficulty
     * @param username The username
     * @param difficulty The difficulty level
     * @return true if update was successful, false otherwise
     */
    public boolean incrementGamesPlayed(String username, String difficulty) {
        return incrementStat(username, difficulty, "games_played", 1);
    }
    
    /**
     * Increment games won for a user and difficulty
     * @param username The username
     * @param difficulty The difficulty level
     * @return true if update was successful, false otherwise
     */
    public boolean incrementGamesWon(String username, String difficulty) {
        return incrementStat(username, difficulty, "games_won", 1);
    }
    
    /**
     * Increment games lost for a user and difficulty
     * @param username The username
     * @param difficulty The difficulty level
     * @return true if update was successful, false otherwise
     */
    public boolean incrementGamesLost(String username, String difficulty) {
        return incrementStat(username, difficulty, "games_lost", 1);
    }
    
    /**
     * Increment hints used for a user and difficulty
     * @param username The username
     * @param difficulty The difficulty level
     * @param count Number of hints to add
     * @return true if update was successful, false otherwise
     */
    public boolean incrementHintsUsed(String username, String difficulty, int count) {
        return incrementStat(username, difficulty, "hints_used", count);
    }
    
    /**
     * Set a specific stat value for a user and difficulty
     * @param username The username
     * @param difficulty The difficulty level
     * @param statKey The stat key to set
     * @param value The value to set
     * @return true if update was successful, false otherwise
     */
    public boolean setStat(String username, String difficulty, String statKey, int value) {
        if (username == null || username.isEmpty() || 
            difficulty == null || difficulty.isEmpty()) {
            return false;
        }
        
        open();
        
        // Ensure stats record exists
        initializeStats(username, difficulty);
        
        // Update the stat
        ContentValues values = new ContentValues();
        values.put(GameDatabaseHelper.COLUMN_VALUE, value);
        
        int result = database.update(
            GameDatabaseHelper.TABLE_STATS,
            values,
            GameDatabaseHelper.COLUMN_USERNAME + " = ? AND " +
            GameDatabaseHelper.COLUMN_DIFFICULTY + " = ? AND " +
            GameDatabaseHelper.COLUMN_KEY + " = ?",
            new String[]{username, difficulty, statKey}
        );
        
        close();
        return result > 0;
    }
    
    /**
     * Get all statistics for a user and difficulty
     * @param username The username
     * @param difficulty The difficulty level
     * @return Map containing all statistics
     */
    public Map<String, Integer> getStats(String username, String difficulty) {
        Map<String, Integer> stats = new HashMap<>();
        
        if (username == null || username.isEmpty() || 
            difficulty == null || difficulty.isEmpty()) {
            return stats;
        }
        
        open();
        
        // Ensure stats record exists
        initializeStats(username, difficulty);
        
        String query = "SELECT " + GameDatabaseHelper.COLUMN_KEY + ", " + GameDatabaseHelper.COLUMN_VALUE +
                       " FROM " + GameDatabaseHelper.TABLE_STATS + 
                       " WHERE " + GameDatabaseHelper.COLUMN_USERNAME + " = ? AND " +
                       GameDatabaseHelper.COLUMN_DIFFICULTY + " = ?";
        
        Cursor cursor = database.rawQuery(query, new String[]{username, difficulty});
        
        if (cursor.moveToFirst()) {
            do {
                String key = cursor.getString(cursor.getColumnIndex(GameDatabaseHelper.COLUMN_KEY));
                int value = cursor.getInt(cursor.getColumnIndex(GameDatabaseHelper.COLUMN_VALUE));
                stats.put(key, value);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        close();
        
        return stats;
    }
    
    /**
     * Delete all statistics for a specific user
     * @param username The username
     * @return true if deletion was successful, false otherwise
     */
    public boolean deleteUserStats(String username) {
        if (username == null || username.isEmpty()) {
            return false;
        }
        
        open();
        int result = database.delete(
            GameDatabaseHelper.TABLE_STATS,
            GameDatabaseHelper.COLUMN_USERNAME + " = ?",
            new String[]{username}
        );
        close();
        
        return result > 0;
    }
    
    /**
     * Get all unique usernames from the stats table
     * @return List of usernames
     */
    public List<String> getAllUsers() {
        List<String> users = new ArrayList<>();
        
        String query = "SELECT DISTINCT " + GameDatabaseHelper.COLUMN_USERNAME + 
                      " FROM " + GameDatabaseHelper.TABLE_STATS;
        
        Cursor cursor = database.rawQuery(query, null);
        
        if (cursor.moveToFirst()) {
            do {
                String username = cursor.getString(0);
                if (username != null && !username.isEmpty()) {
                    users.add(username);
                }
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        return users;
    }
}
