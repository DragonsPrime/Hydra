package com.pinecone.hydra.storage.volume.entity.local.striped;

import com.pinecone.framework.system.prototype.Pinenut;

import java.util.concurrent.Semaphore;

public class BufferOutMate implements Pinenut {
    private Semaphore   bufferOutLock;
    private int bufferOutThreadId;

    public BufferOutMate() {
    }

    public BufferOutMate(Semaphore bufferOutLock, int bufferOutThreadId) {
        this.bufferOutLock = bufferOutLock;
        this.bufferOutThreadId = bufferOutThreadId;
    }


    public Semaphore getBufferOutLock() {
        return bufferOutLock;
    }


    public void setBufferOutLock(Semaphore bufferOutLock) {
        this.bufferOutLock = bufferOutLock;
    }


    public int getBufferOutThreadId() {
        return bufferOutThreadId;
    }


    public void setBufferOutThreadId(int bufferOutThreadId) {
        this.bufferOutThreadId = bufferOutThreadId;
    }

    public String toString() {
        return "bufferToFileMate{bufferToFileLock = " + bufferOutLock + ", bufferToFileThreadId = " + bufferOutThreadId + "}";
    }
}
