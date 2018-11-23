package cc.lixiaohui.util.concurrent;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 提交任务时, 若未到达最大线程数, 则优先创建新线程来处理任务, 若已到达最大线程数, 则加入队列(而非像
 * {@link ThreadPoolExecutor}那样先提交到队列, 等队列满了再创建新的线程)
 * 
 *
 */
public class EagerThreadPoolExecutor extends ThreadPoolExecutor {

    /**
     * 已提交的任务数(包括未执行完成的和还在队列中等待被执行的任务)
     */
    private final AtomicInteger submittedTaskCount = new AtomicInteger(0);

    public EagerThreadPoolExecutor(int corePoolSize, int maximumPoolSize,
            long keepAliveTime, TimeUnit unit, TaskQueue<Runnable> workQueue,
            ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
                threadFactory, handler);
    }

    public int getSubmittedTaskCount() {
        return submittedTaskCount.get();
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        submittedTaskCount.decrementAndGet();
    }

    @Override
    public void execute(Runnable command) {
        submittedTaskCount.incrementAndGet();
        try {
            super.execute(command);
        } catch (RejectedExecutionException rx) {
            // 实际上队列可能并没有满, 尝试加入队列
            final TaskQueue<Runnable> queue = (TaskQueue<Runnable>) super.getQueue();
            if (!queue.offer(command)) {
                submittedTaskCount.decrementAndGet();
                getRejectedExecutionHandler().rejectedExecution(command,
                        EagerThreadPoolExecutor.this);
            }
        } catch (Throwable t) {
            // decrease any way
            submittedTaskCount.decrementAndGet();
        }
    }
}