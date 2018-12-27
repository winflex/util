package winflex.util.algorithm.sort;

/**
 * 1. 分两半, 递归对左, 右半部分进行排序
 * 2. 整理排序后的数组(同时遍历两子数组, 不断取较小值到一个临时数组中, 遍历完后该临时数组便是有序的, 最后再把临时数组复制回原数组)
 * 
 * O(n*log(n))
 * 
 * @author lixiaohui
 * @date 2017年2月22日 上午11:30:57
 */
public class MergeSort {
	
	public static final void sort(int[] array, int left, int right) {
		if (left > right) {
			throw new IllegalArgumentException("left > right");
		}
		
		if (array == null || (right == left)) {
			return;
		}
		
		int[] temp = new int[right - left + 1];
		if (left < right) {
			int mid = (left + right) / 2;
			sort(array, left, mid);
			sort(array, mid + 1, right);
			merge(array, left, mid, right, temp);
		}
	}

	private static void merge(int[] array, int left, int mid, int right, int[] temp) {
		int i = left, j = mid + 1;
		int k = 0;
		while (i <= mid && j <= right) {
			// choose the smaller one
			temp[k++] = array[i] < array[j] ? array[i++] : array[j++];
		}
		while (i <= mid) {
			temp[k++] = array[i++];
		}
		while (j <= right) {
			temp[k++] = array[j++];
		}
		
		for (int t = 0; t < k; t++) {
			array[left + t] = temp[t];
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
