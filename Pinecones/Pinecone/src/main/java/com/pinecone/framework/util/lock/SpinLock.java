package com.pinecone.framework.util.lock;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class SpinLock implements Lock {
    private final AtomicBoolean mMutexSignal = new AtomicBoolean( false );

    @Override
    public void lock() {
        while ( !this.mMutexSignal.compareAndSet(false, true) ) {

        }
    }

    @Override
    public void unlock() {
        this.mMutexSignal.set(false);
    }

    @Override
    public boolean tryLock() {
        return this.mMutexSignal.compareAndSet( false, true );
    }

    @Override
    public Condition newCondition() {
        throw new UnsupportedOperationException( "SpinLock does not support conditions." );
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        while ( !this.mMutexSignal.compareAndSet( false, true ) ) {
            if (Thread.currentThread().isInterrupted()) {
                throw new InterruptedException();
            }
        }
    }

    @Override
    public boolean tryLock( long time, java.util.concurrent.TimeUnit unit ) throws InterruptedException {
        long endTime = System.nanoTime() + unit.toNanos( time );
        while ( !this.mMutexSignal.compareAndSet(false, true) ) {
            if ( System.nanoTime() > endTime ) {
                return false;
            }
            if ( Thread.currentThread().isInterrupted() ) {
                throw new InterruptedException();
            }
        }
        return true;
    }
}
