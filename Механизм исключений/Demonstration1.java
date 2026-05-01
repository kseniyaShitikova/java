package com.mycompany.demonstration1;

import java.io.*;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Demonstration1 {

    public static int readAgeFromFile(String path) throws FileNotFoundException {
        File file = new File(path);
        if (!file.exists()) {
            throw new FileNotFoundException("File not found: " + path);
        }
        try (Scanner fileScanner = new Scanner(file)) {
            if (fileScanner.hasNextInt()) {
                return fileScanner.nextInt();
            } else {
                throw new IllegalArgumentException("File does not contain a number");
            }
        }
    }

    public static int readAgeFromConsole() {
        Scanner consoleScanner = new Scanner(System.in);
        System.out.print("Enter age: ");
        return consoleScanner.nextInt();
    }

    public static void validateAge(int age) throws IllegalAgeException {
        if (age < 0) {
            throw new IllegalArgumentException("Age cannot be negative: " + age);
        }
        if (age < 18) {
            throw new IllegalAgeException("Age " + age + " is less than 18");
        }
    }

    static class IllegalAgeException extends Exception {
        public IllegalAgeException(String message) {
            super(message);
        }
    }

    static class AgeValidator {
        protected int minAge;
        
        public AgeValidator(int minAge) {
            this.minAge = minAge;
        }
        
        public void checkAge(int age) throws IllegalAgeException {
            if (age < 0) {
                throw new IllegalArgumentException("Age cannot be negative");
            }
            if (age < minAge) {
                throw new IllegalAgeException("Age " + age + " is less than " + minAge);
            }
            System.out.println(" Age " + age + " is valid (>= " + minAge + ")");
        }
    }
    
    static class StrictAgeValidator extends AgeValidator {
        
        public StrictAgeValidator(int minAge) {
            super(minAge);
        }
        
        @Override
        public void checkAge(int age) throws IllegalAgeException {
            if (age > 100) {
                throw new TooOldException("Age " + age + " is too old (max 100)");
            }
            
            if (minAge < 18 && age >= minAge && age < 18) {
                throw new IllegalAgeException("Even with minAge=" + minAge + 
                        ", age must be at least 18 in strict mode");
            }
            
            super.checkAge(age);
        }
    }
    
    static class TooOldException extends IllegalAgeException {
        public TooOldException(String message) {
            super(message);
        }
    }
    
    public static void testLSP() {
        System.out.println("\n" );
        System.out.println("=== LSP DEMONSTRATION: Age Validation ===");
        
        AgeValidator validator = new AgeValidator(16);
        
        System.out.println("\n--- Testing with AgeValidator (parent) ---");
        testValidator(validator, 17);
        testValidator(validator, 15);
        testValidator(validator, 150);
        
        System.out.println("\n--- Testing with StrictAgeValidator (child) ---");
        AgeValidator strictValidator = new StrictAgeValidator(16);
        
        testValidator(strictValidator, 17);
        testValidator(strictValidator, 15);
        testValidator(strictValidator, 150);
    }
    
    public static void testValidator(AgeValidator validator, int age) {
        System.out.println("\nTesting age: " + age);
        try {
            validator.checkAge(age);
        } catch (IllegalAgeException e) {
            System.out.println(" Caught: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            
            if (e instanceof TooOldException) {
                System.out.println("  LSP VIOLATION: Parent doesn't throw TooOldException!");
            }
        } catch (IllegalArgumentException e) {
            System.out.println(" Caught IllegalArgumentException: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        int age = 0;
        boolean success = false;
        boolean fromFile = false;

        System.out.println("=== Demonstration: file and console handling ===");

        try {
            age = readAgeFromFile("age.txt");
            System.out.println("Age read from file: " + age);
            fromFile = true;
            success = true;
        } catch (FileNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
            System.out.println("Try console input: ");
        } catch (IllegalArgumentException e) {
            System.out.println("File error: " + e.getMessage());
            return;
        }

        if (!fromFile) {
            try {
                age = readAgeFromConsole();
                success = true;
            } catch (InputMismatchException e2) {
                System.out.println("Input error: integer required.");
                return;
            }
        }

        if (success && age != 0) {
            try {
                validateAge(age);
                System.out.println("Age is valid: " + age);
            } catch (IllegalArgumentException e) {
                System.out.println("Validation error (unchecked): " + e.getMessage());
            } catch (IllegalAgeException e) {
                System.out.println("Validation error (checked): " + e.getMessage());
            } finally {
                System.out.println("Finally block: age validation completed.");
            }
        }
        
        testLSP();
    }
}