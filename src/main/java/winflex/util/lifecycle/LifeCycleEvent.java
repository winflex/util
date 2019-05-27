package winflex.util.lifecycle;

import java.util.EventObject;

/**
 * 
 * 
 * @author winflex
 */
public class LifeCycleEvent extends EventObject {

	private static final long serialVersionUID = 6757600267753576331L;

	private AbstractLifeCycle lifeCycle;
	private LifeCycleState state;

	public LifeCycleEvent(Object source, AbstractLifeCycle lifeCycle, LifeCycleState state) {
		super(source);
		this.lifeCycle = lifeCycle;
		this.state = state;
	}

	public final AbstractLifeCycle getLifeCycle() {
		return lifeCycle;
	}

	public final void setLifeCycle(AbstractLifeCycle lifeCycle) {
		this.lifeCycle = lifeCycle;
	}

	public final LifeCycleState getState() {
		return state;
	}

	public final void setState(LifeCycleState state) {
		this.state = state;
	}
}
