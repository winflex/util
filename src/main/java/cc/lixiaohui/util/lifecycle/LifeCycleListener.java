package cc.lixiaohui.util.lifecycle;

import java.util.EventListener;

public interface LifeCycleListener extends EventListener {
	void lifeCycleEvent(LifeCycleEvent e);
}
