package cc.lixiaohui.util.concurrent;

import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * An enhanced {@link Future} that supports callback and attachments
 *
 * @author winflex
 */
public interface IFuture extends Future<Object> {

    Object getNow();

    boolean isSuccessful();

    Throwable cause();

    IFuture addListener(IFutureListener listener);
    
    IFuture addListener(Executor executor, IFutureListener listener);

    IFuture removeListener(IFutureListener listener);

    IFuture await() throws InterruptedException;

    IFuture awaitUninterruptibly();

    boolean await(long timeout, TimeUnit unit) throws InterruptedException;

    boolean awaitUninterruptibly(long timeout, TimeUnit unit);
    
    Object getAttachment(String name);
    
    Map<String, Object> getAttachments();

    void setAttachment(String name, Object value);
}
