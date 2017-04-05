package cc.lixiaohui.util.attr;

/**
 *
 * @author lixiaohui
 * @date 2017年3月28日 下午4:11:31
 * @version 1.0
 */
public interface Attribute<T> {
    
    AttributeKey<T> key();

    T get();

    void set(T value);
    
    T getAndSet(T value);

    T setIfAbsent(T value);

    T getAndRemove();

    boolean compareAndSet(T oldValue, T newValue);

    void remove();
    
    boolean hasValue();
}
