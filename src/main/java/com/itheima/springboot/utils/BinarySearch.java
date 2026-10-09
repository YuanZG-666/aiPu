package com.itheima.springboot.utils;

/**
 * 对升序排列的 int 数组执行二分查找。
 */
public final class BinarySearch {

    private BinarySearch() {
        // 工具类不需要创建实例。
    }

    /**
     * 查找目标值在升序数组中的索引。
     *
     * @param numbers 已按升序排列的数组
     * @param target  要查找的值
     * @return 找到时返回一个匹配元素的索引；未找到时返回 -1
     */
    public static int search(int[] numbers, int target) {
        if (numbers == null) {
            return -1;
        }

        int left = 0;
        int right = numbers.length - 1;

        while (left <= right) {
            // 这种写法可以避免 (left + right) 可能产生的整数溢出。
            int middle = left + (right - left) / 2;

            if (numbers[middle] == target) {
                return middle;
            }
            if (numbers[middle] < target) {
                left = middle + 1;
            } else {
                right = middle - 1;
            }
        }

        return -1;
    }
}
