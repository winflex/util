package winflex.util.ds.tree;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import winflex.util.GetAndResetable;

/**
 * @author lixiaohui
 * @date 2016年10月26日 下午6:07:40
 * 
 */
public abstract class AbstractBinaryTree<T> implements BinaryTree<T> {
	
	public static class NodeImpl<N> implements Node<N> {

		private N value;
		private Node<N> parent;
		private Node<N> left;
		private Node<N> right;
		
		public NodeImpl(N value) {
			this(value, null);
		}
		
		public NodeImpl(N value, Node<N> parent) {
			this.value = value;
			this.parent = parent;
		}
		
		@Override
		public N value() {
			return value;
		}

		@Override
		public Node<N> parent() {
			return parent;
		}
		
		@Override
		public Node<N> parent(Node<N> parent) {
			Node<N> old = this.parent;
			this.parent = parent;
			return old;
		}

		@Override
		public Node<N> left() {
			return left;
		}

		@Override
		public Node<N> right() {
			return right;
		}

		@Override
		public Node<N> left(Node<N> leftChild) {
			Node<N> old = left;
			left = leftChild;
			return old;
		}

		@Override
		public Node<N> right(Node<N> rightChild) {
			Node<N> old = right;
			right = rightChild;
			return old;
		}

		@Override
		public Node<N> deleteLeft() {
			return left(null);
		}

		@Override
		public Node<N> deleteRight() {
			return right(null);
		}
		
		@Override
		public boolean hasLeft() {
			return left != null;
		}
		
		@Override
		public boolean hasRight() {
			return right != null;
		}
		
		@Override
		public boolean isLeaf() {
			return !(hasLeft() || hasRight());
		}
		
		@Override
		public boolean cut() {
			// unattach parent -> this
			if (parent().left() == this) {
				parent().deleteLeft();
			} else if (parent().right() == this) {
				parent().deleteRight();
			} else {
				return false;
			}
			// unattach this -> parent
			parent(null);
			return true;
		}
		
		public static <K> Node<K> newInstance(K value) {
			return new NodeImpl<K>(value);
		}
		
		public static <K> Node<K> newInstance(K value, Node<K> parent) {
			return new NodeImpl<K>(value, parent);
		}

	}
	
	private static class ArrayTransformer<N> implements NodeHandler<N>, GetAndResetable<Node<N>[]> {

		private List<Node<N>> nodes = new ArrayList<Node<N>>();
		
		@Override
		public void reset() {
			nodes.clear();
		}

		@SuppressWarnings("unchecked")
		@Override
		public Node<N>[] get() {
			return (Node<N>[]) nodes.toArray();
		}

		@Override
		public void handle(Node<N> node) {
			nodes.add(node);
		}

		@Override
		public Node<N>[] getAndReset() {
			Node<N>[] array = get();
			reset();
			return array;
		}
		
	}
	
	public static class TraverseTracker<N> implements NodeHandler<N>, GetAndResetable<String> {

		private StringBuilder sb = new StringBuilder();
		
		@Override
		public void handle(Node<N> node) {
			sb.append(node.value()).append(',');
		}

		@Override
		public String get() {
			sb.deleteCharAt(sb.length() - 1);
			return sb.toString();
		}

		@Override
		public void reset() {
			sb.delete(0, sb.length());
		}

		@Override
		public String getAndReset() {
			String s = get();
			reset();
			return s;
		}
		
	}
	
	protected Node<T> root;
	
	private final ArrayTransformer<T> ARRAY_TRANSFORMER = new ArrayTransformer<T>();
	
	private final TraverseTracker<T> TRAVERSE_TRACKER = new TraverseTracker<T>();
	
	public AbstractBinaryTree(){}
	
	public AbstractBinaryTree(Node<T> root) {
		this.root = root;
	}
	
	@Override
	public Node<T> root() {
		return root;
	}
	
	@Override
	public Node<T> root(Node<T> newRoot) {
		Node<T> old = root;
		root = newRoot;
		return old;
	}

	@Override
	public int nodeCount() {
		return nodeCount(root);
	}
	
	@Override
	public int nodesAt(int level) {
		return nodesAt(root, level);
	}
	
	@Override
	public int leafCount() {
		return leafCount(root);
	}

	@Override
	public int depth() {
		return depth(root);
	}
	
	@Override
	public boolean isComplete() {
		return isComplete(root);
	}

