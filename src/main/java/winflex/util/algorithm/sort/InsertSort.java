package winflex.util.algorithm.sort;

/**
 * 插入排序
 * 
 * O(n^2)
 * 
 * @author lixiaohui
 * @date 2017年2月22日 上午11:12:25
 */
public class InsertSort {
	
	public static final void sort(int[] array, int left, int right) {
		if (left > right) {
			throw new IllegalArgumentException("left > right");
		}
		
		if (array == null || (right == left)) {
			return;
		}
		
		for (int i = left + 1; i <= right; i++) {
			int temp = array[i];
			int j = i;
			while (j > left && temp < array[j - 1]) {
				array[j] = array[j - 1];
				--j;
			}
			array[j] = temp;
		}
		
	}
	
	public static void main(String[] args) {
        int[] array = new int[] { 1, 2, 3, -1, 2, 6, 1, 1, 3, 5, 4 };
        sort(array, 0, array.length - 1);
        for (int i = 0; i < array.length;i++) {
            System.out.print(array[i] + " ");
        }
    }
}
