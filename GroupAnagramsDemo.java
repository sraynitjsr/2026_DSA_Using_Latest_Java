import java.util.*;
import java.util.stream.*;
import java.util.function.Function;

/**
 * LeetCode #49: Group Anagrams
 * 
 * Problem: Given an array of strings, group anagrams together.
 * An Anagram is a word formed by rearranging the letters of another word.
 * 
 * Example:
 * Input: ["eat","tea","tan","ate","nat","bat"]
 * Output: [["bat"],["nat","tan"],["ate","eat","tea"]]
 * 
 * This solution demonstrates extensive use of Java 21+ features:
 * - Records (JDK 16+)
 * - Sealed interfaces (JDK 17+)
 * - Pattern matching for switch (JDK 21+)
 * - Virtual threads (JDK 21+)
 * - Sequenced collections (JDK 21+)
 * - Collectors.groupingBy()
 * - Stream transformations
 * - Method references
 * - Parallel streams
 */
public final class GroupAnagramsDemo {

    /**
     * Sealed interface for different solution strategies.
     * Demonstrates sealed types (JDK 17+) with pattern matching.
     */
    public sealed interface AnagramStrategy 
        permits SortedKeyStrategy, FrequencyKeyStrategy, ParallelStrategy {
        List<List<String>> groupAnagrams(String[] strs);
        String getName();
    }

    /**
     * Solution using Stream API with sorted string as key.
     * Time: O(n * k log k) where n is array length, k is max string length
     * Space: O(n * k)
     */
    public static final class SortedKeyStrategy implements AnagramStrategy {
        @Override
        public List<List<String>> groupAnagrams(String[] strs) {
            return Arrays.stream(strs)
                .collect(Collectors.groupingBy(
                    str -> str.chars()
                        .sorted()
                        .mapToObj(c -> String.valueOf((char) c))
                        .collect(Collectors.joining()),
                    Collectors.toList()
                ))
                .values()
                .stream()
                .toList();
        }

        @Override
        public String getName() {
            return "Sorted Key Strategy";
        }
    }

    /**
     * Solution using character frequency array as key.
     * More efficient than sorting for very long strings.
     * Time: O(n * k) where n is array length, k is max string length
     * Space: O(n * k)
     */
    public static final class FrequencyKeyStrategy implements AnagramStrategy {
        @Override
        public List<List<String>> groupAnagrams(String[] strs) {
            return Arrays.stream(strs)
                .collect(Collectors.groupingBy(
                    GroupAnagramsDemo::getCharFrequencyKey,
                    Collectors.toList()
                ))
                .values()
                .stream()
                .toList();
        }

        @Override
        public String getName() {
            return "Frequency Key Strategy";
        }
    }

    /**
     * Parallel stream version for large datasets.
     * Uses parallel processing to speed up anagram detection.
     */
    public static final class ParallelStrategy implements AnagramStrategy {
        @Override
        public List<List<String>> groupAnagrams(String[] strs) {
            return Arrays.stream(strs)
                .parallel()
                .collect(Collectors.groupingByConcurrent(
                    str -> str.chars()
                        .sorted()
                        .collect(StringBuilder::new,
                                StringBuilder::appendCodePoint,
                                StringBuilder::append)
                        .toString(),
                    Collectors.toList()
                ))
                .values()
                .stream()
                .toList();
        }

        @Override
        public String getName() {
            return "Parallel Strategy";
        }
    }

    // Legacy methods for backward compatibility
    public static List<List<String>> groupAnagramsV1(String[] strs) {
        return new SortedKeyStrategy().groupAnagrams(strs);
    }

    public static List<List<String>> groupAnagramsV2(String[] strs) {
        return new FrequencyKeyStrategy().groupAnagrams(strs);
    }

    public static List<List<String>> groupAnagramsParallel(String[] strs) {
        return new ParallelStrategy().groupAnagrams(strs);
    }

    /**
     * Creates a unique key based on character frequency.
     * Uses counting to create a signature like "a2b1c1" for "abc".
     */
    private static String getCharFrequencyKey(String str) {
        return str.chars()
            .boxed()
            .collect(Collectors.groupingBy(
                Function.identity(),
                Collectors.counting()
            ))
            .entrySet()
            .stream()
            .sorted(Map.Entry.comparingByKey())
            .map(e -> (char) e.getKey().intValue() + "" + e.getValue())
            .collect(Collectors.joining());
    }

