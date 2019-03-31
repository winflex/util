package winflex.util.concurrent;

import java.util.concurrent.Executor;

/**
 *
 *
 * @author winflex
 */
public interface IProgressiveFuture<V> extends IFuture<V> {

	@Override
	IProgressiveFuture<V> addListener(
			IFutureListener<? extends IFuture<V>> listener);

	/**
	 * <pre>
	 * f.addListener(new IProgressiveFutureListener() {
	 * 
	 *      public void operationCompleted(IFuture future) throws Exception {
	 *      }
	 *      
	 *      public void operationProgressived(IProgressiveFuture future, long progress, long total)
	 *              throws Exception {
	 *      }
	 * });
	 * 
	 * <pre>
	 * @see IProgressiveFutureListener
	 */
	@Override
	IProgressiveFuture<V> addListener(
			IFutureListener<? extends IFuture<V>> listener, Executor executor);

	@Override
	IProgressiveFuture<V> removeListener(
			IFutureListener<? extends IFuture<V>> listener);

	@Override
	IProgressiveFuture<V> await() throws InterruptedException;

	@Override
	IProgressiveFuture<V> awaitUninterruptibly();

	@Override
	IProgressiveFuture<V> setAttachment(String name, Object value);
}
