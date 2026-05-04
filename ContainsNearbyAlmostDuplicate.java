import java.util.*;

/**
 * LeetCode #220: Contains Duplicate III
 * 
 * Problem: Given an integer array nums and two integers indexDiff and valueDiff,
 * return true if there are two distinct indices i and j such that:
 * - abs(i - j) <= indexDiff
 * - abs(nums[i] - nums[j]) <= valueDiff
 * 
 * Solution uses bucket sort approach with sliding window.
 * Time: O(n), Space: O(min(n, indexDiff))
 */
public final class ContainsNearbyAlmostDuplicate {

    /**
     * Executes test cases demonstrating the algorithm.
     */
    public void start() {
        var testCases = List.of(
            new TestCase(new int[]{1, 2, 3, 1}, 3, 0, true, "Adjacent duplicates"),
            new TestCase(new int[]{1, 5, 9, 1, 5, 9}, 2, 3, false, "No nearby duplicates"),
            new TestCase(new int[]{1, 0, 1, 1}, 1, 2, true, "Within value diff"),
            new TestCase(new int[]{-2147483648, 2147483647}, 1, 1, false, "Edge case: int bounds")
        );

        System.out.println("=== Contains Nearby Almost Duplicate ===\n");
        
        testCases.forEach(tc -> {
            var result = containsNearbyAlmostDuplicate(tc.nums, tc.indexDiff, tc.valueDiff);
            var status = result == tc.expected ? "✓ PASS" : "✗ FAIL";
            
            System.out.println("""
                Test: %s
                Array: %s
                Index Diff: %d, Value Diff: %d
                Expected: %b, Got: %b [%s]
                """.formatted(
                    tc.description,
                    Arrays.toString(tc.nums),
                    tc.indexDiff,
                    tc.valueDiff,
                    tc.expected,
                    result,
                    status
                ));
        });
    }

    /**
     * Checks if array contains nearby almost duplicate using bucket sort.
     * Uses modern switch expression and enhanced pattern matching.
     */
    public boolean containsNearbyAlmostDuplicate(int[] nums, int indexDiff, int valueDiff) {
        // Early return using enhanced switch expression (Java 21+)
        if (valueDiff < 0) {
            return false;
        }

        var buckets = new HashMap<Long, Long>();
        var bucketSize = (long) valueDiff + 1;

        for (var i = 0; i < nums.length; i++) {
            var num = (long) nums[i];
            var bucketId = getBucketId(num, bucketSize);

            // Check current bucket
            if (buckets.containsKey(bucketId)) {
                return true;
            }

            // Check adjacent buckets
            if (buckets.containsKey(bucketId - 1) && 
                Math.abs(num - buckets.get(bucketId - 1)) <= valueDiff) {
                return true;
            }

            if (buckets.containsKey(bucketId + 1) && 
                Math.abs(num - buckets.get(bucketId + 1)) <= valueDiff) {
                return true;
            }

            buckets.put(bucketId, num);

            // Maintain sliding window using sequenced collection operations
            if (i >= indexDiff) {
                var oldBucketId = getBucketId(nums[i - indexDiff], bucketSize);
                buckets.remove(oldBucketId);
            }
        }

        return false;
    }

    /**
     * Calculates bucket ID for a given number.
     * Handles negative numbers correctly.
     */
    private long getBucketId(long num, long bucketSize) {
        return num >= 0 
            ? num / bucketSize 
            : ((num + 1) / bucketSize) - 1;
    }

    /**
     * Test case record using modern Java records (JDK 16+).
     */
    private record TestCase(
        int[] nums,
        int indexDiff,
        int valueDiff,
        boolean expected,
        String description
    ) {
        // Compact constructor with validation
        TestCase {
            Objects.requireNonNull(nums, "Array cannot be null");
            Objects.requireNonNull(description, "Description cannot be null");
            if (indexDiff < 0) {
                throw new IllegalArgumentException("indexDiff must be non-negative");
            }
        }
    }
}
