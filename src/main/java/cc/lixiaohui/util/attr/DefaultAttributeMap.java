/*
 * Copyright(C) 2013 Agree Corporation. All rights reserved.
 * 
 * Contributors:
 *     Agree Corporation - initial API and implementation
 */
package cc.lixiaohui.util.attr;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 *
 *
 * @author lixiaohui
 * @date 2017年3月28日 下午4:22:46
 * @version 1.0
 *
 */
public class DefaultAttributeMap implements AttributeMap {

    private final Map<AttributeKey<?>, Attribute<?>> map = new IdentityHashMap<>(2);

    /*
     * @see
     * cn.com.agree.addal.util.attr.AttributeMap#attr(cn.com.agree.addal.util
     * .attr.AttributeKey)
     */
    @Override
    public <T> Attribute<T> attr(AttributeKey<T> key) {
        synchronized (map) {
            @SuppressWarnings("unchecked")
            Attribute<T> attr = (Attribute<T>) map.get(key);
            if (attr == null) {
                attr = new DefaultAttribute<T>(map, key);
                map.put(key, attr);
            }
            return attr;
        }
    }

    private static final class DefaultAttribute<T> extends AtomicReference<T> implements
            Attribute<T> {

        private static final long serialVersionUID = 454069034159097005L;

        private final Map<AttributeKey<?>, Attribute<?>> map;
        private final AttributeKey<T> key;

        DefaultAttribute(Map<AttributeKey<?>, Attribute<?>> map, AttributeKey<T> key) {
            this.map = map;
            this.key = key;
        }

        @Override
        public AttributeKey<T> key() {
            return key;
        }

        @Override
        public T setIfAbsent(T value) {
            while (!compareAndSet(null, value)) {
                T old = get();
                if (old != null) {
                    return old;
                }
            }
            return null;
        }
        
        /* 
         * @see cn.com.agree.addal.util.attr.Attribute#hasValue()
         */
        @Override
        public boolean hasValue() {
            return get() != null;
        }

        @Override
        public T getAndRemove() {
            T oldValue = getAndSet(null);
            remove0();
            return oldValue;
        }

        @Override
        public void remove() {
            set(null);
            remove0();
        }

        private void remove0() {
            synchronized (map) {
                map.remove(key);
            }
        }
    }

}
