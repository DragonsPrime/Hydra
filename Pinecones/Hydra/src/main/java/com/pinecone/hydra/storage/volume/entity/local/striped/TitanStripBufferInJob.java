package com.pinecone.hydra.storage.volume.entity.local.striped;

import com.pinecone.framework.util.Debug;
import com.pinecone.hydra.storage.KChannel;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.StorageExportIORequest;
import com.pinecone.hydra.storage.volume.entity.LogicVolume;
import com.pinecone.hydra.storage.volume.entity.local.striped.export.StripedChannelExport;
import com.pinecone.hydra.storage.volume.runtime.MasterVolumeGram;
import com.pinecone.hydra.storage.volume.runtime.VolumeJobCompromiseException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;

public class TitanStripBufferInJob implements StripBufferInJob {
    protected VolumeManager                 volumeManager;

    protected StorageExportIORequest        object;
    protected int                           jobCount;
    protected int                           jobCode;
    protected LogicVolume                   volume;
    protected KChannel                      channel;
    protected AtomicInteger                 currentCacheBlockNumber;
    protected final Semaphore               blockerLatch;

    protected StripBufferStatus             status;

    protected List< CacheBlock >            cacheBlockGroup;
    protected LocalStripedTaskThread        parentThread;

    protected byte[]                        buffer;
    protected Lock                          majorStatusIO;
    protected MasterVolumeGram              masterVolumeGram;

    protected Number                        offset;
    protected Number                        endSize;

    public TitanStripBufferInJob(MasterVolumeGram masterVolumeGram, StripedChannelExport stripedChannelExport, LogicVolume volume, StorageExportIORequest object, int jobCode ){
        this.masterVolumeGram             = masterVolumeGram;
        this.object                       = object;
        this.jobCount                     = this.masterVolumeGram.getJobCount();
        this.jobCode                      = jobCode;
        this.volumeManager                = stripedChannelExport.getVolumeManager();
        this.volume                       = volume;
        this.channel                      = stripedChannelExport.getFileChannel();
        this.currentCacheBlockNumber      = new AtomicInteger( jobCode );
        this.blockerLatch                 = new Semaphore(0);
        this.buffer                       = masterVolumeGram.getBuffer();
        this.cacheBlockGroup              = masterVolumeGram.getCacheGroup();

        this.intoWritingStatus();
    }

    @Override
    public void applyThread( LocalStripedTaskThread taskThread ) {
        this.parentThread                 = taskThread;
         this.masterVolumeGram            = (MasterVolumeGram) this.parentThread.parentExecutum();
        this.majorStatusIO                = this.masterVolumeGram.getMajorStatusIO();
    }

    @Override
    public StripBufferStatus getStatus() {
        return this.status;
    }

    protected void intoWritingStatus() {
        this.status = BufferWriteStatus.Writing;
    }

    protected void intoSuspendedStatus() {
        this.status = BufferWriteStatus.Suspended;
    }

    protected void intoSynchronizationStatus() {
        this.status = BufferWriteStatus.Synchronization;
    }

    protected void intoExitingStatus() {
        this.status = BufferWriteStatus.Exiting;
    }


    @Override
    public void execute() throws VolumeJobCompromiseException {
        long size = this.object.getSize().longValue();
        long stripSize = this.volumeManager.getConfig().getDefaultStripSize().longValue();
        long currentPosition    = 0;


        MasterVolumeGram parentProcess = (MasterVolumeGram)this.parentThread.parentExecutum();
        while ( true ){
            if( this.cacheBlockGroup.get( currentCacheBlockNumber.get()).getStatus() == CacheBlockStatus.Free){
                long bufferSize = stripSize;
                if( currentPosition >= size ){
                    this.intoExitingStatus();
                    this.wakeUpBufferToFileThread();
                    break;
                }

                this.cacheBlockGroup.get( currentCacheBlockNumber.get()).setStatus( CacheBlockStatus.Writing );
                if( currentPosition + bufferSize > size ){
                    bufferSize = size - currentPosition;
                }

                try {
                    this.volume.channelExport( this.object, this.channel, this.cacheBlockGroup.get( currentCacheBlockNumber.get() ), currentPosition, bufferSize, this.buffer);

                    currentPosition += bufferSize;
                    this.wakeUpBufferToFileThread();

                    // 切换缓存块
                    this.intoSynchronizationStatus();
                    this.currentCacheBlockNumber.addAndGet(this.jobCount);
                    if( this.currentCacheBlockNumber.get() > cacheBlockGroup.size() - 1 ){
                        this.currentCacheBlockNumber.getAndSet( jobCode );
                    }
                    if( this.cacheBlockGroup.get( this.currentCacheBlockNumber.get() ).getStatus() == CacheBlockStatus.Full ){
                        try {
                            this.intoSuspendedStatus();
                            Debug.trace("线程"+this.parentThread.getName()+":"+"我摸鱼了，没得写了");
                             this.blockerLatch.acquire();
                        }
                        catch ( InterruptedException e ){
                            Thread.currentThread().interrupt();
                            e.printStackTrace();
                        }
                    }

                    this.intoWritingStatus();

                }
                catch ( IOException | SQLException e ) {
                    throw new VolumeJobCompromiseException( e );
                }
            }
            else {
                try {
                    this.intoSuspendedStatus();
                    Debug.trace("我摸鱼了，没得写了");
                    this.wakeUpBufferToFileThread();
                   this.blockerLatch.acquire();
                }
                catch ( InterruptedException e ){
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                }
            }
        }

        Debug.trace("我是线程" + jobCode + "我已经完成任务");
    }

    @Override
    public Semaphore getBlockerLatch() {
        return this.blockerLatch;
    }

    @Override
    public void setStatus(StripBufferStatus status) {
        this.status = status;
    }

    private void wakeUpBufferToFileThread(){
        this.majorStatusIO.lock();
        try {
            MasterVolumeGram masterVolumeGram = (MasterVolumeGram) this.parentThread.parentExecutum();
            LocalStripedTaskThread bufferToFileThread = masterVolumeGram.getChildThread( this.masterVolumeGram.getBufferOutThreadId() );
            if( bufferToFileThread.getJobStatus() == BufferOutStatus.Suspended ){
                Debug.trace("线程"+bufferToFileThread.getName()+"被唤醒");
                bufferToFileThread.setJobStatus( BufferOutStatus.Writing );
                this.masterVolumeGram.getBufferOutBlockerLatch().release();
            }
        }
        finally {
            this.majorStatusIO.unlock();
        }

    }
}
