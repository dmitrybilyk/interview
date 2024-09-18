package com.conduct.interview._1_bases.immutables_classes.why_string_is_immutable;

public class ForSecurity {
    public static void main(String[] args) {
        // File path passed to a method to check file access permissions
        String filePath = "/user/data/config.txt";
        checkFileAccess(filePath);  // Expected safe behavior
        
        // Even if the filePath variable is passed around, it can't be modified
        // The original filePath remains secure
        System.out.println("Original File Path: " + filePath);  // Output: /user/data/config.txt
    }

    public static void checkFileAccess(String path) {
        // Perform security checks (e.g., validate file path permissions)
        System.out.println("Checking access for: " + path);

        // Hypothetically, if an attacker tried to modify the path, it wouldn't affect the original
        // This would be impossible as Strings are immutable
    }
}
