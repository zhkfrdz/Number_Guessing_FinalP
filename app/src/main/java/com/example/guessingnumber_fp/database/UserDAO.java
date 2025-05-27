package com.example.guessingnumber_fp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for User-related database operations
 */
public class UserDAO {
    private SQLiteDatabase database;
    private GameDatabaseHelper dbHelper;
    
    public UserDAO(Context context) {
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
     * Create a new user
     * @param username The username to create
     * @return true if user was created successfully, false if username already exists
     */
    public boolean createUser(String username) {
        if (username == null || username.isEmpty() || userExists(username)) {
            return false;
        }
        
        open();
        ContentValues values = new ContentValues();
        values.put(GameDatabaseHelper.COLUMN_USERNAME, username);
        
        long result = database.insert(GameDatabaseHelper.TABLE_USERS, null, values);
        close();
        
        return result != -1;
    }
    
    /**
     * Check if a user exists
     * @param username The username to check
     * @return true if user exists, false otherwise
     */
    public boolean userExists(String username) {
        if (username == null || username.isEmpty()) {
            return false;
        }
        
        open();
        String query = "SELECT * FROM " + GameDatabaseHelper.TABLE_USERS + 
                       " WHERE " + GameDatabaseHelper.COLUMN_USERNAME + " = ?";
        Cursor cursor = database.rawQuery(query, new String[]{username});
        
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        close();
        
        return exists;
    }
    
    /**
     * Get all usernames
     * @return List of all usernames
     */
    public List<String> getAllUsers() {
        List<String> userList = new ArrayList<>();
        
        open();
        String query = "SELECT " + GameDatabaseHelper.COLUMN_USERNAME + 
                       " FROM " + GameDatabaseHelper.TABLE_USERS + 
                       " ORDER BY " + GameDatabaseHelper.COLUMN_USERNAME;
        
        Cursor cursor = database.rawQuery(query, null);
        
        if (cursor.moveToFirst()) {
            do {
                userList.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        close();
        
        return userList;
    }
    
    /**
     * Delete a user and all associated data
     * @param username The username to delete
     * @return true if deletion was successful, false otherwise
     */
    public boolean deleteUser(String username) {
        if (username == null || username.isEmpty() || !userExists(username)) {
            return false;
        }
        
        open();
        int result = database.delete(
            GameDatabaseHelper.TABLE_USERS,
            GameDatabaseHelper.COLUMN_USERNAME + " = ?",
            new String[]{username}
        );
        close();
        
        return result > 0;
    }
    
    /**
     * Rename a user
     * @param oldUsername The current username
     * @param newUsername The new username
     * @return true if rename was successful, false otherwise
     */
    public boolean renameUser(String oldUsername, String newUsername) {
        if (oldUsername == null || oldUsername.isEmpty() || 
            newUsername == null || newUsername.isEmpty() ||
            !userExists(oldUsername) || userExists(newUsername)) {
            return false;
        }
        
        open();
        ContentValues values = new ContentValues();
        values.put(GameDatabaseHelper.COLUMN_USERNAME, newUsername);
        
        int result = database.update(
            GameDatabaseHelper.TABLE_USERS,
            values,
            GameDatabaseHelper.COLUMN_USERNAME + " = ?",
            new String[]{oldUsername}
        );
        close();
        
        return result > 0;
    }
}
