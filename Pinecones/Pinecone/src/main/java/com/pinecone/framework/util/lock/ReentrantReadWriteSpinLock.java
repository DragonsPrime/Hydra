package com.pinecone.framework.util.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;

public class ReentrantReadWriteSpinLock implements ReadWriteLock {
    private final AtomicInteger               mutexSignal = new AtomicInteger( 0 );
    private static final int              WRITE_LOCK_MASK = 1 << 16;

    private Thread                          writingThread = null;
    private int                       writeReentrantCount = 0;

    private final ThreadLocal<Integer> readReentrantCount = ThreadLocal.withInitial(() -> 0);

    private final Lock                           readLock = new ReadLock();
    private final Lock                          writeLock = new WriteLock();

    @Override
    public Lock readLock() {
        return this.readLock;
    }

    @Override
    public Lock writeLock() {
        return this.writeLock;
    }

    private class ReadLock implements Lock {
        @Override
        public void lock() {
            while ( !this.tryLock() ) {

            }
        }

        @Override
        public boolean tryLock() {
            int currentState = mutexSignal.get();

            if ( ( currentState & WRITE_LOCK_MASK ) != 0 && writingThread != Thread.currentThread() ) {
                return false;
            }

            if ( mutexSignal.compareAndSet( currentState, currentState + 1 ) ) {
                readReentrantCount.set( readReentrantCount.get() + 1 );
                return true;
            }
            return false;
        }

        @Override
        public void lockInterruptibly() throws InterruptedException {
            while ( !this.tryLock() ) {
                if ( Thread.interrupted() ) {
                    throw new InterruptedException( "Thread was interrupted while attempting to acquire read lock." );
                }
            }
        }

        @Override
        public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
            long deadline = System.nanoTime() + unit.toNanos(time);
            while ( !this.tryLock() ) {
                if ( Thread.interrupted() ) {
                    throw new InterruptedException( "Thread was interrupted while attempting to acquire read lock." );
                }
                if ( System.nanoTime() > deadline ) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public void unlock() {
            if (readReentrantCount.get() <= 0) {
                throw new IllegalMonitorStateException( "Read lock not held by current thread." );
            }

            readReentrantCount.set( readReentrantCount.get() - 1 );
            mutexSignal.decrementAndGet();
        }

        @Override
        public Condition newCondition() {
            throw new UnsupportedOperationException();
        }
    }

    private class WriteLock implements Lock {
        @Override
        public void lock() {
            Thread currentThread = Thread.currentThread();

            if ( writingThread == currentThread ) {
                ++writeReentrantCount;
                return;
            }

            while ( true ) {
                int currentState = mutexSignal.get();

                if ( currentState == 0 ) {
                    if ( mutexSignal.compareAndSet( 0, WRITE_LOCK_MASK ) ) {
                        writingThread = currentThread;
                        writeReentrantCount = 1;
                        break;
                    }
                }
            }
        }

        @Override
        public boolean tryLock() {
            Thread currentThread = Thread.currentThread();

            if ( writingThread == currentThread ) {
                writeReentrantCount++;
                return true;
            }

            if ( mutexSignal.compareAndSet(0, WRITE_LOCK_MASK) ) {
                writingThread = currentThread;
                writeReentrantCount = 1;
                return true;
            }
            return false;
        }

        @Override
        public void lockInterruptibly() throws InterruptedException {
            while ( !this.tryLock() ) {
                if ( Thread.interrupted() ) {
                    throw new InterruptedException("Thread was interrupted while attempting to acquire write lock");
                }
            }
        }

        @Override
        public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
            long deadline = System.nanoTime() + unit.toNanos(time);
            while ( !this.tryLock() ) {
                if ( Thread.interrupted() ) {
                    throw new InterruptedException("Thread was interrupted while attempting to acquire write lock");
                }
                if ( System.nanoTime() > deadline ) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public void unlock() {
            if ( writingThread != Thread.currentThread() ) {
                throw new IllegalMonitorStateException("Write lock not held by current thread");
            }

            if ( --writeReentrantCount == 0 ) {
                writingThread = null;
                mutexSignal.set(0);
            }
        }

        @Override
        public Condition newCondition() {
            throw new UnsupportedOperationException();
        }
    }

}

