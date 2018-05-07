/*
 * Copyright(C) 2013 Agree Corporation. All rights reserved.
 * 
 * Contributors:
 *     Agree Corporation - initial API and implementation
 */
package cc.lixiaohui.util.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 *
 *
 * @author lixiaohui
 * @date 2018年4月27日 下午2:32:51
 * @version 1.0
 *
 */
public class SpinLock implements Lock {

    private final AtomicReference<Thread> owner = new AtomicReference<Thread>();
    
    private int holdCount;
    
    @Override
    public void lock() {
        final AtomicReference<Thread> owner = this.owner;

        final Thread current = Thread.currentThread();
        if (owner.get() == current) {
            ++holdCount;
            return;
        }
        
        while (!owner.compareAndSet(null, current)) {
        }

        holdCount = 1;
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        final AtomicReference<Thread> owner = this.owner;
        
        final Thread current = Thread.currentThread();
        if (owner.get() == current) {
            ++holdCount;
            return;
        }
        
        while (!owner.compareAndSet(null, current)) {
            if (current.isInterrupted()) {
                current.interrupt();
                throw new InterruptedException();
            }
        }

        holdCount = 1;
    }

    @Override
    public boolean tryLock() {
        boolean locked =  owner.compareAndSet(null, Thread.currentThread());
        if (locked) {
            holdCount = 1;
        }
        return locked;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        final AtomicReference<Thread> owner = this.owner;
        final Thread current = Thread.currentThread();
        if (owner.get() == current) {
            ++holdCount;
            return true;
        }
        
        final long start = System.nanoTime();
        final long timeoutNanos = unit.toNanos(time);
        while (!owner.compareAndSet(null, current)) {
            if (current.isInterrupted()) {
                current.interrupt();
                throw new InterruptedException();
            }
            
            long elapsed = System.nanoTime() - start;
            if (elapsed >= timeoutNanos) {
                return false;
            }
        }

        holdCount = 1;
        return true;
    }

    @Override
    public void unlock() {
        final AtomicReference<Thread> owner = this.owner;
        final Thread current = Thread.currentThread();
        
        if (owner.get() != current) {
            throw new IllegalMonitorStateException();
        }
        
        if (--holdCount == 0) {
            owner.set(null);
        }
    }

    @Override
    public Condition newCondition() {
        throw new UnsupportedOperationException();
    }

    public static void main(String[] args) throws Throwable {
        final SpinLock lock = new SpinLock();
        lock.lock();
        try {
            Thread t = new Thread() {
                public void run() {
                    try {
                        System.out.println(lock.tryLock(1, TimeUnit.SECONDS));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                };
            };
            t.start();
            t.join();
        } finally {
            lock.unlock();
        }
    }
}
