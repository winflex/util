package winflex.util.ds.tree;

import java.util.Comparator;

import winflex.util.Objects;

/**
 * 二叉排序树
 * @author lixiaohui
 * @date 2016年10月27日 下午4:42:29
 * 
 */
public class SortedBinaryTree<T> extends AbstractBinaryTree<T> {
	
	private Comparator<T> comparator;
	
	/**
	 * values[0]为根元素
	 * @param values
	 */
	public SortedBinaryTree(T[] values) {
		this(null, values);
	}
	
	/**
	 * values[0]为根元素
	 * @param values
	 * @param comparator 元素比较器
	 */
	public SortedBinaryTree(Comparator<T> comparator, T[] values) {
		this.comparator = comparator;
		if (comparator == null && !(values instanceof Comparable[])) {
			throw new IllegalArgumentException("A Comparator must be giving or the value must be instance of Comparable");
		}
		
		buildTree(values);
	}
	
	private void buildTree(T[] values) {
		for (T value : values) {
			insert(value);
		}
	}

	@Override
	public void insert(T value) {
		Node<T> newNode = NodeImpl.newInstance(value);
		if (root == null) { // 尚未有根
			root = newNode;
			return;
		}
		
		insert(root, newNode);
	}

	@Override
	public boolean delete(T value) {
		Node<T> targetNode = findNode(root, value);
		
		if (targetNode == null) {
			return false;
		}
		
		// walkthrough, 要删除的为P, P的父为F, P的直接前驱为S
		// 1.若节点P是叶子节点, 则直接删除
		// 2.若节点P只有左子树PL或右子树PR, 只需另PL或PR成为P的父节点的左子树即可
		// 3.若节点P左右子树都有, 有两种做法: 
		//  1)另P的左子树为F的左子树, 而P的右子树为S的右子树 
		//  2)另P的直接前驱替代P, 然后从二叉树中删去它的直接前驱
		
		// 叶子
		if (targetNode.isLeaf()) {
			targetNode.cut();
			return true;
		} 
		Node<T> parent = targetNode.parent();
		// 左右子树都有, 采用第一种算法
		if (targetNode.hasLeft() && targetNode.hasRight()) {
			Node<T> directPrecursor = findDirectPrecursor(targetNode);
			Node<T> targetLeftChild = targetNode.left();
			Node<T> targetRightChild = targetNode.right();
			// P的左子树为F的左子树
			targetLeftChild.parent(parent);
			parent.left(targetLeftChild);
			
			// P的右子树为S的右子树 
			targetRightChild.parent(directPrecursor);
			directPrecursor.right(targetRightChild);
			return true;
		}
		// 只有左子树或只有右子树
		Node<T> graftNode  = null;
		graftNode = targetNode.hasLeft() ? targetNode.left() : targetNode.right();
		// 把graftNode嫁接到targetNode的父节点上
		if (parent.left() == targetNode) {
			parent.left(graftNode);
		} else {
			parent.right(graftNode);
		}
		graftNode.parent(parent);
		return true;
	}

	// ------------ util methods -----------------
	
	// 找节点的直接前驱
	private Node<T> findDirectPrecursor(Node<T> targetNode) {
		Node<T> node = targetNode.right();
		while (node.right() != null) {
			node = node.right();
		}
		return node;
	}

	private Node<T> findNode(Node<T> root, T value) {
		if (root == null) {
			return null;
		}
		int cmpResult = compare(root.value(), value);
		if (cmpResult == 0) {
			return root;
		} else if (cmpResult > 0) {
			return findNode(root.left(), value);
		} else {
			return findNode(root.right(), value);
		}
	}

	private void insert(Node<T> parent, Node<T> node) {
		int compareResult = compare(parent.value(), node.value());
		if (compareResult == 0) { // 要插入的值等于当前父节点的值, 忽略
			return;
		} else if (compareResult > 0) { // 要插入的值大于当前父节点的值, 往右插
			if (parent.hasRight()) {
				insert(parent.right(), node);
			} else {
				parent.right(node);
				node.parent(parent);
			}
		} else { // 要插入的值小于当前父节点的值, 往左插
			if (parent.hasLeft()) {
				insert(parent.left(), node);
			} else {
				parent.left(node);
				node.parent(parent);
			}
		}
	}

	private void checkComparator() {
		Objects.requireNonNull(comparator, "comparator must not be null");
	}
	
	@SuppressWarnings("unchecked")
	private int compare(T value1, T value2) {
		int result;
		if (value1 instanceof Comparable) {
			result = ((Comparable<T>) value1).compareTo(value2);
		} else {
			checkComparator();
			result = comparator.compare(value1, value2);
		}
		return result;
	}
	
	
	// ---------- getters and setters ---------------

	public Comparator<T> getComparator() {
		return comparator;
	}

	public void setComparator(Comparator<T> comparator) {
		this.comparator = comparator;
	}
	

}
