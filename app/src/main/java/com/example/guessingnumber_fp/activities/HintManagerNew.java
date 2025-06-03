package com.example.guessingnumber_fp.activities;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Class to manage hint generation for the Number Guessing Game
 * This separates hint logic from the PlayActivity for better organization
 */
public class HintManagerNew {
    private static final Random random = new Random();
    
    // Track recently used hint types to avoid repetition
    private static final List<Integer> recentlyUsedHints = new ArrayList<>();
    private static final int MAX_RECENT_HINTS = 3; // Don't repeat the last 3 hint types
    
    // Difficulty level ranges
    public static final int EASY_MAX = 10;
    public static final int MEDIUM_MAX = 30;
    public static final int HARD_MAX = 50;
    public static final int IMPOSSIBLE_MAX = 100;
    
    /**
     * Generate a hint based on the target number and difficulty
     * 
     * @param targetNumber The number to generate a hint for
     * @param difficultyMax The maximum range for the current difficulty
     * @param difficulty The difficulty level name
     * @return A user-friendly hint message
     */
    public static String generateHint(int targetNumber, int difficultyMax, String difficulty) {
        // Get valid hint types for this target number and difficulty
        List<Integer> validHintTypes = getValidHintTypes(difficultyMax, targetNumber);
        
        // Remove recently used hint types to avoid repetition
        List<Integer> availableHintTypes = new ArrayList<>(validHintTypes);
        availableHintTypes.removeAll(recentlyUsedHints);
        
        // If we've filtered out too many hint types, use all valid types
        // This ensures we always have at least one hint available
        if (availableHintTypes.size() < 2) {
            availableHintTypes = new ArrayList<>(validHintTypes);
        }
        
        // Select a random hint type from the available ones
        int randomIndex = random.nextInt(availableHintTypes.size());
        int selectedHintType = availableHintTypes.get(randomIndex);
        
        // Update the recently used hints list
        recentlyUsedHints.add(selectedHintType);
        if (recentlyUsedHints.size() > MAX_RECENT_HINTS) {
            recentlyUsedHints.remove(0); // Remove the oldest hint
        }
        
        // Generate the hint based on the selected type
        return generateHintByType(selectedHintType, targetNumber, difficultyMax);
    }
    
    /**
     * Determine which hint types are appropriate for the current difficulty and target number
     */
    private static List<Integer> getValidHintTypes(int difficultyMax, int targetNumber) {
        List<Integer> validTypes = new ArrayList<>();
        
        // Hint type 0: Range hint - always valid
        validTypes.add(0);
        
        // Hint type 1: Even/Odd hint - always valid
        validTypes.add(1);
        
        // Hint type 2: Greater/less than half of max - always valid
        validTypes.add(2);
        
        // Hint type 3: Number of digits hint - only useful for larger ranges
        if (difficultyMax > 9) {
            validTypes.add(3);
        }
        
        // Hint type 4: First digit hint - only for numbers >= 10
        if (targetNumber >= 10) {
            validTypes.add(4);
        }
        
        // Hint type 5: Last digit hint - always valid
        validTypes.add(5);
        
        // Hint type 6: Specific range hint - always valid
        validTypes.add(6);
        
        // Hint type 7: Sum of digits hint - always valid
        validTypes.add(7);
        
        // Hint type 8: Digit relationship - only for numbers >= 10
        if (targetNumber >= 10) {
            validTypes.add(8);
        }
        
        // Hint type 9: Prime/Composite hint - only for numbers > 1
        if (targetNumber > 1) {
            validTypes.add(9);
        }
        
        // Hint type 10: Comparison to average - always valid (renumbered from 12)
        validTypes.add(10);
        
        // Hint type 11: Math riddle hint - always valid (renumbered from 13)
        validTypes.add(11);
        
        return validTypes;
    }
    
    /**
     * Generate a specific hint based on the hint type
     */
    private static String generateHintByType(int hintType, int targetNumber, int difficultyMax) {
        switch (hintType) {
            case 0:
                // Range hint - narrow range
                return generateRangeHint(targetNumber, difficultyMax);
                
            case 1:
                // Even/Odd hint
                return targetNumber % 2 == 0 ? 
                       "The number is even" : 
                       "The number is odd";
                
            case 2:
                // Greater than half of max hint
                int half = difficultyMax / 2;
                return targetNumber > half ? 
                       "The number is greater than " + half : 
                       "The number is less than or equal to " + half;
                
            case 3:
                // Number of digits hint
                int digitCount = String.valueOf(targetNumber).length();
                return "The number has " + digitCount + " digit" + (digitCount > 1 ? "s" : "");
                
            case 4:
                // First digit hint
                int firstDigit = Integer.parseInt(String.valueOf(targetNumber).substring(0, 1));
                return "The first digit is " + firstDigit;
                
            case 5:
                // Last digit hint
                int lastDigit = targetNumber % 10;
                return "The last digit is " + lastDigit;
                
            case 6:
                // Specific range hint with milestone numbers
                return generateSpecificRangeHint(targetNumber, difficultyMax);
                
            case 7:
                // Sum of digits hint
                int sum = 0;
                int temp = targetNumber;
                while (temp > 0) {
                    sum += temp % 10;
                    temp /= 10;
                }
                return "The sum of the digits is " + sum;
                
            case 8:
                // Digit relationship hint
                if (targetNumber < 10) {
                    return "The number has only one digit";
                } else {
                    String numStr = String.valueOf(targetNumber);
                    int first = Character.getNumericValue(numStr.charAt(0));
                    int last = Character.getNumericValue(numStr.charAt(numStr.length() - 1));
                    
                    if (first > last) {
                        return "The first digit is larger than the last digit";
                    } else if (first < last) {
                        return "The first digit is smaller than the last digit";
                    } else {
                        return "The first and last digits are the same";
                    }
                }
                
            case 9:
                // Prime/Composite hint
                return isPrime(targetNumber) ? 
                       "The number is prime" : 
                       "The number is not prime";
                
            case 10:
                // Comparison to average
                double average = (difficultyMax + 1) / 2.0;
                if (Math.abs(targetNumber - average) < 5) {
                    return "The number is close to the average value of " + average;
                } else if (targetNumber > average) {
                    return "The number is above the average value of " + average;
                } else {
                    return "The number is below the average value of " + average;
                }
                
            case 11:
                // Math riddle hint
                return generateMathRiddle(targetNumber);
                
            default:
                // Fallback hint
                return "Think carefully about the range!";
        }
    }
    
