package com.pinecone.framework.util.lock;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class ReentrantSpinLock implements Lock {
    private final AtomicBoolean mMutexSignal     = new AtomicBoolean( false );
    private Thread              mOwningThread    = null;
    private int                 mnReentrantCount = 0;

    @Override
    public void lock() {
        Thread currentThread = Thread.currentThread();

        if ( currentThread == this.mOwningThread ) {
            ++this.mnReentrantCount;
            return;
        }

        while ( !this.mMutexSignal.compareAndSet( false, true ) ) {

        }

        this.mOwningThread = currentThread;
        this.mnReentrantCount = 1;
    }

    @Override
    public void unlock() {
        Thread currentThread = Thread.currentThread();

        if ( currentThread != this.mOwningThread ) {
            return;
            //throw new IllegalMonitorStateException( "Calling thread has not locked this lock" );
        }

        --this.mnReentrantCount;

        if ( this.mnReentrantCount == 0 ) {
            this.mOwningThread = null;
            this.mMutexSignal.set( false );
        }
    }

    @Override
    public boolean tryLock() {
        Thread currentThread = Thread.currentThread();

        if ( currentThread == this.mOwningThread ) {
            ++this.mnReentrantCount;
            return true;
        }

        if ( this.mMutexSignal.compareAndSet( false, true ) ) {
            this.mOwningThread = currentThread;
            this.mnReentrantCount = 1;
            return true;
        }

        return false;
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        Thread currentThread = Thread.currentThread();

        if ( currentThread == this.mOwningThread ) {
            ++this.mnReentrantCount;
            return;
        }

        while ( !this.mMutexSignal.compareAndSet( false, true ) ) {
            if ( Thread.interrupted() ) {
                throw new InterruptedException();
            }
        }

        this.mOwningThread = currentThread;
        this.mnReentrantCount = 1;
    }

    @Override
    public boolean tryLock(long time, java.util.concurrent.TimeUnit unit) throws InterruptedException {
        long endTime = System.nanoTime() + unit.toNanos(time);
        Thread currentThread = Thread.currentThread();

        if ( currentThread == this.mOwningThread ) {
            this.mnReentrantCount++;
            return true;
        }

        while ( !this.mMutexSignal.compareAndSet( false, true ) ) {
            if ( System.nanoTime() > endTime ) {
                return false;
            }
            if ( Thread.interrupted() ) {
                throw new InterruptedException();
            }
        }

        this.mOwningThread = currentThread;
        this.mnReentrantCount = 1;
        return true;
    }

    @Override
    public Condition newCondition() {
        throw new UnsupportedOperationException( "ReentrantSpinLock does not support conditions." );
    }
}
