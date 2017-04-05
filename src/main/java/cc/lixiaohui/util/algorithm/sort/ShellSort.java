package cc.lixiaohui.util.algorithm.sort;

/**
 * 希尔排序
 * 
 * O(n) ~ O(n^2)
 * @author lixiaohui
 * @date 2017年2月22日 上午11:44:56
 */
public class ShellSort {
	
	public static final void sort(int[] array, int left, int right) {
		if (left > right) {
			throw new IllegalArgumentException("left > right");
		}
		
		if (array == null || (right == left)) {
			return;
		}
		
		int len = right - left + 1;
		for (int gap = len / 2; gap > 0; gap /= 2) {
			for (int i = left; i < left + gap; i += gap) {
				for (int j = i + gap; j <= right; j += gap) {
					int k = j;
					int temp = array[k];
					while (k > i && temp < array[k - gap]) {
						array[k] = array[k - gap];
						k -= gap;
					}
					array[k] = temp;
				}
			}
		}
		
	}
	
	public static void main(String[] args) {
		int len = 10000;
		int[] array = new int[len];
		for (int i = 0; i < len; i++) {
			array[i] = (int) (Math.random() * len);
		}
		sort(array, 0, array.length - 1);
		for (int i = 0; i < array.length; i++) {
			System.out.println(array[i]);
		}
	}
	
}