    /**
     * Generate a range hint appropriate for the difficulty level
     */
    private static String generateRangeHint(int targetNumber, int difficultyMax) {
        // Adjust range size based on difficulty max
        int rangeSize;
        
        if (difficultyMax <= 10) {
            // For easy, give a very narrow range (±1)
            rangeSize = 2;
        } else if (difficultyMax <= 30) {
            // For medium, give a range of about 25% of max
            rangeSize = difficultyMax / 4;
        } else if (difficultyMax <= 50) {
            // For hard, give a range of about 20% of max
            rangeSize = difficultyMax / 5;
        } else {
            // For impossible, give a range of about 15% of max
            rangeSize = difficultyMax / 7;
        }
        
        int lowerBound = Math.max(1, targetNumber - rangeSize/2);
        int upperBound = Math.min(difficultyMax, targetNumber + rangeSize/2);
        
        return "The number is between " + lowerBound + " and " + upperBound;
    }
    
    /**
     * Generate a hint with specific milestone numbers
     */
    private static String generateSpecificRangeHint(int targetNumber, int difficultyMax) {
        // Create milestone numbers based on difficulty range
        List<Integer> milestones = new ArrayList<>();
        
        // Add appropriate milestone numbers based on difficulty max
        if (difficultyMax <= 10) { // Easy
            // For Easy (1-10), use 3, 5, 7 as milestones
            milestones.add(3);
            milestones.add(5);
            milestones.add(7);
        } else if (difficultyMax <= 30) { // Medium
            // For Medium (1-30), use 5, 10, 15, 20, 25 as milestones
            milestones.add(5);
            milestones.add(10);
            milestones.add(15);
            milestones.add(20);
            milestones.add(25);
        } else if (difficultyMax <= 50) { // Hard
            // For Hard (1-50), use 5, 10, 15, 20 as milestones
            milestones.add(5);
            milestones.add(10);
            milestones.add(15);
            milestones.add(20);
            milestones.add(25);
            milestones.add(30);
            milestones.add(35);
            milestones.add(40);
            milestones.add(45);
        } else { // Impossible
            // For Impossible (1-100), use 5, 10, 15, 20 and other milestones
            milestones.add(5);
            milestones.add(10);
            milestones.add(15);
            milestones.add(20);
            milestones.add(25);
            milestones.add(30);
            milestones.add(40);
            milestones.add(50);
            milestones.add(60);
            milestones.add(70);
            milestones.add(80);
            milestones.add(90);
        }
        
        // Find the two closest milestones that contain the target number
        int lowerMilestone = 0;
        int upperMilestone = difficultyMax;
        
        for (int milestone : milestones) {
            if (milestone < targetNumber && milestone > lowerMilestone) {
                lowerMilestone = milestone;
            }
            if (milestone > targetNumber && milestone < upperMilestone) {
                upperMilestone = milestone;
            }
        }
        
        // If the target is exactly on a milestone, give a different type of hint
        if (milestones.contains(targetNumber)) {
            return "The number is one of these: " + milestones.toString().replace("[", "").replace("]", "");
        }
        
        return "The number is between " + lowerMilestone + " and " + upperMilestone;
    }
    
    /**
     * Check if a number is prime
     */
    private static boolean isPrime(int number) {
        if (number <= 1) {
            return false;
        }
        if (number <= 3) {
            return true;
        }
        if (number % 2 == 0 || number % 3 == 0) {
            return false;
        }
        
        for (int i = 5; i * i <= number; i += 6) {
            if (number % i == 0 || number % (i + 2) == 0) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Generate a math riddle hint
     */
    private static String generateMathRiddle(int targetNumber) {
        int riddleType = random.nextInt(5);
        
        switch (riddleType) {
            case 0: // Addition riddle
                int addend = random.nextInt(10) + 1;
                return "If you add " + addend + " to this number, you get " + (targetNumber + addend);
                
            case 1: // Subtraction riddle
                int subtrahend = random.nextInt(10) + 1;
                return "If you subtract " + subtrahend + " from this number, you get " + (targetNumber - subtrahend);
                
            case 2: // Multiplication riddle
                int multiplier = random.nextInt(5) + 2;
                return "If you multiply this number by " + multiplier + ", you get " + (targetNumber * multiplier);
                
            case 3: // Division riddle (only if cleanly divisible)
                int divisor = 2;
                if (targetNumber % 3 == 0) divisor = 3;
                else if (targetNumber % 5 == 0) divisor = 5;
                return "If you divide this number by " + divisor + ", you get " + (targetNumber / divisor);
                
            case 4: // Double plus/minus something
                int adjustment = random.nextInt(10) + 1;
                return "If you double this number and add " + adjustment + ", you get " + (targetNumber * 2 + adjustment);
                
            default:
                return "I'm thinking of a number that when doubled equals " + (targetNumber * 2);
        }
    }
}
