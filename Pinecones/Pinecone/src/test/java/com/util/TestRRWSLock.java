package com.util;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.pinecone.Pinecone;
import com.pinecone.framework.util.Debug;
import com.pinecone.framework.util.lock.ReentrantReadWriteSpinLock;
import com.pinecone.framework.util.lock.ReentrantSpinLock;
import com.pinecone.framework.util.lock.SpinLock;

public class TestRRWSLock {
    private static final ReentrantReadWriteSpinLock lock = new ReentrantReadWriteSpinLock();
    //private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    //private static final ReentrantLock cl = new ReentrantLock();
    //private static final SpinLock cl = new SpinLock();
    private static final ReentrantSpinLock cl = new ReentrantSpinLock();

    private static void readOperation(int threadId) {
        lock.readLock().lock();
        try {
            Debug.trace( "Thread " + threadId + " is reading..." );
            try {
                Thread.sleep(100);
            }
            catch ( InterruptedException e ) {
                e.printStackTrace();
            }
        }
        finally {
            lock.readLock().unlock();
            Debug.trace( "Thread " + threadId + " finished reading." );
        }
    }

    private static void writeOperation(int threadId) {
        lock.writeLock().lock();
        try {
            Debug.trace( "Thread " + threadId + " is writing..." );
            try {
                Thread.sleep(200);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        finally {
            lock.writeLock().unlock();
            Debug.trace( "Thread " + threadId + " finished writing." );
        }
    }

    private static void testReentrancy() {
        lock.writeLock().lock();
        try {
            Debug.trace( "Main thread started writing." );
            lock.writeLock().lock();
            try {
                Debug.trace( "Main thread re-entered writing." );
            }
            finally {
                lock.writeLock().unlock();
            }
        }
        finally {
            lock.writeLock().unlock();
            Debug.trace( "Main thread finished writing." );
        }
    }

    private static void testSimple() {
        ExecutorService executorService = Executors.newFixedThreadPool( 4 );

        for ( int i = 1; i <= 3; ++i ) {
            final int threadId = i;
            executorService.submit(() -> readOperation(threadId));
        }

        for ( int i = 1; i <= 3; ++i ) {
            final int threadId = i;
            executorService.submit(() -> writeOperation(threadId));
        }

        executorService.submit(() -> testReentrancy());

        executorService.shutdown();
    }


    private static final Map<Integer, Integer> map = new TreeMap<>();

    private static int cnt = 0;

    private static void treeReadOperation() {
        for ( int i = 0; i < 1e6; i++ ) {
            lock.readLock().lock();
            //cl.lock();
            try {
                //Debug.trace( map.get(i) );
                map.get(i);
            }
            finally {
                //cl.unlock();
                lock.readLock().unlock();
            }
        }

        ++cnt;
    }

    private static void treeWriteOperation() {
        for ( int i = 0; i < 1e6; i++ ) {
            lock.writeLock().lock();
            //cl.lock();
            try {
                map.put(i, i);
            }
            finally {
                //cl.unlock();
                lock.writeLock().unlock();
            }
        }

        ++cnt;
    }

    private static void testUnit() {
        Thread rt = new Thread( TestRRWSLock::treeReadOperation );
        Thread wt = new Thread( TestRRWSLock::treeWriteOperation );

        rt.start();
        wt.start();

        while ( cnt < 2 ) {
            Debug.sleep(1);
        }

    }

    public static void main( String[] args ) throws Exception {
        Pinecone.init( (Object...cfg )->{

            TestRRWSLock.testUnit();

            return 0;
        }, (Object[]) args );
    }
}
