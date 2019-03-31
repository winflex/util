package winflex.util.concurrent;

/**
 *
 *
 * @author winflex
 */
public interface IProgressiveFutureListener<F extends IProgressiveFuture<?>> extends IFutureListener<F> {
    
    void operationProgressed(F future, long progress, long total) throws Exception;
}
