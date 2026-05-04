import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.IntStream;

/**
 * Demonstrates solving the classic Two Sum problem using modern Java features.
 * Features used: Records (JDK 14+), Pattern Matching (Open JDK 21+), 
 * Stream API, var keyword (JDK 10+), and text blocks (Open JDK 15+).
 */
public class TwoSumDemo {

    /**
     * Result record to represent the two indices that sum to target.
     * Using records (JDK 14+) for immutable data carriers.
     */
    public record TwoSumResult(int index1, int index2, int value1, int value2, int sum) {
        // Compact constructor with validation
        public TwoSumResult {
            if (index1 < 0 || index2 < 0) {
                throw new IllegalArgumentException("Indices must be non-negative");
            }
            if (index1 == index2) {
                throw new IllegalArgumentException("Indices must be different");
            }
        }
        
        @Override
        public String toString() {
            return "Result[indices=(%d, %d), values=(%d, %d), sum=%d]"
                .formatted(index1, index2, value1, value2, sum);
        }
    }

    /**
     * Sealed interface for different solution strategies.
     * Using sealed types (JDK 17+) to restrict implementations.
     */
    public sealed interface SolutionStrategy 
        permits BruteForceStrategy, HashMapStrategy, TwoPointerStrategy {
        Optional<TwoSumResult> findTwoSum(int[] nums, int target);
        String getName();
    }

    /**
     * Brute force approach - O(n²) time complexity.
     */
    public static final class BruteForceStrategy implements SolutionStrategy {
        @Override
        public Optional<TwoSumResult> findTwoSum(int[] nums, int target) {
            for (var i = 0; i < nums.length; i++) {
                for (var j = i + 1; j < nums.length; j++) {
                    if (nums[i] + nums[j] == target) {
                        return Optional.of(new TwoSumResult(i, j, nums[i], nums[j], target));
                    }
                }
            }
            return Optional.empty();
        }

        @Override
        public String getName() {
            return "Brute Force O(n²)";
        }
    }

    /**
     * HashMap approach - O(n) time complexity, O(n) space.
     */
    public static final class HashMapStrategy implements SolutionStrategy {
        @Override
        public Optional<TwoSumResult> findTwoSum(int[] nums, int target) {
            var numMap = new HashMap<Integer, Integer>();
            
            for (var i = 0; i < nums.length; i++) {
                var complement = target - nums[i];
                
                if (numMap.containsKey(complement)) {
                    var j = numMap.get(complement);
                    return Optional.of(new TwoSumResult(j, i, nums[j], nums[i], target));
                }
                
                numMap.put(nums[i], i);
            }
            
            return Optional.empty();
        }

        @Override
        public String getName() {
            return "HashMap O(n)";
        }
    }

    /**
     * Two pointer approach - O(n log n) time (due to sorting), O(1) extra space.
     * Note: This modifies indices due to sorting, so we track original indices.
     */
    public static final class TwoPointerStrategy implements SolutionStrategy {
        private record IndexedValue(int value, int originalIndex) 
            implements Comparable<IndexedValue> {
            @Override
            public int compareTo(IndexedValue other) {
                return Integer.compare(this.value, other.value);
            }
        }

        @Override
        public Optional<TwoSumResult> findTwoSum(int[] nums, int target) {
            var indexed = IntStream.range(0, nums.length)
                .mapToObj(i -> new IndexedValue(nums[i], i))
                .sorted()
                .toList();
            
            var left = 0;
            var right = indexed.size() - 1;
            
            while (left < right) {
                var sum = indexed.get(left).value + indexed.get(right).value;
                
                if (sum == target) {
                    var leftItem = indexed.get(left);
                    var rightItem = indexed.get(right);
                    return Optional.of(new TwoSumResult(
                        leftItem.originalIndex, 
                        rightItem.originalIndex,
                        leftItem.value,
                        rightItem.value,
                        target
                    ));
                } else if (sum < target) {
                    left++;
                } else {
                    right--;
                }
            }
            
            return Optional.empty();
        }

