package winflex.util.concurrent;


/**
 * 类似于{@link java.util.concurrent.CountDownLatch}, 但{@link CountDownFuture}可以添加完成监听器
 *
 *
 * @author lixiaohui
 *
 */
public final class CountDownFuture extends DefaultProgressivePromise<Integer> {
    
    private static final long serialVersionUID = 8184812105149735481L;
    
    private final int expectedCount;
    private volatile int count;
    
    public CountDownFuture(int count) {
    	this.expectedCount = count;
        this.count = count;
        if (count == 0) {
            setSuccess(null);
        }
    }
    
    public int getCount() {
        return this.count;
    }
    
    public void countDown() {
        countDown(1);
    }

	/**
     * if n > 0, then count = count - n
     * if n <= 0, then count is set to 0.
     */
    public void countDown(int n) {
        if (isDone()) {
            return;
        }
        
        synchronized (this) {
            if (isDone()) {
                return;
            }
            
            super.setProgress(expectedCount - count + n, expectedCount);
            if (n <= 0) {
                this.count = 0;
                super.setSuccess(null);
            } else {
                final int count = this.count - n;
                if (count > 0) {
                    this.count = count;
                } else {
                    this.count = 0;
                    super.setSuccess(null);
                }
            }
        }
    }
    
    @Override
    public IPromise<Integer> setSuccess(Object result) {
        throw new UnsupportedOperationException("Use countDown() instead");
    }
    
    @Override
    public IPromise<Integer> setFailure(Throwable cause) {
        throw new UnsupportedOperationException("Use countDown() instead");
    }
    
    @Override
    public IProgressivePromise<Integer> setProgress(long progress, long total) {
    	throw new UnsupportedOperationException("Use countDown() instead");
    }
}