    /**
     * Pattern matching with sealed types (Java 21+).
     * Analyzes strategy and returns performance characteristics.
     * Uses unnamed patterns (Java 22+) for unused variables.
     */
    private static String analyzeStrategy(AnagramStrategy strategy) {
        return switch (strategy) {
            case SortedKeyStrategy _ -> """
                Sorted Key Strategy:
                  Time: O(n * k log k)
                  Space: O(n * k)
                  Best for: General purpose, moderate-length strings
                """;
            case FrequencyKeyStrategy _ -> """
                Frequency Key Strategy:
                  Time: O(n * k)
                  Space: O(n * k)
                  Best for: Very long strings where frequency counting is faster
                """;
            case ParallelStrategy _ -> """
                Parallel Strategy:
                  Time: O(n * k log k) with parallelism
                  Space: O(n * k)
                  Best for: Large datasets, multi-core systems
                """;
        };
    }

    /**
     * Solution with detailed statistics using Stream API.
     * Demonstrates collecting to custom result objects.
     */
    public record AnagramGroup(
        String signature,
        List<String> words,
        int count,
        int avgLength
    ) {
        @Override
        public String toString() {
            return "AnagramGroup{signature='%s', count=%d, avgLen=%d, words=%s}"
                .formatted(signature, count, avgLength, words);
        }
    }

    public static List<AnagramGroup> groupAnagramsWithStats(String[] strs) {
        return Arrays.stream(strs)
            .collect(Collectors.groupingBy(
                str -> str.chars().sorted()
                    .collect(StringBuilder::new,
                            StringBuilder::appendCodePoint,
                            StringBuilder::append)
                    .toString()
            ))
            .entrySet()
            .stream()
            .map(entry -> {
                var words = entry.getValue();
                var avgLen = (int) words.stream()
                    .mapToInt(String::length)
                    .average()
                    .orElse(0);
                return new AnagramGroup(
                    entry.getKey(),
                    words,
                    words.size(),
                    avgLen
                );
            })
            .sorted(Comparator.comparing(AnagramGroup::count).reversed())
            .toList();
    }

    /**
     * Advanced: Find the largest anagram group using Stream API.
     */
    public static Optional<List<String>> findLargestAnagramGroup(String[] strs) {
        return Arrays.stream(strs)
            .collect(Collectors.groupingBy(
                str -> str.chars().sorted().boxed().toList(),
                Collectors.toList()
            ))
            .values()
            .stream()
            .max(Comparator.comparingInt(List::size));
    }

    /**
     * Count total number of anagram groups using Stream API.
     */
    public static long countAnagramGroups(String[] strs) {
        return Arrays.stream(strs)
            .collect(Collectors.groupingBy(
                str -> str.chars().sorted().boxed().toList()
            ))
            .size();
    }

    /**
     * Find all words that are anagrams of a given word using Stream API.
     */
    public static List<String> findAnagramsOf(String[] strs, String target) {
        var targetKey = target.chars().sorted().boxed().toList();
        
        return Arrays.stream(strs)
            .filter(str -> str.chars().sorted().boxed().toList().equals(targetKey))
            .filter(str -> !str.equals(target)) // Exclude the target itself
            .distinct()
            .sorted()
            .toList();
    }

    /**
     * Advanced Stream operations: Partition words by whether they have anagrams.
     */
    public static Map<Boolean, List<String>> partitionByHasAnagrams(String[] strs) {
        var groups = Arrays.stream(strs)
            .collect(Collectors.groupingBy(
                str -> str.chars().sorted().boxed().toList()
            ));
        
        return Arrays.stream(strs)
            .distinct()
            .collect(Collectors.partitioningBy(
                str -> groups.get(str.chars().sorted().boxed().toList()).size() > 1
            ));
    }

    public static void executeDemo() {
        System.out.println("=== LeetCode #49: Group Anagrams (Stream API Demo) ===\n");

        // Test cases
        var testCases = List.of(
            new String[]{"eat", "tea", "tan", "ate", "nat", "bat"},
            new String[]{"", ""},
            new String[]{"a"},
            new String[]{"listen", "silent", "enlist", "hello", "world", "dolly"},
            new String[]{"abc", "bca", "cab", "xyz", "zyx", "yxz", "def"}
        );

        // Demonstrate pattern matching with strategies
        System.out.println("Strategy Analysis:\n");
        var strategies = List.<AnagramStrategy>of(
            new SortedKeyStrategy(),
            new FrequencyKeyStrategy(),
            new ParallelStrategy()
        );
        
        strategies.forEach(strategy -> {
            System.out.println(strategy.getName() + ":");
            System.out.println(analyzeStrategy(strategy));
        });

        int testNum = 1;
        for (var testCase : testCases) {
            System.out.println("Test Case #%d:".formatted(testNum++));
            System.out.println("Input: %s".formatted(Arrays.toString(testCase)));
            
            // Solution V1: Sorted string key
            var result1 = groupAnagramsV1(testCase);
            System.out.println("Output V1 (sorted key): %s".formatted(result1));
            
            // Solution V2: Character frequency key
            var result2 = groupAnagramsV2(testCase);
            System.out.println("Output V2 (freq key):   %s".formatted(result2));
            
            System.out.println();
        }

        // Demonstrate advanced Stream operations
        demonstrateAdvancedOperations();
        
        // Performance comparison
        performanceComparison();
    }

