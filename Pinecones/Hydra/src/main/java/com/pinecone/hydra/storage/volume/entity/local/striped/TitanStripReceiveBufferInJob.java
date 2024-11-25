package com.pinecone.hydra.storage.volume.entity.local.striped;

import com.pinecone.hydra.storage.StorageReceiveIORequest;
import com.pinecone.hydra.storage.volume.entity.LogicVolume;
import com.pinecone.hydra.storage.volume.runtime.MasterVolumeGram;
import com.pinecone.hydra.storage.volume.runtime.VolumeJobCompromiseException;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;

public class TitanStripReceiveBufferInJob implements StripReceiveBufferInJob{
    protected MasterVolumeGram          masterVolumeGram;

    protected byte[]                    buffer;

    protected int                       jobCode;

    protected CacheBlock                cacheBlock;

    protected StripBufferStatus         status;

    protected InputStream               stream;


    protected final Semaphore           blockerLatch;

    protected LocalStripedTaskThread    parentThread;

    protected Lock                      majorStatusIO;

    public TitanStripReceiveBufferInJob(MasterVolumeGram masterVolumeGram, int jobCode, InputStream stream){
        this.masterVolumeGram         = masterVolumeGram;
        this.buffer                   = this.masterVolumeGram.getBuffer();
        this.jobCode                  = jobCode;
        this.cacheBlock               = this.masterVolumeGram.getCacheGroup().get( jobCode );
        this.status                   = ReceiveBufferInStatus.Suspended;
        this.stream                   = stream;
        this.blockerLatch             = new Semaphore(0);
    }


    @Override
    public void execute() throws VolumeJobCompromiseException, IOException, InterruptedException {
        while( true ){
            if( this.status == ReceiveBufferInStatus.Exiting ){
                break;
            }
            this.status = ReceiveBufferInStatus.Writing;
            int start = this.cacheBlock.getByteStart().intValue();
            int end   = this.cacheBlock.getByteEnd().intValue();
            int length = end - start;
            int read = this.stream.read(this.buffer, this.cacheBlock.getByteStart().intValue(), length);
            this.cacheBlock.setValidByteStart( start );
            this.cacheBlock.setValidByteEnd( start + read );

            this.status = ReceiveBufferInStatus.Suspended;

            LocalStripedTaskThread bufferOutThread = this.masterVolumeGram.getChildThread(this.masterVolumeGram.getBufferOutThreadId());
            //检测缓存写出线程的状态为摸鱼状态则唤醒
            if( bufferOutThread.getJobStatus() == ReceiveBufferOutStatus.Suspended ){
                this.masterVolumeGram.getBufferOutBlockerLatch().acquire();
            }
            //如果下一个线程不在工作则唤醒
            int nextJobCode = this.jobCode+1;
            if( nextJobCode >= this.masterVolumeGram.getJobCount() ){
                nextJobCode = 0;
            }
            CacheBlock nextCacheBlock = this.masterVolumeGram.getCacheGroup().get(nextJobCode);
            LocalStripedTaskThread nextThread = this.masterVolumeGram.getChildThread(nextCacheBlock.getBufferWriteThreadId());
            if( nextThread.getJobStatus() == ReceiveBufferInStatus.Suspended ){
                nextThread.getBlockerLatch().acquire();
            }

            this.blockerLatch.release();
        }

    }

    @Override
    public void applyThread(LocalStripedTaskThread thread) {
        this.parentThread  = thread;
        this.majorStatusIO = this.masterVolumeGram.getMajorStatusIO();
    }

    @Override
    public StripBufferStatus getStatus() {
        return this.status;
    }

    @Override
    public Semaphore getBlockerLatch() {
        return this.blockerLatch;
    }

    @Override
    public void setStatus(StripBufferStatus status) {
        this.status = status;
    }
}
