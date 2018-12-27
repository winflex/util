package winflex.util.concurrent;

/**
 * 
 *
 * @author winflex
 */
public interface IFutureListener {

    void operationCompleted(IFuture future) throws Exception;
}
