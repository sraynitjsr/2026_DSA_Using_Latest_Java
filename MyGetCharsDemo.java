/**
 * Demonstrates JDK 25's new getChars() default method in CharSequence interface.
 * This method allows bulk character extraction without casting to specific implementations.
 */

import java.nio.CharBuffer;

public class MyGetCharsDemo {

    public static void executeDemo() {
        System.out.println("=== JDK 25 getChars() Method Demo ===\n");
        
        // Using var keyword (JDK 10+) for cleaner code
        var str = "JDK25_Feature";
        var sb = new StringBuilder("StringBuilder_Test");
        var cb = CharBuffer.wrap("CharBuffer_Test");
        var custom = new SimpleSequence("Custom_Sequence");

        // Basic extraction demo
        System.out.println("1. Basic Extraction (first 5 chars):");
        printBulkData(str);
        printBulkData(sb);
        printBulkData(cb);
        printBulkData(custom);
        
        // Edge cases demo
        System.out.println("\n2. Edge Cases:");
        demonstrateEdgeCases();
        
        // Performance comparison
        System.out.println("\n3. Old vs New Approach:");
        compareApproaches("Performance_Test_String");
        
        // Practical use case
        System.out.println("\n4. Practical Use Case:");
        demonstratePracticalUse();
    }

    /**
     * Extracts and prints the first 5 characters from a CharSequence.
     */
    public static void printBulkData(CharSequence cs) {
        var buffer = new char[5];
        cs.getChars(0, 5, buffer, 0);
        System.out.println("  %-20s -> %s".formatted(
            cs.getClass().getSimpleName(), 
            new String(buffer)
        ));
    }
    
    /**
     * Demonstrates edge cases like empty sequences, full extraction, and partial copies.
     */
    private static void demonstrateEdgeCases() {
        // Full string extraction
        var text = "JAVA";
        var fullBuffer = new char[text.length()];
        ((CharSequence) text).getChars(0, text.length(), fullBuffer, 0);
        System.out.println("  Full extraction: " + new String(fullBuffer));
        
        // Middle section extraction
        var middle = new char[4];
        ((CharSequence) "0123456789").getChars(3, 7, middle, 0);
        System.out.println("  Middle (3-7): " + new String(middle));
        
        // Empty to specific offset
        var offsetBuffer = new char[10];
        java.util.Arrays.fill(offsetBuffer, '-');
        ((CharSequence) "ABC").getChars(0, 3, offsetBuffer, 5);
        System.out.println("  With offset: " + new String(offsetBuffer));
    }
    
    /**
     * Compares the new getChars() method with the traditional charAt() loop approach.
     */
    private static void compareApproaches(String text) {
        var buffer = new char[11];
        
        // New way (JDK 25): Direct bulk copy
        long start1 = System.nanoTime();
        ((CharSequence) text).getChars(0, 11, buffer, 0);
        long time1 = System.nanoTime() - start1;
        System.out.println("  New getChars():  " + new String(buffer) + " (%d ns)".formatted(time1));
        
        // Old way: Character-by-character using charAt()
        var buffer2 = new char[11];
        long start2 = System.nanoTime();
        for (int i = 0; i < 11; i++) {
            buffer2[i] = text.charAt(i);
        }
        long time2 = System.nanoTime() - start2;
        System.out.println("  Old charAt():    " + new String(buffer2) + " (%d ns)".formatted(time2));
    }
    
    /**
     * Shows a practical use case: extracting file extensions or parsing tokens.
     */
    private static void demonstratePracticalUse() {
        var filename = "document.pdf";
        var extensionStart = filename.indexOf('.') + 1;
        var extLength = filename.length() - extensionStart;
        
        var extension = new char[extLength];
        ((CharSequence) filename).getChars(extensionStart, filename.length(), extension, 0);
        
        System.out.println("  Filename: " + filename);
        System.out.println("  Extension: " + new String(extension));
    }

    /**
     * Custom CharSequence implementation to demonstrate interface flexibility.
     * Implements the default getChars() method inherited from CharSequence.
     */
    static class SimpleSequence implements CharSequence {
        private final String internal;
        
        SimpleSequence(String internal) { 
            this.internal = internal; 
        }
        
        @Override 
        public int length() { 
            return internal.length(); 
        }
        
        @Override 
        public char charAt(int index) { 
            return internal.charAt(index); 
        }
        
        @Override 
        public CharSequence subSequence(int start, int end) { 
            return internal.subSequence(start, end); 
        }
        
        @Override 
        public String toString() { 
            return internal; 
        }
        
        // Note: getChars() is inherited as a default method from CharSequence (JDK 25)
        // No need to override unless you want optimized behavior
    }
}
