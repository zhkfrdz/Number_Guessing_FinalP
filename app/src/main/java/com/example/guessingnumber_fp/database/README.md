# SQLite Database Implementation for Number Guessing Game

This package provides SQLite database support for the Number Guessing Game while maintaining full compatibility with the existing SharedPreferences implementation.

## Overview

The implementation follows a dual-storage approach that allows you to:
1. Continue using SharedPreferences as before (no disruption to existing code)
2. Gradually transition to SQLite where it makes sense
3. Eventually move completely to SQLite if desired

## Components

### GameDataManager

The central class that provides a unified interface for data storage. It wraps both SharedPreferences and SQLite operations, allowing you to:

- Use the same familiar methods as SharedPreferences (getString, putInt, etc.)
- Access game-specific convenience methods (getCurrentUser, isSoundEnabled, etc.)
- Control which storage system is used through a simple mode setting

### GameDatabaseHelper

A simplified SQLite helper that creates and manages the database schema. The database includes:

- Users table: Stores user information
- Scores table: Stores game scores for each user and difficulty
- Statistics table: Stores game statistics (games played, won, lost, etc.)
- Settings table: Stores user settings (sound, music, etc.)

## How to Use

### In BaseActivity

```java
// In BaseActivity.onCreate()
protected GameDataManager dataManager;

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // Initialize data manager
    dataManager = GameDataManager.getInstance(this);
}
```

### In Your Activities

Instead of using SharedPreferences directly:

```java
// Old code with SharedPreferences
SharedPreferences prefs = getSharedPreferences("game_data", MODE_PRIVATE);
boolean musicOn = prefs.getBoolean("music_on", true);

// New code with GameDataManager
boolean musicOn = dataManager.isMusicEnabled();
```

### For Game Statistics

```java
// Get games played for current user in easy difficulty
int gamesPlayed = dataManager.getGameStat("games_played", "easy", dataManager.getCurrentUser(), 0);

// Increment games played
dataManager.incrementGameStat("games_played", "easy", dataManager.getCurrentUser(), 1);
```

### For Highscores

```java
// Get highscore
int highscore = dataManager.getHighscore("easy", dataManager.getCurrentUser());

// Set new highscore (only updates if score is higher than current)
dataManager.setHighscore("easy", dataManager.getCurrentUser(), newScore);
```

## Transitioning to SQLite

To begin using SQLite alongside SharedPreferences:

```java
// In your Application class or MainActivity
GameDataManager.setStorageMode(GameDataManager.MODE_DUAL);
```

This will write data to both storage systems but read from SharedPreferences first.

## Benefits of SQLite

1. **Better Performance**: More efficient for larger datasets
2. **Data Integrity**: Proper schema with constraints ensures valid data
3. **Advanced Queries**: Complex queries like sorting and filtering
4. **Scalability**: Easy to add new features and data types
5. **Maintainability**: Well-organized code that's easier to maintain

## Implementation Notes

This implementation preserves all existing functionality while adding SQLite support. Your app will continue to work exactly as before, with no disruption to the main logic.
