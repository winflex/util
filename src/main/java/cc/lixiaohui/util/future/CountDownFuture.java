package cc.lixiaohui.util.future;


/**
 * 类似于{@link CountDownLatch}, 但{@link CountDownFuture}可以添加完成监听器
 *
 *
 * @author lixiaohui
 *
 */
public final class CountDownFuture extends DefaultFuture<Object> {
    
    private volatile int count;
    
    public CountDownFuture(int count) {
        this.count = count;
        if (count == 0) {
            setSuccess(null);
        }
    }
    
    public void countDown() {
        countDown(1);
    }
    
    public void countDown(int n) {
        if (isDone()) {
            return;
        }
        
        synchronized (this) {
            
            if (isDone()) {
                return;
            }
            
            final int count = this.count = this.count - n;
            
            if (count == 0) {
                setSuccess(null);
            }
        }
    }
}