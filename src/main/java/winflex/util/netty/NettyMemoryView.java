package winflex.util.netty;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicLong;

import io.netty.util.internal.PlatformDependent;

public class NettyMemoryView {

    private static long maxDirectMemory;
    private static AtomicLong usedDirectMemory;

    static {
        try {
            Field maxField = PlatformDependent.class
                    .getDeclaredField("DIRECT_MEMORY_LIMIT");
            maxField.setAccessible(true);
            maxDirectMemory = maxField.getLong(null);

            Field usedField = PlatformDependent.class
                    .getDeclaredField("DIRECT_MEMORY_COUNTER");
            usedField.setAccessible(true);
            usedDirectMemory = (AtomicLong) usedField.get(null);
        } catch (Throwable e) {
        	e.printStackTrace();
            maxDirectMemory = -1;
            usedDirectMemory = new AtomicLong(-1);
        }
    }

    public static long getMaxDirectMemory() {
        return maxDirectMemory;
    }

    public static long getUsedDirectMemory() {
        return usedDirectMemory.get();
    }
}