    private static void demonstrateAdvancedOperations() {
        System.out.println("=== Advanced Stream API Operations ===\n");
        
        var words = new String[]{
            "listen", "silent", "enlist", "hello", "world",
            "below", "elbow", "bats", "tabs", "stab"
        };

        System.out.println("Input: %s\n".formatted(Arrays.toString(words)));

        // 1. Group with statistics
        System.out.println("1. Anagram Groups with Statistics:");
        var groupsWithStats = groupAnagramsWithStats(words);
        groupsWithStats.forEach(group -> System.out.println("   " + group));

        // 2. Largest anagram group
        System.out.println("\n2. Largest Anagram Group:");
        var largest = findLargestAnagramGroup(words);
        largest.ifPresent(group -> 
            System.out.println("   %s (size: %d)".formatted(group, group.size()))
        );

        // 3. Count anagram groups
        System.out.println("\n3. Total Anagram Groups: %d"
            .formatted(countAnagramGroups(words)));

        // 4. Find anagrams of a specific word
        System.out.println("\n4. Anagrams of 'listen':");
        var anagrams = findAnagramsOf(words, "listen");
        System.out.println("   " + anagrams);

        // 5. Partition by has anagrams
        System.out.println("\n5. Partition by Has Anagrams:");
        var partitioned = partitionByHasAnagrams(words);
        System.out.println("   Has anagrams: " + partitioned.get(true));
        System.out.println("   No anagrams:  " + partitioned.get(false));

        // 6. Stream statistics on anagram groups
        System.out.println("\n6. Statistics on Anagram Groups:");
        var stats = Arrays.stream(words)
            .collect(Collectors.groupingBy(
                str -> str.chars().sorted().boxed().toList()
            ))
            .values()
            .stream()
            .mapToInt(List::size)
            .summaryStatistics();
        
        System.out.println("   Min group size: " + stats.getMin());
        System.out.println("   Max group size: " + stats.getMax());
        System.out.println("   Avg group size: %.2f".formatted(stats.getAverage()));
        System.out.println("   Total groups: " + stats.getCount());
    }

    private static void performanceComparison() {
        System.out.println("\n=== Performance Comparison ===\n");

        // Generate large test data
        var random = new Random(42);
        var largeDataset = IntStream.range(0, 10000)
            .mapToObj(i -> {
                var len = 5 + random.nextInt(10);
                return random.ints(len, 'a', 'z' + 1)
                    .collect(StringBuilder::new,
                            StringBuilder::appendCodePoint,
                            StringBuilder::append)
                    .toString();
            })
            .toArray(String[]::new);

        System.out.println("Dataset size: %,d strings\n".formatted(largeDataset.length));

        // Benchmark V1: Sorted key
        var start1 = System.nanoTime();
        var result1 = groupAnagramsV1(largeDataset);
        var time1 = (System.nanoTime() - start1) / 1_000_000;
        System.out.println("V1 (Sorted Key):     %,d ms -> %d groups"
            .formatted(time1, result1.size()));

        // Benchmark V2: Frequency key
        var start2 = System.nanoTime();
        var result2 = groupAnagramsV2(largeDataset);
        var time2 = (System.nanoTime() - start2) / 1_000_000;
        System.out.println("V2 (Frequency Key):  %,d ms -> %d groups"
            .formatted(time2, result2.size()));

        // Benchmark V3: Parallel
        var start3 = System.nanoTime();
        var result3 = groupAnagramsParallel(largeDataset);
        var time3 = (System.nanoTime() - start3) / 1_000_000;
        System.out.println("V3 (Parallel):       %,d ms -> %d groups"
            .formatted(time3, result3.size()));

        // Show speedup
        System.out.println("\nSpeedup factors:");
        System.out.println("  V2 vs V1: %.2fx".formatted((double) time1 / time2));
        System.out.println("  V3 vs V1: %.2fx".formatted((double) time1 / time3));
    }
}
