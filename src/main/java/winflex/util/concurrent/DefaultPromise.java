package winflex.util.concurrent;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 *
 * @author winflex
 */
public class DefaultPromise<V> implements IPromise<V>, Serializable {

	private static final long serialVersionUID = -6962917401801793701L;

	private static final Logger logger = LoggerFactory
			.getLogger(DefaultPromise.class);

	private static final Object SUCCESS_SIGNAL = new Object();

	private volatile Object result;

	/**
	 * Accessed only inside synchronized(this)
	 */
	private final Map<IFutureListener<? extends IFuture<V>>, Executor> listeners = new HashMap<>();
	private final ConcurrentMap<String, Object> attachments = new ConcurrentHashMap<>();

	/**
	 * The default executor to execute
	 * {@link IFutureListener#operationCompleted(IFuture)}
	 */
	private final Executor defaultExecutor;

	public DefaultPromise() {
		this(SynchronousExecutor.INSTANCE);
	}

	public DefaultPromise(Executor executor) {
		this.defaultExecutor = executor == null ? SynchronousExecutor.INSTANCE
				: executor;
	}

	@Override
	public V get() throws InterruptedException, ExecutionException {
		await();
		Throwable cause = cause();
		if (cause == null) {
			return (V) getNow();
		}
		throw new ExecutionException(cause);
	}

	@Override
	public V get(long timeout, TimeUnit unit) throws InterruptedException,
			ExecutionException, TimeoutException {
		if (!await(timeout, unit)) {
			throw new TimeoutException();
		}

		Throwable cause = cause();
		if (cause == null) {
			return (V) getNow();
		}

		throw new ExecutionException(cause);
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		if (isDone()) {
			return false;
		}

		synchronized (this) {
			if (isDone()) {
				return false;
			}
			result = new CauseHolder(new CancellationException());
			notifyAll();
		}
		notifyListeners();
		return true;
	}

	@Override
	public boolean isCancelled() {
		return result instanceof CauseHolder
				&& ((CauseHolder) result).cause instanceof CancellationException;
	}

