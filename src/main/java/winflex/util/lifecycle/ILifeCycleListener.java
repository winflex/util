package winflex.util.lifecycle;

import java.util.EventListener;

/**
 * 
 * 
 * @author winflex
 */
public interface ILifeCycleListener extends EventListener {
	
	void lifeCycleEvent(LifeCycleEvent e);
}
