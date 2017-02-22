package cc.lixiaohui.util.ds.tree;


/**
 * 二叉树
 * 
 * @author lixiaohui
 * @date 2016年10月27日 下午1:39:35
 * 
 * @param <T> 节点data类型参数
 */
public interface BinaryTree<T> {

	/**
	 * @return 返回根节点
	 */
	Node<T> root();
	
	/**
	 * 设置新的根节点
	 * @param newRoot 新根节点
	 * @return 旧根节点 or null
	 */
	Node<T> root(Node<T> newRoot);
	
	/**
	 * @return 返回总节点个数
	 */
	int nodeCount();
	
	/**
	 * 返回第level层的节点个数
	 * @param level 层次
	 * @return 返回第level层的节点个数
	 */
	int nodesAt(int level);
	
	/**
	 * @return 叶子节点数
	 */
	int leafCount();
	
	/**
	 * @return 树的深度
	 */
	int depth();
	
	/**
	 * 插入节点
	 * @param value 新节点
	 */
	void insert(T value);
	
	/**
	 * 删除节点
	 * @param value 要删除的节点
	 */
	boolean delete(T value);
	
	/**
	 * @return 是否是完全二叉树
	 */
	boolean isComplete();
	
	/**
	 * 遍历树
	 * @param policy 遍历策略
	 * @param handler 节点处理器
	 */
	void traverse(TraversePolicy policy, NodeHandler<T> handler);
	
	/**
	 * 遍历树生成value数组
	 * @param policy 遍历策略
	 * @return value数组
	 */
	T[] valueAsArray(TraversePolicy policy);
	
	/**
	 * 遍历树生成Node<T>数组
	 * @param policy 遍历策略
	 * @return Node<T>数组
	 */
	Node<T>[] nodeAsArray(TraversePolicy policy);
	
	String toString(TraversePolicy policy);
	
	/**
	 * 树的遍历策略
	 *
	 */
	public enum TraversePolicy {
		/**
		 * 前序遍历
		 */
		PRE_ORDER,
		
		/**
		 * 中序遍历
		 */
		IN_ORDER,
		
		/**
		 * 后序遍历
		 */
		POST_ORDER,
		
		/**
		 * 层次遍历, 从上往下, 从左至右
		 */
		Level_UP_DOWN_LEFT_RIGHT 
	}
	
	/**
	 * 节点
	 * 
	 * @param <T> 节点value类型参数
	 */
	public interface Node<T> {
		
		/**
		 * @return 绑定的数据
		 */
		T value();
		
		/**
		 * @return 父节点
		 */
		Node<T> parent();
		
		/**
		 * 设置父节点
		 * @param parent 新的父节点
		 * @return 旧的父节点
		 */
		Node<T> parent(Node<T> parent);
		
		/**
		 * @return 左孩子节点
		 */
		Node<T> left();
		
		/**
		 * @return 右孩子节点
		 */
		Node<T> right();
		
		/**
		 * 设置左孩子节点
		 * @param leftChild
		 * @return 旧的左孩子节点
		 */
		Node<T> left(Node<T> leftChild);
		
		/**
		 * 设置右孩子节点
		 * @param rightChild
		 * @return 旧的右孩子节点
		 */
		Node<T> right(Node<T> rightChild);
		
		/**
		 * 删除左孩子节点
		 * @return 旧的左孩子节点
		 */
		Node<T> deleteLeft();
		
		/**
		 * 删除右孩子节点
		 * @return 旧的右孩子节点
		 */
		Node<T> deleteRight();
		
		/**
		 * @return 是否有左节点
		 */
		boolean hasLeft();
		
		/**
		 * @return 是否有右节点
		 */
		boolean hasRight();
		
		/**
		 * 将当前节点从父节点中删除
		 * @return
		 */
		boolean cut();
		
		/**
		 * 是否是叶子结点
		 * @return
		 */
		boolean isLeaf();
	}
	
	/**
	 * 节点处理器
	 * @param <N> 节点value类型参数
	 */
	public interface NodeHandler<N> {
		void handle(Node<N> node);
	}
	
}
