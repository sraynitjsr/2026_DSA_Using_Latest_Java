import java.util.*;

class ContainsNearbyAlmostDuplicate {

    public void Start() {
        int[] nums1 = {1, 2, 3, 1};
        System.out.println(containsNearbyAlmostDuplicate(nums1, 3, 0));

        int[] nums2 = {1, 5, 9, 1, 5, 9};
        System.out.println(containsNearbyAlmostDuplicate(nums2, 2, 3));
    }

    public boolean containsNearbyAlmostDuplicate(int[] nums, int indexDiff, int valueDiff) {
        if (valueDiff < 0) return false;

        Map<Long, Long> buckets = new HashMap<>();
        long size = (long) valueDiff + 1;

        for (int i = 0; i < nums.length; i++) {
            long num = nums[i];
            long id = getBucketId(num, size);

            if (buckets.containsKey(id)) return true;

            if (buckets.containsKey(id - 1) && Math.abs(num - buckets.get(id - 1)) <= valueDiff)
                return true;

            if (buckets.containsKey(id + 1) && Math.abs(num - buckets.get(id + 1)) <= valueDiff)
                return true;

            buckets.put(id, num);

            if (i >= indexDiff) {
                long oldId = getBucketId(nums[i - indexDiff], size);
                buckets.remove(oldId);
            }
        }

        return false;
    }

    private long getBucketId(long num, long size) {
        if (num >= 0) return num / size;
        return ((num + 1) / size) - 1;
    }
}
