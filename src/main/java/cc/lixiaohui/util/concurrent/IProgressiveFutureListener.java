package cc.lixiaohui.util.concurrent;

/**
 *
 *
 * @author winflex
 */
public interface IProgressiveFutureListener extends IFutureListener {
    
    void operationProgressived(IProgressiveFuture future, long progress, long total) throws Exception;
}
