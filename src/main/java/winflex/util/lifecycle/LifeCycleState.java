package winflex.util.lifecycle;

/**
 * 
 * @author winflex
 */
public enum LifeCycleState {

	NEW(0), // 初始前

	INITIALIZING(1), INITIALIZED(2), INITIALIZE_FAILED(3),

	STARTING(4), STARTED(5), START_FAILED(6),

	DESTROYING(7), DESTROYED(8), DESTROY_FAILED(9);

	private int age;

	private LifeCycleState(int age) {
		this.age = age;
	}

	public int getAge() {
		return age;
	}
}
