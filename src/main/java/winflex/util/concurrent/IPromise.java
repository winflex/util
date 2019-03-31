package winflex.util.concurrent;

import java.util.concurrent.Executor;

/**
 * A writeable {@link IFuture}
 * 
 * @author winflex
 */
public interface IPromise<V> extends IFuture<V> {
    
    IPromise<V> setSuccess(Object result);
    
    IPromise<V> setFailure(Throwable cause);
    
    @Override
    IPromise<V> addListener(IFutureListener<? extends IFuture<V>> listener);
    
    @Override
    IPromise<V> addListener(IFutureListener<? extends IFuture<V>> listener, Executor executor);

    @Override
    IPromise<V> removeListener(IFutureListener<? extends IFuture<V>> listener);

    @Override
    IPromise<V> await() throws InterruptedException;

    @Override
    IPromise<V> awaitUninterruptibly();
    
    @Override
    IPromise<V> setAttachment(String name, Object value);
}
