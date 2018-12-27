package winflex.util.algorithm.sort;

/**
 * 冒泡排序
 * 
 * O(n^2)
 * 
 * @author lixiaohui
 * @date 2017年2月22日 下午12:45:16
 */
public class BubbleSort {
    
	public static final void sort(int[] array, int left, int right) {
		if (left > right) {
			throw new IllegalArgumentException("left > right");
		}
		
		if (array == null || (right == left)) {
			return;
		}
		
		int len = right - left + 1;
		for (int i = 0; i < len; ++i) {
			for (int j = left; j < right - i; ++j) {
				if (array[j] > array[j + 1]) {
					int temp = array[j];
					array[j] = array[j + 1];
					array[j + 1] = temp;
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
