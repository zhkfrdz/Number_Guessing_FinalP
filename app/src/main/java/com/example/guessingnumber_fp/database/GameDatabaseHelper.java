package com.example.guessingnumber_fp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * SQLite Database Helper for the Number Guessing Game
 * Handles database creation, schema management, and version upgrades
 * This is a simplified version that works alongside SharedPreferences
 */
public class GameDatabaseHelper extends SQLiteOpenHelper {
    // Database Information
    private static final String DATABASE_NAME = "number_guessing_game.db";
    private static final int DATABASE_VERSION = 1;
    
    // Table Names
    public static final String TABLE_USERS = "users";
    public static final String TABLE_SCORES = "scores";
    public static final String TABLE_STATS = "statistics";
    public static final String TABLE_SETTINGS = "settings";
    
    // Common Column Names
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_DIFFICULTY = "difficulty";
    public static final String COLUMN_KEY = "key";
    public static final String COLUMN_VALUE = "value";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    
    // Table Create Statements
    // Users table - simple table to store usernames
    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_USERNAME + " TEXT UNIQUE NOT NULL"
            + ")";
    
    // Scores table - stores scores for each user and difficulty
    private static final String CREATE_TABLE_SCORES = "CREATE TABLE " + TABLE_SCORES + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_USERNAME + " TEXT NOT NULL,"
            + COLUMN_DIFFICULTY + " TEXT NOT NULL,"
            + COLUMN_VALUE + " INTEGER NOT NULL,"
            + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP"
            + ")";
    
    // Statistics table - stores game statistics
    private static final String CREATE_TABLE_STATS = "CREATE TABLE " + TABLE_STATS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_USERNAME + " TEXT NOT NULL,"
            + COLUMN_DIFFICULTY + " TEXT NOT NULL,"
            + COLUMN_KEY + " TEXT NOT NULL,"
            + COLUMN_VALUE + " INTEGER NOT NULL,"
            + "UNIQUE(" + COLUMN_USERNAME + "," + COLUMN_DIFFICULTY + "," + COLUMN_KEY + ")"
            + ")";
    
    // Settings table - stores user settings
    private static final String CREATE_TABLE_SETTINGS = "CREATE TABLE " + TABLE_SETTINGS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_USERNAME + " TEXT NOT NULL,"
            + COLUMN_KEY + " TEXT NOT NULL,"
            + COLUMN_VALUE + " TEXT,"
            + "UNIQUE(" + COLUMN_USERNAME + "," + COLUMN_KEY + ")"
            + ")";
    
    public GameDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Creating required tables
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_SCORES);
        db.execSQL(CREATE_TABLE_STATS);
        db.execSQL(CREATE_TABLE_SETTINGS);
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // For future database schema upgrades
        // This will be implemented in future versions when schema changes
        
        // For now, simply drop and recreate tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SETTINGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STATS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCORES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        
        // Create new tables
        onCreate(db);
    }
}
