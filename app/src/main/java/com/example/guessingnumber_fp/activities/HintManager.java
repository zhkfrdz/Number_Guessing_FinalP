package com.example.guessingnumber_fp.activities;

import java.util.List;

/**
 * @deprecated This class has been replaced by HintManagerNew. 
 * This version is kept for backward compatibility and delegates all calls to HintManagerNew.
 */
@Deprecated
public class HintManager {
    
    /**
     * Generates a hint based on the target number and difficulty.
     * @param targetNumber The number to generate a hint for
     * @param difficultyMax The maximum number for the current difficulty
     * @param difficulty The difficulty level name
     * @return A hint string
     */
    public static String generateHint(int targetNumber, int difficultyMax, String difficulty) {
        return HintManagerNew.generateHint(targetNumber, difficultyMax, difficulty);
    }
}
