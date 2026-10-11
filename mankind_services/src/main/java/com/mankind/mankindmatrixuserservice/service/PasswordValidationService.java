package com.mankind.mankindmatrixuserservice.service;

import com.mankind.mankindmatrixuserservice.exception.PasswordValidationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class PasswordValidationService {

    private static final int MIN_LENGTH = 8;
    private static final int MAX_LENGTH = 128;
    
    // Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{" + MIN_LENGTH + "," + MAX_LENGTH + "}$"
    );

    /**
     * Validates a password according to security requirements
     * 
     * @param password the password to validate
     * @throws PasswordValidationException if password validation fails
     */
    public void validatePassword(String password) {
        List<String> errors = new ArrayList<>();

        if (password == null || password.trim().isEmpty()) {
            errors.add("Password cannot be empty");
        }

        if (password != null && password.length() < MIN_LENGTH) {
            errors.add("Password must be at least " + MIN_LENGTH + " characters long");
        }

        if (password != null && password.length() > MAX_LENGTH) {
            errors.add("Password cannot exceed " + MAX_LENGTH + " characters");
        }

        if (password != null && !PASSWORD_PATTERN.matcher(password).matches()) {
            errors.add("Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character (@$!%*?&)");
        }

        // Check for common weak passwords
        if (password != null && isCommonPassword(password)) {
            errors.add("Password is too common and not secure");
        }

        if (!errors.isEmpty()) {
            throw new PasswordValidationException(errors);
        }
    }

    /**
     * Checks if the password meets all security requirements
     * 
     * @param password the password to check
     * @return true if password is valid, false otherwise
     */
    public boolean isValidPassword(String password) {
        try {
            validatePassword(password);
            return true;
        } catch (PasswordValidationException e) {
            return false;
        }
    }

    /**
     * Checks if password is a common weak password
     * 
     * @param password the password to check
     * @return true if password is common, false otherwise
     */
    private boolean isCommonPassword(String password) {
        if (password == null) return false;
        
        String lowerPassword = password.toLowerCase();
        
        // List of common weak passwords (deduplicated)
        String[] commonPasswords = {
            "password", "123456", "123456789", "qwerty", "abc123", "password123",
            "admin", "letmein", "welcome", "monkey", "dragon", "master", "hello",
            "freedom", "whatever", "qazwsx", "trustno1", "jordan", "harley",
            "ranger", "iwantu", "jennifer", "hunter", "buster", "soccer",
            "baseball", "tiger", "charlie", "andrew", "michelle", "love",
            "sunshine", "jessica", "asshole", "696969", "amanda", "access",
            "computer", "cookie", "mickey", "shadow", "maggie", "654321",
            "superman", "george", "ass", "bailey", "calvin", "changeme",
            "diamond", "ncc1701", "jack", "mike", "mustang", "phoenix",
            "porsche", "rosebud", "shit", "thx1138", "winter", "bigtits",
            "barney", "edward", "raiders", "porn", "badass", "blowme",
            "spanky", "bigdaddy", "johnson", "chester", "london", "midnight",
            "blue", "fishing", "000000", "hacker", "slayer", "dolphin",
            "marlin", "stingray", "cowboy", "bruins", "panther", "marvin",
            "dennis", "frank", "horney", "pussy", "badboy", "blowjob"
        };

        for (String common : commonPasswords) {
            if (lowerPassword.equals(common)) {
                return true;
            }
        }

        return false;
    }
} 