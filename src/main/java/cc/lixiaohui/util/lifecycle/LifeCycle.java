package cc.lixiaohui.util.lifecycle;

/**
 * 简单生命周期定义
 * @author lixiaohui
 *
 */
public interface LifeCycle {
	void init() throws LifeCycleException;
	void start() throws LifeCycleException;
	void restart() throws LifeCycleException;
	void destroy() throws LifeCycleException;
}
