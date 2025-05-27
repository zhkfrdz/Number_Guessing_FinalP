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
 * Data Access Object for Score-related database operations
 */
public class ScoreDAO {
    private SQLiteDatabase database;
    private GameDatabaseHelper dbHelper;
    
    public ScoreDAO(Context context) {
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
     * Add a new score for a user
     * @param username The username
     * @param difficulty The difficulty level (easy, medium, hard, impossible)
     * @param score The score value
     * @return true if score was added successfully, false otherwise
     */
    public boolean addScore(String username, String difficulty, int score) {
        if (username == null || username.isEmpty() || 
            difficulty == null || difficulty.isEmpty()) {
            return false;
        }
        
        open();
        ContentValues values = new ContentValues();
        values.put(GameDatabaseHelper.COLUMN_USERNAME, username);
        values.put(GameDatabaseHelper.COLUMN_DIFFICULTY, difficulty);
        values.put(GameDatabaseHelper.COLUMN_VALUE, score);
        
        long result = database.insert(GameDatabaseHelper.TABLE_SCORES, null, values);
        
        // Update best score in stats table if needed
        updateBestScore(username, difficulty, score);
        
        close();
        return result != -1;
    }
    
    /**
     * Update the best score for a user in a specific difficulty if the new score is higher
     * @param username The username
     * @param difficulty The difficulty level
     * @param score The new score
     */
    private void updateBestScore(String username, String difficulty, int score) {
        String query = "SELECT " + GameDatabaseHelper.COLUMN_VALUE + 
                       " FROM " + GameDatabaseHelper.TABLE_STATS + 
                       " WHERE " + GameDatabaseHelper.COLUMN_USERNAME + " = ? AND " +
                       GameDatabaseHelper.COLUMN_DIFFICULTY + " = ? AND " +
                       GameDatabaseHelper.COLUMN_KEY + " = 'best_score'";
        
        Cursor cursor = database.rawQuery(query, new String[]{username, difficulty});
        
        if (cursor.moveToFirst()) {
            int currentBest = cursor.getInt(0);
            if (score > currentBest) {
                ContentValues values = new ContentValues();
                values.put(GameDatabaseHelper.COLUMN_VALUE, score);
                
                database.update(
                    GameDatabaseHelper.TABLE_STATS,
                    values,
                    GameDatabaseHelper.COLUMN_USERNAME + " = ? AND " + 
                    GameDatabaseHelper.COLUMN_DIFFICULTY + " = ?",
                    new String[]{username, difficulty}
                );
            }
        }
        
        cursor.close();
    }
    
    /**
     * Get top scores for a specific difficulty
     * @param difficulty The difficulty level
     * @param limit Maximum number of scores to return
     * @return List of maps containing username and score
     */
    public List<Map<String, Object>> getTopScores(String difficulty, int limit) {
        List<Map<String, Object>> scoresList = new ArrayList<>();
        
        open();
        String query = "SELECT " + GameDatabaseHelper.COLUMN_USERNAME + ", " + 
                       GameDatabaseHelper.COLUMN_VALUE + ", " + 
                       GameDatabaseHelper.COLUMN_TIMESTAMP + 
                       " FROM " + GameDatabaseHelper.TABLE_SCORES + 
                       " WHERE " + GameDatabaseHelper.COLUMN_DIFFICULTY + " = ? " +
                       " ORDER BY " + GameDatabaseHelper.COLUMN_VALUE + " DESC " +
                       " LIMIT ?";
        
        Cursor cursor = database.rawQuery(query, new String[]{difficulty, String.valueOf(limit)});
        
        if (cursor.moveToFirst()) {
            do {
                Map<String, Object> scoreMap = new HashMap<>();
                scoreMap.put("username", cursor.getString(0));
                scoreMap.put("score", cursor.getInt(1));
                scoreMap.put("timestamp", cursor.getString(2));
                scoresList.add(scoreMap);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        close();
        
        return scoresList;
    }
    
    /**
     * Get the best score for a user in a specific difficulty
     * @param username The username
     * @param difficulty The difficulty level
     * @return The best score, or 0 if no scores exist
     */
    public int getBestScore(String username, String difficulty) {
        if (username == null || username.isEmpty() || 
            difficulty == null || difficulty.isEmpty()) {
            return 0;
        }
        
        open();
        String query = "SELECT MAX(" + GameDatabaseHelper.COLUMN_VALUE + ") " +
                       " FROM " + GameDatabaseHelper.TABLE_SCORES + 
                       " WHERE " + GameDatabaseHelper.COLUMN_USERNAME + " = ? AND " +
                       GameDatabaseHelper.COLUMN_DIFFICULTY + " = ?";
        
        Cursor cursor = database.rawQuery(query, new String[]{username, difficulty});
        
        int bestScore = 0;
        if (cursor.moveToFirst()) {
            bestScore = cursor.getInt(0);
        }
        
        cursor.close();
        close();
        
        return bestScore;
    }
    
    /**
     * Delete all scores for a specific user
     * @param username The username
     * @return true if deletion was successful, false otherwise
     */
    public boolean deleteUserScores(String username) {
        if (username == null || username.isEmpty()) {
            return false;
        }
        
        open();
        int result = database.delete(
            GameDatabaseHelper.TABLE_SCORES,
            GameDatabaseHelper.COLUMN_USERNAME + " = ?",
            new String[]{username}
        );
        close();
        
        return result > 0;
    }
}
