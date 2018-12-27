package winflex.util.algorithm.sort;

import java.util.Arrays;

/**
 * 计数排序
 * 
 * O(n)
 * 
 * @author lixiaohui
 * @date 2017年2月22日 下午1:45:16
 */
public class CountSort {
	
	public static final void sort(int[] array, int left, int right) {
		if (left > right) {
			throw new IllegalArgumentException("left > right");
		}
		
		if (array == null || (right == left)) {
			return;
		}
		
		int max = array[left];
		int min = array[left];
		// find the max and min value
		for (int i = left + 1; i <= right; i++) {
			if (array[i] > max) {
				max = array[i];
			}
			if (array[i] < min) {
				min = array[i];
			}
		}
		int range = max - min + 1;
		int[] temp = new int[range];
		Arrays.fill(temp, 0);
		for (int i = left; i <= right; i++) {
			temp[array[i] - min]++;
		}
		int k = 0;
		for (int i = 0; i < temp.length; i++) {
			for (int j = 0; j < temp[i]; j++) {
				array[left + k] = i + min;
				k++;
			}
		}
	}
	
	public static void main(String[] args) {
        int[] array = new int[] { 1, 2, 3, 2, -2, -1, 6, 1, 1, 3, 5, 4 };
        sort(array, 0, array.length - 1);
        for (int i = 0; i < array.length;i++) {
            System.out.print(array[i] + " ");
        }
    }
}
