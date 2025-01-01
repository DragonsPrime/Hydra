package com.pinecone.hydra.storage.volume.entity.local.striped;

import com.pinecone.framework.util.Debug;
import com.pinecone.hydra.storage.Chanface;
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

    protected Chanface                  stream;


    protected final Semaphore           blockerLatch;

    protected LocalStripedTaskThread    parentThread;

    protected Lock                      majorStatusIO;

    public TitanStripReceiveBufferInJob(MasterVolumeGram masterVolumeGram, int jobCode, Chanface stream, LogicVolume volume){
        this.masterVolumeGram         = masterVolumeGram;
        this.buffer                   = this.masterVolumeGram.getBuffer();
        this.jobCode                  = jobCode;
        this.cacheBlock               = this.masterVolumeGram.getCacheGroup().get( jobCode );
        this.status                   = ReceiveBufferInStatus.Suspended;
        this.stream                   = stream;
        this.blockerLatch             = new Semaphore(0);
        this.cacheBlock.setVolume( volume );
    }


    @Override
    public void execute() throws VolumeJobCompromiseException {
        while( true ){
            try {
                if( this.status == ReceiveBufferInStatus.Exiting ){
                    break;
                }
                if(  this.masterVolumeGram.getCurrentBufferInJobCode() == this.jobCode ){
                    Debug.trace("我是缓存线程我开始工作了");
                    this.status = ReceiveBufferInStatus.Writing;
                    this.cacheBlock.setStatus( CacheBlockStatus.Writing );
                    int start = this.cacheBlock.getByteStart().intValue();
                    int end   = this.cacheBlock.getByteEnd().intValue();
                    int length = end - start;
                    int read = this.stream.read(this.buffer, this.cacheBlock.getByteStart().intValue(), length);
                    this.cacheBlock.setValidByteStart( start );
                    this.cacheBlock.setValidByteEnd( start + read );

                    this.status = ReceiveBufferInStatus.Suspended;
                    this.cacheBlock.setStatus( CacheBlockStatus.Full );

                    LocalStripedTaskThread bufferOutThread = this.masterVolumeGram.getChildThread(this.masterVolumeGram.getBufferOutThreadId());
                    //检测缓存写出线程的状态为摸鱼状态则唤醒
                    if( bufferOutThread.getJobStatus() == ReceiveBufferOutStatus.Suspended ){
                        this.masterVolumeGram.getBufferOutBlockerLatch().release();
                    }
                    //如果下一个线程不在工作则唤醒
                    int nextJobCode = this.jobCode+1;
                    if( nextJobCode >= this.masterVolumeGram.getJobCount() ){
                        nextJobCode = 0;
                    }

                    CacheBlock nextCacheBlock = this.masterVolumeGram.getCacheGroup().get(nextJobCode);

                    LocalStripedTaskThread nextThread = this.masterVolumeGram.getChildThread(nextCacheBlock.getBufferWriteThreadId());
                    if( nextThread.getJobStatus() == ReceiveBufferInStatus.Suspended && nextJobCode != this.jobCode ){
                        nextThread.getBlockerLatch().release();
                    }
                }
                Debug.trace("我休息了");
                this.blockerLatch.acquire();
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }


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
