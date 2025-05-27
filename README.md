# PURRFECT GUESS

## Project Description
Purrfect Guess is a fun and interactive number guessing game for Android, featuring a charming cat theme. Two animated cats guide, react, and respond to your guesses. The orange cat offers helpful hints, while the other cat delivers witty and humorous roasts for incorrect guesses, making the experience both entertaining and engaging.

The game includes lively sound effects, a heart system (three lives per round), and a player stats section to track your progress. Enjoy replayable gameplay, playful animations, and a delightful user experience.

## Features
- **Cat-Themed UI:** Animated cats guide and react to your gameplay.
- **Number Guessing:** Guess a randomly generated number within a range based on difficulty.
- **Roasting & Hints:** Receive funny roasts for wrong guesses and helpful hints from the orange cat.
- **Heart System:** Three lives per round; lose a heart for each wrong guess.
- **Difficulty Levels:** Easy, Medium, and Hard, each with its own music and range.
- **Player Stats:** Track games played, won, lost, and hints used.
- **Sound Effects:** Lively sounds for actions and background music for each mode.
- **Modern UI:** Clean, responsive, and visually appealing interface.

## Tech Stack
- **Language:** Java 17
- **IDE:** Android Studio Meerkat 2024.1.1
- **Min SDK:** 24 (Android 7.0)
- **Target SDK:** 34 (Android 14.0)
- **Database:** SQLite (local, for user stats)
- **Testing:** JUnit, Logcat
- **Notifications:** NotificationManager (for future features)

## Architecture
- **UI Layer:** Android XML Layouts, custom views, and animations
- **App Logic:** Java, ViewModels, event handling, state management
- **Data Layer:** SQLite for persistent stats and user data
- **Session Management:** SharedPreferences for login state and preferences
- **Sound & Music:** Managed by a singleton MusicManager

## Requirements
- Android Studio Meerkat 2024.1.1+
- Java 17
- Min SDK 24, Target SDK 34
- SQLite (bundled)
- JUnit, Logcat (for testing)
- NotificationManager (for future notification features)

## How to Build/Run
1. Clone the repository.
2. Open in Android Studio.
3. Build the project (Gradle sync).
4. Run on an emulator or Android device (API 24+).

## Credits
- Cat illustrations and animations: [Your source or credit here]
- Sound effects: [Your source or credit here]

## License
[Specify your license here, e.g., MIT, Apache 2.0, etc.] 