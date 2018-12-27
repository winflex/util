package winflex.util.concurrent;

import java.util.concurrent.Executor;

/**
 *
 *
 * @author winflex
 */
public interface IProgressiveFuture extends IFuture {
    
    @Override
    IFuture addListener(IFutureListener listener);
    
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
     * <pre>
     * @see IProgressiveFutureListener
     */
    @Override
    public IFuture addListener(IFutureListener listener, Executor executor);
}
