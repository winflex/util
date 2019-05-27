package winflex.util.lifecycle;

/**
 * 生命周期抽象
 * 
 * @author winflex
 */
public interface ILifeCycle {
	
	void init() throws LifeCycleException;
	
	void start() throws LifeCycleException;
	
	void destroy() throws LifeCycleException;
}
