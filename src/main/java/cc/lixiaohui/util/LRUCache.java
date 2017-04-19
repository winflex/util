/*
 * Copyright(C) 2013 Agree Corporation. All rights reserved.
 * 
 * Contributors:
 *     Agree Corporation - initial API and implementation
 */
package cc.lixiaohui.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 *
 *
 * @author lixiaohui
 * @date 2017年4月10日 下午5:22:35
 * @version 1.0
 *
 */
public class LRUCache<K, V> {

    private final ConcurrentMap<K, Entry> nodes;

    private int cacheSize;

    // youngest
    private Entry head;

    // oldest
    private Entry tail;

    public LRUCache(int cacheSize) {
        this.cacheSize = cacheSize;
        this.nodes = new ConcurrentHashMap<K, Entry>(cacheSize);
    }

    public void put(K key, V value) {
        Entry node = nodes.get(key);
        if (node == null) {
            node = new Entry();
            if (nodes.size() == cacheSize) { // full, evict eldest node
                nodes.remove(tail.key);
                removeTail();
            }
            nodes.put(key, node);
        }
    }

    Entry removeTail() {
        final Entry oldTail = tail;
        if (oldTail != null) {
            if (oldTail.before != null) {
                oldTail.before.after = null;
            } else {
                head = null;
            }
            tail = oldTail.before;
        }
        return oldTail;
    }

    public V get(K key) {
        Entry node = nodes.get(key);
        if (node == null) {
            return null;
        }
        moveToHead(node);
        return node.value;
    }

    private void moveToHead(Entry node) {
        if (node == head)
            return;
        if (node.before != null)
            node.before.after = node.after;
        if (node.after != null)
            node.after.before = node.before;
        if (tail == node)
            tail = node.before;
        if (head != null) {
            node.after = head;
            head.before = node;
        }
        head = node;
        node.before = null;
        if (tail == null)
            tail = head;
    }

    public void clear() {
        head = null;
        tail = null;
        nodes.clear();
    }

    private class Entry {
        Entry before;
        Entry after;
        K key;
        V value;
    }
}