        @Override
        public String getName() {
            return "Two Pointer O(n log n)";
        }
    }

    public static void executeDemo() {
        System.out.println("=== Two Sum Problem - Modern Java Implementation ===\n");

        // Test cases using text blocks (JDK 15+) for better readability
        var testCases = List.of(
            new TestCase(new int[]{2, 7, 11, 15}, 9, "Basic case"),
            new TestCase(new int[]{3, 2, 4}, 6, "No sorted input"),
            new TestCase(new int[]{3, 3}, 6, "Duplicate values"),
            new TestCase(new int[]{1, 5, 3, 8, 12, 4}, 16, "Larger array"),
            new TestCase(new int[]{-1, -2, -3, -4, -5}, -8, "Negative numbers")
        );

        // List of strategies to test
        var strategies = List.<SolutionStrategy>of(
            new HashMapStrategy(),
            new BruteForceStrategy(),
            new TwoPointerStrategy()
        );

        // Run all test cases with all strategies
        for (var testCase : testCases) {
            System.out.println("Test: %s".formatted(testCase.description));
            System.out.println("Array: %s, Target: %d"
                .formatted(Arrays.toString(testCase.nums), testCase.target));
            
            for (var strategy : strategies) {
                runWithTiming(strategy, testCase);
            }
            System.out.println();
        }

        // Demonstrate pattern matching with sealed types
        demonstratePatternMatching(strategies.getFirst(), testCases.getFirst());
    }

    /**
     * Helper record for test cases.
     */
    private record TestCase(int[] nums, int target, String description) {}

    /**
     * Runs a strategy with timing information.
     */
    private static void runWithTiming(SolutionStrategy strategy, TestCase testCase) {
        var startTime = System.nanoTime();
        var result = strategy.findTwoSum(testCase.nums, testCase.target);
        var duration = System.nanoTime() - startTime;

        System.out.println("  %-25s -> %s (%,d ns)".formatted(
            strategy.getName(),
            result.map(Object::toString).orElse("No solution found"),
            duration
        ));
    }

    /**
     * Demonstrates pattern matching with sealed types (JDK 21+).
     */
    private static void demonstratePatternMatching(SolutionStrategy strategy, TestCase testCase) {
        System.out.println("=== Pattern Matching Demo (JDK 21+) ===");
        
        // Pattern matching with sealed types
        var analysisText = switch (strategy) {
            case HashMapStrategy hs -> """
                HashMap Strategy Analysis:
                - Time Complexity: O(n)
                - Space Complexity: O(n)
                - Best for: General purpose, unsorted arrays
                - Trade-off: Uses extra space for hash table
                """;
            case BruteForceStrategy bfs -> """
                Brute Force Strategy Analysis:
                - Time Complexity: O(n²)
                - Space Complexity: O(1)
                - Best for: Very small arrays
                - Trade-off: Slow for large inputs
                """;
            case TwoPointerStrategy tps -> """
                Two Pointer Strategy Analysis:
                - Time Complexity: O(n log n)
                - Space Complexity: O(n) for tracking indices
                - Best for: When sorting is acceptable
                - Trade-off: Modifies order, requires sorting
                """;
        };
        
        System.out.println(analysisText);
    }

    /**
     * Demonstrates using BiFunction with lambda expressions.
     */
    public static void demonstrateFunctionalApproach() {
        System.out.println("\n=== Functional Programming Approach ===");
        
        BiFunction<int[], Integer, Optional<TwoSumResult>> twoSumFunc = 
            (nums, target) -> new HashMapStrategy().findTwoSum(nums, target);
        
        var result = twoSumFunc.apply(new int[]{1, 2, 3, 4, 5}, 9);
        System.out.println("Using BiFunction: " + result.orElse(null));
    }
}
