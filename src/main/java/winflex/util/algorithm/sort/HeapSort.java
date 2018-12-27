package winflex.util.algorithm.sort;

/**
 * 堆排序
 * 
 * O(nlog(n))
 * 
 * @author lixiaohui
 * @date 2017年2月22日 下午2:28:46
 */
public class HeapSort {
	
	public static final void sort(int[] array, int left, int right) {
		if (left > right) {
			throw new IllegalArgumentException("left > right");
		}
		
		if (array == null || (right == left)) {
			return;
		}
		
		int len = right - left + 1;
		for (int i = len / 2; i >= left; --i) {
			heapAdjust(array, i, right);
		}
		
		for (int i = right; i > left; i--) {
			int temp = array[i];
			array[i] = array[left];
			array[left] = temp;
			
			heapAdjust(array, left, i - 1);
			
		}
	}
	
	private static final void heapAdjust(int[] array, int parent, int right) {
		int temp = array[parent];
		int child = 2 * parent + 1;
		while (child <= right) {
			// 如果有右孩子结点，并且右孩子结点的值大于左孩子结点，则选取右孩子结点
			if (child + 1 <= right && array[child] < array[child + 1]) {
				++child;
			}
			
			// 如果父结点的值已经大于孩子结点的值，则直接结束
			if (temp > array[child]) {
				break;
			}
			// 把孩子结点的值赋给父结点
			array[parent] = array[child];
			parent = child;
			child = 2 * child + 1;
		}
		array[parent] = temp;
	}
	
	public static void main(String[] args) {
        int[] array = new int[] { 1, 3, 4, 5, 2, 6, 9, 7, 8, 0 };
        sort(array, 0, array.length - 1);
        for (int i = 0; i < array.length;i++) {
            System.out.print(array[i] + " ");
        }
    }
}