	@Override
	public boolean isDone() {
		return result != null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public V getNow() {
		Object result = this.result;
		if (result == SUCCESS_SIGNAL || result instanceof CauseHolder) {
			return null;
		}
		return (V) result;
	}

	@Override
	public boolean isSuccessful() {
		Object result = this.result;
		if (result == null) {
			return false;
		}

		return !(result instanceof CauseHolder);
	}

	@Override
	public Throwable cause() {
		Object result = this.result;
		if (result instanceof CauseHolder) {
			return ((CauseHolder) result).cause;
		}
		return null;
	}

	@Override
	public IPromise<V> await() throws InterruptedException {
		return await0(true);
	}

	@Override
	public IPromise<V> awaitUninterruptibly() {
		try {
			return await0(false);
		} catch (InterruptedException e) {
			throw new Error(e);
		}
	}

	@Override
	public boolean await(long timeout, TimeUnit unit)
			throws InterruptedException {
		return await0(unit.toNanos(timeout), true);
	}

	@Override
	public boolean awaitUninterruptibly(long timeout, TimeUnit unit) {
		try {
			return await0(unit.toNanos(timeout), false);
		} catch (InterruptedException e) {
			throw new Error(e);
		}
	}

	@Override
	public IPromise<V> addListener(
			IFutureListener<? extends IFuture<V>> listener) {
		return addListener(listener, defaultExecutor);
	}

	@Override
	public IPromise<V> addListener(
			IFutureListener<? extends IFuture<V>> listener, Executor executor) {
		Objects.requireNonNull(listener, "listener");
		Objects.requireNonNull(executor, "executor");

		if (isDone()) {
			notifyListener(listener, executor);
			return this;
		}

		synchronized (this) {
			if (!isDone()) {
				listeners.put(listener, executor);
				return this;
			}
		}

		notifyListener(listener, executor);
		return this;
	}

	@Override
	public IPromise<V> removeListener(
			IFutureListener<? extends IFuture<V>> listener) {
		synchronized (this) {
			if (!isDone()) {
				listeners.remove(listener);
			}
		}
		return this;
	}

	@Override
	public Object getAttachment(String name) {
		return attachments.get(name);
	}

	@Override
	public Map<String, Object> getAttachments() {
		return attachments;
	}

	@Override
	public IPromise<V> setAttachment(String name, Object value) {
		attachments.put(name, value);
		return this;
	}

	@Override
	public IPromise<V> setSuccess(Object result) {
		if (setSuccess0(result)) {
			notifyListeners();
		}
		return this;
	}

	@Override
	public IPromise<V> setFailure(Throwable cause) {
		if (setFailure0(cause)) {
			notifyListeners();
		}

		return this;
	}

	private boolean setFailure0(Throwable cause) {
		if (isDone()) {
			return false;
		}

		synchronized (this) {
			if (isDone()) {
				return false;
			}

			result = new CauseHolder(cause);
			notifyAll();
		}
		return true;
	}

	private boolean setSuccess0(Object result) {
		if (isDone()) {
			return false;
		}

		synchronized (this) {
			if (isDone()) {
				return false;
			}

			if (result == null) {
				this.result = SUCCESS_SIGNAL;
			} else {
				this.result = result;
			}
			notifyAll();
		}
		return true;
	}

	private IPromise<V> await0(boolean interruptable)
			throws InterruptedException {
		if (isDone()) {
			return this;
		}

		if (interruptable && Thread.interrupted()) {
			throw new InterruptedException("thread had been interrupted");
		}

		boolean interrupted = false;
		synchronized (this) {
			while (!isDone()) {
				try {
					wait();
				} catch (InterruptedException e) {
					if (interruptable) {
						throw e;
					} else {
						interrupted = true;
					}
				}
			}
		}

		if (interrupted) {
			Thread.currentThread().interrupt();
		}
		return this;
	}

	private boolean await0(long timeoutNanos, boolean interruptable)
			throws InterruptedException {
		if (isDone()) {
			return true;
		}

		if (timeoutNanos <= 0) {
			return isDone();
		}

		if (interruptable && Thread.interrupted()) {
			throw new InterruptedException("thread had been interrupted");
		}

		long startTime = System.nanoTime();
		long waitTime = timeoutNanos;
		boolean interrupted = false;

		try {
			synchronized (this) {
				if (isDone()) {
					return true;
				}

				for (;;) {
					try {
						wait(waitTime / 1000000, (int) (waitTime % 1000000));
					} catch (InterruptedException e) {
						if (interruptable) {
							throw e;
						} else {
							interrupted = true;
						}
					}

					if (isDone()) {
						return true;
					} else {
						waitTime = timeoutNanos
								- (System.nanoTime() - startTime);
						if (waitTime <= 0) {
							return isDone();
						}
					}
				}
			}
		} finally {
			if (interrupted) {
				Thread.currentThread().interrupt();
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void notifyListener(IFutureListener listener, Executor executor) {
		if (executor == SynchronousExecutor.INSTANCE) {
			// No need to new runnable instance
			try {
				listener.operationCompleted(this);
			} catch (Throwable t) {
				logger.error(t.getMessage(), t);
			}
		} else {
			executor.execute(new Runnable() {

				@Override
				public void run() {
					try {
						listener.operationCompleted(DefaultPromise.this);
					} catch (Throwable t) {
						logger.error(t.getMessage(), t);
					}
				}
			});
		}
	}

	private void notifyListeners() {
		Map<IFutureListener<? extends IFuture<V>>, Executor> copy;
		synchronized (this) {
			copy = new HashMap<>(listeners);
		}

		for (Entry<IFutureListener<? extends IFuture<V>>, Executor> entry : copy
				.entrySet()) {
			notifyListener(entry.getKey(), entry.getValue());
		}
	}

	protected final Executor defaultExecutor() {
		return this.defaultExecutor;
	}

	protected final Map<IFutureListener<? extends IFuture<V>>, Executor> listeners() {
		return this.listeners;
	}

	private static final class CauseHolder implements Serializable {

		private static final long serialVersionUID = 8712728693792003462L;

		final Throwable cause;

		private CauseHolder(Throwable cause) {
			this.cause = cause;
		}
	}
}
