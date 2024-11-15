package com.pinecone.hydra.storage.volume.entity.local.striped;

import com.pinecone.framework.system.prototype.Pinenut;

import java.util.concurrent.Semaphore;

public class BufferToFileMate implements Pinenut {
    private Semaphore   bufferToFileLock;
    private int         bufferToFileThreadId;

    public BufferToFileMate() {
    }

    public BufferToFileMate(Semaphore bufferToFileLock, int bufferToFileThreadId) {
        this.bufferToFileLock = bufferToFileLock;
        this.bufferToFileThreadId = bufferToFileThreadId;
    }


    public Semaphore getBufferToFileLock() {
        return bufferToFileLock;
    }


    public void setBufferToFileLock(Semaphore bufferToFileLock) {
        this.bufferToFileLock = bufferToFileLock;
    }


    public int getBufferToFileThreadId() {
        return bufferToFileThreadId;
    }


    public void setBufferToFileThreadId(int bufferToFileThreadId) {
        this.bufferToFileThreadId = bufferToFileThreadId;
    }

    public String toString() {
        return "bufferToFileMate{bufferToFileLock = " + bufferToFileLock + ", bufferToFileThreadId = " + bufferToFileThreadId + "}";
    }
}
