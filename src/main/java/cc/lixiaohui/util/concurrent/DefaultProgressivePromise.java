package cc.lixiaohui.util.concurrent;

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
public class DefaultProgressivePromise extends DefaultPromise implements
        IProgressivePromise {

    private static final long serialVersionUID = -9185531061543050609L;
    
    private static final Logger logger = LoggerFactory.getLogger(DefaultProgressivePromise.class);

    public DefaultProgressivePromise() {
    }

    public DefaultProgressivePromise(Executor executor) {
        super(executor);
    }

    @Override
    public IProgressivePromise setProgress(long progress, long total) {
        if (progress < 0 || total < 0 || progress > total) {
            throw new IllegalArgumentException(
                    "progress: " + progress + " (expected: 0 <= progress <= total (" + total + "))");
        }
        
        if (isDone()) {
            return this;
        }
        
        notifyProgressiveListeners(progress, total);
        return this;
    }

    private void notifyProgressiveListeners(long progress, long total) {
        Map<IFutureListener, Executor> listeners;
        synchronized (this) {
            listeners = new HashMap<>(listeners());
        }
        
        for (Entry<IFutureListener, Executor> entry: listeners.entrySet()) {
            IFutureListener l = entry.getKey();
            if (l instanceof IProgressiveFutureListener) {
                notifyProgressListener((IProgressiveFutureListener) l, entry.getValue(), progress, total);
            }
        }
    }
    
    private void notifyProgressListener(IProgressiveFutureListener l, Executor e, long progress, long total) {
        if (e instanceof SynchronousExecutor) {
            try {
                l.operationProgressived(this, progress, total);
            } catch (Exception t) {
                logger.error(t.getMessage(), t);
            }
        } else {
            e.execute(new Runnable() {
                
                @Override
                public void run() {
                    try {
                        l.operationProgressived(DefaultProgressivePromise.this, progress, total);
                    } catch (Exception t) {
                        logger.error(t.getMessage(), t);
                    }
                }
            });
        }
    }
}
