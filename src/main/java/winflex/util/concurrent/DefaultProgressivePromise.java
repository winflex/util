package winflex.util.concurrent;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 *
 * @author winflex
 */
public class DefaultProgressivePromise<V> extends DefaultPromise<V> implements
		IProgressivePromise<V> {

	private static final long serialVersionUID = -9185531061543050609L;

	private static final Logger logger = LoggerFactory
			.getLogger(DefaultProgressivePromise.class);

	public DefaultProgressivePromise() {
	}

	public DefaultProgressivePromise(Executor executor) {
		super(executor);
	}

	@Override
	public IProgressivePromise<V> setProgress(long progress, long total) {
		if (progress < 0 || total < 0 || progress > total) {
			throw new IllegalArgumentException("progress: " + progress
					+ " (expected: 0 <= progress <= total (" + total + "))");
		}

		if (isDone()) {
			return this;
		}

		notifyProgressiveListeners(progress, total);
		return this;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void notifyProgressiveListeners(long progress, long total) {
		Map<IFutureListener<? extends IFuture<V>>, Executor> listeners;
		synchronized (this) {
			listeners = new HashMap<>(listeners());
		}

		for (Entry<IFutureListener<? extends IFuture<V>>, Executor> entry : listeners
				.entrySet()) {
			IFutureListener<? extends IFuture<V>> l = entry.getKey();
			if (l instanceof IProgressiveFutureListener) {
				notifyProgressiveListener((IProgressiveFutureListener) l,
						entry.getValue(), progress, total);
			}
		}
	}

	private void notifyProgressiveListener(
			IProgressiveFutureListener<IProgressiveFuture<V>> l, Executor e,
			long progress, long total) {
		if (e == SynchronousExecutor.INSTANCE) {
			try {
				l.operationProgressed(this, progress, total);
			} catch (Exception t) {
				logger.error(t.getMessage(), t);
			}
		} else {
			e.execute(new Runnable() {

				@Override
				public void run() {
					try {
						l.operationProgressed(DefaultProgressivePromise.this,
								progress, total);
					} catch (Exception t) {
						logger.error(t.getMessage(), t);
					}
				}
			});
		}
	}

	@Override
	public IProgressivePromise<V> await() throws InterruptedException {
		super.await();
		return this;
	}

	@Override
	public IProgressivePromise<V> awaitUninterruptibly() {
		super.awaitUninterruptibly();
		return this;
	}

	@Override
	public IProgressivePromise<V> addListener(
			IFutureListener<? extends IFuture<V>> listener) {
		super.addListener(listener);
		return this;
	}

	@Override
	public IProgressivePromise<V> addListener(
			IFutureListener<? extends IFuture<V>> listener, Executor executor) {
		super.addListener(listener, executor);
		return this;
	}

	@Override
	public IProgressivePromise<V> removeListener(
			IFutureListener<? extends IFuture<V>> listener) {

		super.removeListener(listener);
		return this;
	}

	@Override
	public IProgressivePromise<V> setAttachment(String name, Object value) {
		super.setAttachment(name, value);
		return this;
	}
}
