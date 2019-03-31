package winflex.util.concurrent;

import java.util.concurrent.Executor;


/**
 * 
 *
 * @author winflex
 */
public interface IProgressivePromise<V> extends IPromise<V>, IProgressiveFuture<V> {
    
    IProgressivePromise<V> setProgress(long progress, long total);
    
    @Override
    IProgressivePromise<V> addListener(IFutureListener<? extends IFuture<V>> listener);
    
    @Override
    IProgressivePromise<V> addListener(IFutureListener<? extends IFuture<V>> listener, Executor executor);
    
    @Override
    IProgressivePromise<V> removeListener(IFutureListener<? extends IFuture<V>> listener);

    @Override
    IProgressivePromise<V> await() throws InterruptedException;

    @Override
    IProgressivePromise<V> awaitUninterruptibly();

    @Override
    IProgressivePromise<V> setAttachment(String name, Object value);
}
