package winflex.util.lifecycle;

import static winflex.util.lifecycle.LifeCycleState.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link ILifeCycle} 的抽象实现
 * 
 * @author winflex
 */
public abstract class AbstractLifeCycle implements ILifeCycle {

	protected final String name;
	protected final boolean autoLogState;
	private final List<ILifeCycleListener> lifeCycleListeners = new ArrayList<ILifeCycleListener>();

	protected volatile LifeCycleState state = NEW;

	protected AbstractLifeCycle() {
		this(null);
	}

	protected AbstractLifeCycle(String name) {
		this(name, true);
	}

	protected AbstractLifeCycle(String name, boolean autoLogState) {
		if (name == null) {
			name = getClass().getSimpleName();
		}
		this.name = name;
		this.autoLogState = autoLogState;
		if (autoLogState) {
			addLifeCycleListener(new LifeCycleLogger());
		}
	}

	@Override
	public synchronized final void init() throws LifeCycleException {
		checkState(NEW);

		setState(INITIALIZING);
		try {
			initInternal();
		} catch (LifeCycleException e) {
			setState(INITIALIZE_FAILED);
			throw e;
		}
		setState(INITIALIZED);
	}

	protected void initInternal() throws LifeCycleException {
	};

	@Override
	public final void start() throws LifeCycleException {
		checkState(INITIALIZED);

		setState(STARTING);
		try {
			startInternal();
		} catch (LifeCycleException e) {
			setState(START_FAILED);
			throw e;
		}
		setState(STARTED);
	}

	protected void startInternal() throws LifeCycleException {
	};

	@Override
	public final void destroy() throws LifeCycleException {
		checkState(INITIALIZED, INITIALIZE_FAILED, STARTED, START_FAILED);

		setState(LifeCycleState.DESTROYING);
		try {
			destroyInternal();
		} catch (LifeCycleException e) {
			setState(LifeCycleState.DESTROY_FAILED);
			throw e;
		}
		setState(LifeCycleState.DESTROYED);
	}

	protected void destroyInternal() throws LifeCycleException {
	};

	private void checkState(LifeCycleState... expectedStates) throws LifeCycleException {
		if (Arrays.asList(expectedStates).contains(state)) {
			return;
		}
		StringBuilder buf = new StringBuilder(64);
		buf.append("expected state is ");
		for (int i = 0; i < expectedStates.length; i++) {
			if (i > 0) {
				buf.append(" | ");
			}
			buf.append(expectedStates[i]);
		}
		buf.append(", but actual state is ").append(state);
		throw new LifeCycleException(buf.toString());
	}

	private void setState(LifeCycleState state) {
		this.state = state;
		fireLifeCycleEvent(new LifeCycleEvent(this, this, state), false);
	}

	protected void fireLifeCycleEvent(final LifeCycleEvent e, boolean async) {
		if (!async) {
			fireLifeCycleEvent0(e);
		} else { // 异步通知
			new Thread(() -> fireLifeCycleEvent0(e)).start();
		}
	}

	private void fireLifeCycleEvent0(LifeCycleEvent e) {
		lifeCycleListeners.forEach((l) -> l.lifeCycleEvent(e));
	}

	@Override
	public final void addLifeCycleListener(ILifeCycleListener l) {
		lifeCycleListeners.add(l);
	}

	@Override
	public final void removeLifeCycleListener(ILifeCycleListener l) {
		lifeCycleListeners.remove(l);
	}

	private static final Logger lifeCycleLogger = LoggerFactory.getLogger(LifeCycleLogger.class);

	public class LifeCycleLogger implements ILifeCycleListener {

		@Override
		public void lifeCycleEvent(LifeCycleEvent e) {
			lifeCycleLogger.info(name + " " + e.getState().toString().toLowerCase());
		}
	}
}
