package winflex.util.concurrent;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * An enhanced {@link Future} that supports callback and attachments
 *
 * @author winflex
 */
public interface IFuture<V> extends Future<V> {

	V getNow();

	@Override
	V get() throws InterruptedException, ExecutionException;

	@Override
	V get(long timeout, TimeUnit unit) throws InterruptedException,
			ExecutionException, TimeoutException;

	boolean isSuccessful();

	Throwable cause();

	IFuture<V> addListener(IFutureListener<? extends IFuture<V>> listener);

	IFuture<V> addListener(IFutureListener<? extends IFuture<V>> listener,
			Executor executor);

	IFuture<V> removeListener(IFutureListener<? extends IFuture<V>> listener);

	IFuture<V> await() throws InterruptedException;

	IFuture<V> awaitUninterruptibly();

	boolean await(long timeout, TimeUnit unit) throws InterruptedException;

	boolean awaitUninterruptibly(long timeout, TimeUnit unit);

	Object getAttachment(String name);

	Map<String, Object> getAttachments();

	IFuture<V> setAttachment(String name, Object value);
}