	@Override
	public void traverse(TraversePolicy policy, NodeHandler<T> handler) {
		switch (policy) {
		case PRE_ORDER:
			preOrderTraverse(root, handler);
			break;
		case IN_ORDER:
			inOrderTraverse(root, handler);
			break;
		case POST_ORDER:
			postOrderTraverse(root, handler);
			break;
		case Level_UP_DOWN_LEFT_RIGHT:
			levelUpDownLeftRight(root, handler);
			break;
		default:
			break;
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public T[] valueAsArray(TraversePolicy policy) {
		List<T> values = new ArrayList<T>();
		for (Node<T> n : nodeAsArray(policy)) {
			values.add(n.value());
		}
		return (T[]) values.toArray();
	}
	
	@Override
	public Node<T>[] nodeAsArray(TraversePolicy policy) {
		traverse(policy, ARRAY_TRANSFORMER);
		return ARRAY_TRANSFORMER.getAndReset();
	}
	
	protected Node<T> newNode(T value) {
		return new NodeImpl<T>(value);
	}
	
	// ------------------- util methods -------------------------
	
	/**
	 * 前序遍历
	 * @param node
	 * @param handler
	 */
	private void preOrderTraverse(Node<T> node, NodeHandler<T> handler) {
		if (node == null) {
			return;
		}
		
		visit(node, handler);
		preOrderTraverse(node.left(), handler);
		preOrderTraverse(node.right(), handler);
	}
	
	/**
	 * 中序遍历
	 * @param node
	 * @param handler
	 */
	private void inOrderTraverse(Node<T> node, NodeHandler<T> handler) {
		if (node == null) {
			return;
		}
		
		inOrderTraverse(node.left(), handler);
		visit(node, handler);
		inOrderTraverse(node.right(), handler);
	}
	
	/**
	 * 后序遍历
	 * @param node
	 * @param handler
	 */
	private void postOrderTraverse(Node<T> node, NodeHandler<T> handler) {
		if (node == null) {
			return;
		}
		
		postOrderTraverse(node.left(), handler);
		postOrderTraverse(node.right(), handler);
		visit(node, handler);
	}
	
	/**
	 * 层次遍历, 从上到下, 从左到右
	 * @param node
	 * @param handler
	 */
	private void levelUpDownLeftRight(Node<T> node, NodeHandler<T> handler) {
		if (node == null) {
			return;
		}
		Queue<Node<T>> queue = new LinkedList<Node<T>>();
		queue.offer(node);
		while (!queue.isEmpty()) {
			Node<T> headNode = queue.poll();
			visit(headNode, handler);
			if (headNode.hasLeft()) {
				queue.offer(headNode.left());
			}
			if (headNode.hasRight()) {
				queue.offer(headNode.right());
			}
		}
	}
	
	private void visit(Node<T> node, NodeHandler<T> handler) {
		handler.handle(node);
	}
	
	private int nodeCount(Node<T> node) {
		if (node == null) {
			return 0;
		}
		return 1 + nodeCount(node.left()) + nodeCount(node.right());
	}
	
	/**
	 * 第k层节点等于左子树第k-1层节点 + 右子树第k-1层节点
	 * @param root
	 * @param level
	 * @return
	 */
	private int nodesAt(Node<T> root, int level) {
		if (root == null || level < 1) {
			return 0;
		}
		if (level == 1) {
			return 1;
		}
		return nodesAt(root.left(), level - 1) + nodesAt(root.right(), level - 1);
	}
	
	/**
	 * 叶子节点数
	 * @param node
	 * @return
	 */
	private int leafCount(Node<T> node) {
		if (node == null) {
			return 0;
		}
		
		if (!node.hasLeft() && !node.hasRight()) {
			return 1;
		}
		
		return leafCount(node.left()) + leafCount(node.right());
	}
	
	/**
	 * 判断是否完全二叉:
	 * 当遇到一个节点的左子树为空时，则该节点右子树必须为空，且后面遍历的节点左右子树都必须为空，否则不是完全二叉树。
	 * @param root
	 * @return
	 */
	private boolean isComplete(Node<T> root) {
		if (root == null) {
			return false;
		}
		if (root.hasLeft()) {
			return !root.hasRight();
		} else {
			if (root.hasRight()) {
				return root.left().left() == null && root.left().right() == null;
			} else {
				return isComplete(root.left()) && isComplete(root.right());
			}
		}
	}
	
	/**
	 * 深度
	 * max(depth(左子树) + depth(右子树)) + 1
	 * @param node
	 * @return
	 */
	private int depth(Node<T> node) {
		if (node == null) {
			return 0;
		}
		int leftDepth = depth(node.left());
		int rightDepth = depth(node.right());
		return 1 + (leftDepth > rightDepth ? leftDepth : rightDepth);
	}
	
	public String toString(TraversePolicy policy) {
		traverse(policy, TRAVERSE_TRACKER);
		return TRAVERSE_TRACKER.getAndReset();
	}
}
