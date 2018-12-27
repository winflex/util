package winflex.util.attr;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author lixiaohui
 * @date 2017年3月28日 下午4:08:59
 * @version 1.0
 */
public class AttributeKey<T> implements Comparable<AttributeKey<T>>{
    
    private static final ConcurrentMap<String, Boolean> names = new ConcurrentHashMap<String, Boolean>();
    
    private static final AtomicInteger nextId = new AtomicInteger();
    
    private final int id;
    private final String name;
    
    public AttributeKey(String name) {
        if (names.putIfAbsent(name, Boolean.TRUE) != null) {
            throw new IllegalArgumentException(String.format("'%s' is already in use", name));
        }
        
        this.id = nextId.incrementAndGet();
        this.name = name;
    }
    
    public final String name() {
        return name;
    }

    public final int id() {
        return id;
    }

    @Override
    public final int hashCode() {
        return super.hashCode();
    }

    @Override
    public final boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int compareTo(AttributeKey<T> other) {
        if (this == other) {
            return 0;
        }

        int returnCode = name.compareTo(other.name);
        if (returnCode != 0) {
            return returnCode;
        }

        return ((Integer) id).compareTo(other.id);
    }

    @Override
    public String toString() {
        return name();
    }
    
}
