package com.example.guessingnumber_fp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.HashMap;
import java.util.Map;

/**
 * Data Access Object for Settings-related database operations
 */
public class SettingsDAO {
    private SQLiteDatabase database;
    private GameDatabaseHelper dbHelper;
    
    // Common setting keys
    public static final String SETTING_SOUND_ENABLED = "sound_enabled";
    public static final String SETTING_MUSIC_ENABLED = "music_enabled";
    public static final String SETTING_CURRENT_USER = "current_user";
    
    public SettingsDAO(Context context) {
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
     * Save a setting for a user
     * @param username The username
     * @param key The setting key
     * @param value The setting value
     * @return true if setting was saved successfully, false otherwise
     */
    public boolean saveSetting(String username, String key, String value) {
        if (username == null || username.isEmpty() || 
            key == null || key.isEmpty()) {
            return false;
        }
        
        open();
        
        // Check if setting already exists
        String query = "SELECT * FROM " + GameDatabaseHelper.TABLE_SETTINGS + 
                       " WHERE " + GameDatabaseHelper.COLUMN_USERNAME + " = ? AND " +
                       GameDatabaseHelper.COLUMN_KEY + " = ?";
        
        Cursor cursor = database.rawQuery(query, new String[]{username, key});
        
        ContentValues values = new ContentValues();
        values.put(GameDatabaseHelper.COLUMN_USERNAME, username);
        values.put(GameDatabaseHelper.COLUMN_KEY, key);
        values.put(GameDatabaseHelper.COLUMN_VALUE, value);
        
        long result;
        
        if (cursor.moveToFirst()) {
            // Update existing setting
            result = database.update(
                GameDatabaseHelper.TABLE_SETTINGS,
                values,
                GameDatabaseHelper.COLUMN_USERNAME + " = ? AND " + 
                GameDatabaseHelper.COLUMN_KEY + " = ?",
                new String[]{username, key}
            );
        } else {
            // Insert new setting
            result = database.insert(GameDatabaseHelper.TABLE_SETTINGS, null, values);
        }
        
        cursor.close();
        close();
        
        return result != -1;
    }
    
    /**
     * Save a boolean setting for a user
     * @param username The username
     * @param key The setting key
     * @param value The boolean value
     * @return true if setting was saved successfully, false otherwise
     */
    public boolean saveBooleanSetting(String username, String key, boolean value) {
        return saveSetting(username, key, value ? "true" : "false");
    }
    
    /**
     * Save an integer setting for a user
     * @param username The username
     * @param key The setting key
     * @param value The integer value
     * @return true if setting was saved successfully, false otherwise
     */
    public boolean saveIntSetting(String username, String key, int value) {
        return saveSetting(username, key, String.valueOf(value));
    }
    
    /**
     * Get a setting for a user
     * @param username The username
     * @param key The setting key
     * @param defaultValue The default value if setting doesn't exist
     * @return The setting value, or defaultValue if not found
     */
    public String getSetting(String username, String key, String defaultValue) {
        if (username == null || username.isEmpty() || 
            key == null || key.isEmpty()) {
            return defaultValue;
        }
        
        open();
        String query = "SELECT " + GameDatabaseHelper.COLUMN_VALUE + 
                       " FROM " + GameDatabaseHelper.TABLE_SETTINGS + 
                       " WHERE " + GameDatabaseHelper.COLUMN_USERNAME + " = ? AND " +
                       GameDatabaseHelper.COLUMN_KEY + " = ?";
        
        Cursor cursor = database.rawQuery(query, new String[]{username, key});
        
        String value = defaultValue;
        if (cursor.moveToFirst()) {
            value = cursor.getString(0);
        }
        
        cursor.close();
        close();
        
        return value;
    }
    
    /**
     * Get a boolean setting for a user
     * @param username The username
     * @param key The setting key
     * @param defaultValue The default value if setting doesn't exist
     * @return The boolean value, or defaultValue if not found
     */
    public boolean getBooleanSetting(String username, String key, boolean defaultValue) {
        String value = getSetting(username, key, defaultValue ? "true" : "false");
        return "true".equals(value);
    }
    
    /**
     * Get an integer setting for a user
     * @param username The username
     * @param key The setting key
     * @param defaultValue The default value if setting doesn't exist
     * @return The integer value, or defaultValue if not found
     */
    public int getIntSetting(String username, String key, int defaultValue) {
        String value = getSetting(username, key, String.valueOf(defaultValue));
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    /**
     * Get all settings for a user
     * @param username The username
     * @return Map of all settings (key-value pairs)
     */
    public Map<String, String> getAllSettings(String username) {
        Map<String, String> settings = new HashMap<>();
        
        if (username == null || username.isEmpty()) {
            return settings;
        }
        
        open();
        String query = "SELECT " + GameDatabaseHelper.COLUMN_KEY + ", " +
                       GameDatabaseHelper.COLUMN_VALUE + 
                       " FROM " + GameDatabaseHelper.TABLE_SETTINGS + 
                       " WHERE " + GameDatabaseHelper.COLUMN_USERNAME + " = ?";
        
        Cursor cursor = database.rawQuery(query, new String[]{username});
        
        if (cursor.moveToFirst()) {
            do {
                settings.put(cursor.getString(0), cursor.getString(1));
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        close();
        
        return settings;
    }
    
    /**
     * Delete all settings for a specific user
     * @param username The username
     * @return true if deletion was successful, false otherwise
     */
    public boolean deleteUserSettings(String username) {
        if (username == null || username.isEmpty()) {
            return false;
        }
        
        open();
        int result = database.delete(
            GameDatabaseHelper.TABLE_SETTINGS,
            GameDatabaseHelper.COLUMN_USERNAME + " = ?",
            new String[]{username}
        );
        close();
        
        return result > 0;
    }
    
    /**
     * Delete a specific setting for a user
     * @param username The username
     * @param key The setting key
     * @return true if deletion was successful, false otherwise
     */
    public boolean deleteSetting(String username, String key) {
        if (username == null || username.isEmpty() || 
            key == null || key.isEmpty()) {
            return false;
        }
        
        open();
        int result = database.delete(
            GameDatabaseHelper.TABLE_SETTINGS,
            GameDatabaseHelper.COLUMN_USERNAME + " = ? AND " +
            GameDatabaseHelper.COLUMN_KEY + " = ?",
            new String[]{username, key}
        );
        close();
        
        return result > 0;
    }
}
