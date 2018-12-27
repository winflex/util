package cc.lixiaohui.util.concurrent;

import java.util.Objects;
import java.util.concurrent.Executor;

/**
 * 
 *
 * @author winflex
 */
public final class SynchronousExecutor implements Executor {

    public static final SynchronousExecutor INSTANCE = new SynchronousExecutor();
    
    private SynchronousExecutor() {
        // use static instance instead
    }
    
    @Override
    public void execute(Runnable command) {
        Objects.requireNonNull(command, "command").run();
    }
}
