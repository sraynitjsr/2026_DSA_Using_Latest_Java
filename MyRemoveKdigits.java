import java.util.*;
import java.util.stream.Collectors;

/**
 * LeetCode #402: Remove K Digits
 * 
 * Problem: Given a non-negative integer num represented as a string,
 * remove k digits from the number so that the new number is the smallest possible.
 * 
 * Solution uses monotonic stack (deque) approach.
 * Time: O(n), Space: O(n)
 */
public final class MyRemoveKdigits {

    /**
     * Removes k digits from the number to make it smallest.
     * Uses modern Java features including var, enhanced for-each, and switch expressions.
     */
    public String removeKdigits(String num, int k) {
        // Input validation using modern switch expression
        var validationResult = validateInput(num, k);
        if (validationResult != null) {
            return validationResult;
        }

        var stack = new ArrayDeque<Character>();
        var remainingRemovals = k;

        // Build monotonic increasing stack
        for (var digit : num.toCharArray()) {
            while (!stack.isEmpty() && 
                   remainingRemovals > 0 && 
                   stack.peekLast() > digit) {
                stack.pollLast();
                remainingRemovals--;
            }
            stack.addLast(digit);
        }

        // Remove remaining digits from the end if needed
        while (remainingRemovals > 0 && !stack.isEmpty()) {
            stack.pollLast();
            remainingRemovals--;
        }

        // Build result string, skipping leading zeros
        var result = new StringBuilder();
        var skipLeadingZeros = true;
        
        for (var digit : stack) {
            if (skipLeadingZeros && digit == '0') {
                continue;
            }
            skipLeadingZeros = false;
            result.append(digit);
        }

        return result.isEmpty() ? "0" : result.toString();
    }

    /**
     * Alternative implementation using Stream API and functional programming.
     * Demonstrates modern Java's functional capabilities.
     */
    public String removeKdigitsFunctional(String num, int k) {
        if (num == null || num.isEmpty() || k >= num.length()) {
            return "0";
        }

        var stack = new ArrayDeque<Character>();
        var remainingRemovals = k;

        for (var digit : num.toCharArray()) {
            while (!stack.isEmpty() && remainingRemovals > 0 && stack.peekLast() > digit) {
                stack.pollLast();
                remainingRemovals--;
            }
            stack.addLast(digit);
        }

        // Use sequenced collection operations (Java 21+)
        while (remainingRemovals-- > 0 && !stack.isEmpty()) {
            stack.removeLast();
        }

        // Use Stream API to process result
        var result = stack.stream()
            .dropWhile(c -> c == '0')
            .map(String::valueOf)
            .collect(Collectors.joining());

        return result.isEmpty() ? "0" : result;
    }

    /**
     * Validates input using modern switch expression (Java 21+).
     */
    private String validateInput(String num, int k) {
        return switch (num) {
            case null -> "0";
            case String s when s.isEmpty() -> "0";
            case String s when k <= 0 -> s;
            case String s when k >= s.length() -> "0";
            default -> null; // Valid input
        };
    }

    /**
     * Demo method showing test cases with modern record-based approach.
     */
    public static void executeDemo() {
        System.out.println("=== Remove K Digits Demo ===\n");

        var solution = new MyRemoveKdigits();
        
        var testCases = List.of(
            new TestCase("1432219", 3, "1219", "Remove 3 digits"),
            new TestCase("10200", 1, "200", "Leading zeros"),
            new TestCase("10", 2, "0", "Remove all"),
            new TestCase("9", 1, "0", "Single digit"),
            new TestCase("112", 1, "11", "Keep duplicates"),
            new TestCase("5337", 2, "33", "Middle removal")
        );

        testCases.forEach(tc -> {
            var result = solution.removeKdigits(tc.num, tc.k);
            var status = result.equals(tc.expected) ? "✓ PASS" : "✗ FAIL";
            
            System.out.println("""
                Test: %s
                Input: "%s", k=%d
                Expected: "%s", Got: "%s" [%s]
                """.formatted(
                    tc.description,
                    tc.num,
                    tc.k,
                    tc.expected,
                    result,
                    status
                ));
        });
    }

    /**
     * Interactive demo method for user input.
     */
    public static void interactiveDemo() {
        try (var scanner = new Scanner(System.in)) {
            System.out.println("Enter a number:");
            var num = scanner.next();
            
            System.out.println("Enter k (digits to remove):");
            var k = scanner.nextInt();
            
            var solution = new MyRemoveKdigits();
            var result = solution.removeKdigits(num, k);
            
            System.out.println("Result: " + result);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    /**
     * Test case record using modern Java records (JDK 16+).
     */
    private record TestCase(String num, int k, String expected, String description) {
        TestCase {
            Objects.requireNonNull(num, "Number cannot be null");
            Objects.requireNonNull(expected, "Expected result cannot be null");
            Objects.requireNonNull(description, "Description cannot be null");
        }
    }
}
