package winflex.util.algorithm.sort;

/**
 * 快速排序:
 * 先从数列中取出一个数作为基准数,将大于等于基准数的数放到它的右边，小于等于基准数的数全放到它的左边
 * 接着分别递归地对左半边数组, 右半边数组做同样的调整
 * 
 * O(n*log(n))
 * 
 * @author lixiaohui
 * @date 2017年2月22日 上午10:54:41
 */
public class QuickSort {
	
	public static final void sort(int[] array, int left, int right) {
		if (left < right) {
			int i = left;
			int j = right;
			int pivot = array[i];
			while (i < j) {
				while (i < j && array[j] >= pivot) {
					--j;
				}
				if (array[j] < pivot) {
					array[i++] = array[j];
				}
				
				while (i < j && array[i] <= pivot) {
					++i;
				}
				if (array[i] > pivot) {
					array[j--] = array[i];
				}
			}
			array[i] = pivot;
			sort(array, left, i - 1);
			sort(array, i + 1, right);
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

