package com.pinecone.hydra.storage;

import java.util.concurrent.locks.ReentrantLock;

public class TemporaryLock {
    public static ReentrantLock reentrantLock = new ReentrantLock();

}
