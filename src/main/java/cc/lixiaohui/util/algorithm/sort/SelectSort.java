package cc.lixiaohui.util.algorithm.sort;

/**
 * 选择排序
 * 
 * O(n^2)
 * 
 * @author lixiaohui
 * @date 2017年2月22日 下午12:37:32
 */
public class SelectSort {

	public static final void sort(int[] array, int left, int right) {
		if (left > right) {
			throw new IllegalArgumentException("left > right");
		}
		
		if (array == null || (right == left)) {
			return;
		}
		
		for (int i = left; i <= right; i++) {
			for (int j = i + 1; j <= right; j++) {
				if (array[j] < array[i]) {
					int temp = array[i];
					array[i] = array[j];
					array[j] = temp;
				}
			}
		}
	}

	public static void main(String[] args) {
		int[] array = new int[] { 1, 2, 3, -1, 2, 6, 1, 1, 3, 5, 4 };
		sort(array, 0, array.length - 1);
		for (int i = 0; i < array.length; i++) {
			System.out.print(array[i] + " ");
		}
	}
}
