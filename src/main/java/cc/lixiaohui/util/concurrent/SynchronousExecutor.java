package cc.lixiaohui.util.concurrent;

import java.util.concurrent.Executor;

/**
 * 
 *
 * @author winflex
 */
public final class SynchronousExecutor implements Executor {

    public static final SynchronousExecutor INSTANCE = new SynchronousExecutor();
    
    @Override
    public void execute(Runnable command) {
        command.run();
    }
}
