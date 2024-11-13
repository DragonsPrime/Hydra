package com.pinecone.hydra.storage.volume.entity.local.striped;

import com.pinecone.framework.util.Debug;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.ExportStorageObject;
import com.pinecone.hydra.storage.volume.entity.LogicVolume;
import com.pinecone.hydra.storage.volume.entity.local.striped.export.StripedChannelExport;
import com.pinecone.hydra.storage.volume.runtime.MasterVolumeGram;
import com.pinecone.hydra.storage.volume.runtime.VolumeJobCompromiseException;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class TitanStripChannelBufferWriteJob implements StripChannelBufferWriteJob {
    protected VolumeManager                 volumeManager;

    protected ExportStorageObject           object;
    protected int                           jobNum;
    protected int                           jobCode;
    protected LogicVolume                   volume;
    protected FileChannel                   channel;
    protected AtomicInteger                 currentCacheBlockNumber;
    protected final Object                  pipelineLock;
    protected List<Object>                  lockGroup;
    protected StripLockEntity               lockEntity;

    protected BufferWriteStatus             status;

    protected StripExportFlyweightEntity    flyweightEntity;
    protected List< CacheBlock >            cacheBlockGroup;
    protected LocalStripedTaskThread        parentThread;


    public TitanStripChannelBufferWriteJob( StripedChannelExport stripedChannelExport, StripExportFlyweightEntity flyweightEntity, LogicVolume volume ){
        this.lockEntity                 = flyweightEntity.getLockEntity();
        this.object                     = stripedChannelExport.getExportStorageObject();
        this.jobNum                     = flyweightEntity.getJobNum();
        this.jobCode                    = flyweightEntity.getJobCode();
        this.volumeManager              = stripedChannelExport.getVolumeManager();
        this.volume                     = volume;
        this.channel                    = stripedChannelExport.getFileChannel();
        this.currentCacheBlockNumber    = lockEntity.getCurrentBufferCode();
        this.pipelineLock               = lockEntity.getLockObject();
        this.lockGroup                  = lockEntity.getLockGroup();
        this.flyweightEntity            = flyweightEntity;

        this.setWritingStatus();
    }

    public void applyTaskThread( LocalStripedTaskThread taskThread ) {
        this.parentThread = taskThread;
    }

    @Override
    public BufferWriteStatus getStatus() {
        return this.status;
    }

    protected void setWritingStatus() {
        this.status = BufferWriteStatus.Writing;
    }

    protected void setSuspendedStatus() {
        this.status = BufferWriteStatus.Suspended;
    }

    protected void setMasteringStatus() {
        this.status = BufferWriteStatus.Mastering;
    }

    protected void setExitingStatus() {
        this.status = BufferWriteStatus.Exiting;
    }


    protected ReentrantLock reentrantLock = new ReentrantLock();


    @Override
    public void execute() throws VolumeJobCompromiseException {
        long size = this.object.getSize().longValue();
        long stripSize = this.volumeManager.getConfig().getDefaultStripSize().longValue();
        long currentPosition    = 0;


        MasterVolumeGram parentProcess = (MasterVolumeGram)this.parentThread.parentExecutum();
        while ( true ){
//            parentProcess.getMajorStatusIO().lock();
//            try{
                if( this.cacheBlockGroup.get( currentCacheBlockNumber.get()).getStatus() == CacheBlockStatus.free ){
                    long bufferSize = stripSize;
                    if( currentPosition >= size ){
                        this.setExitingStatus();
                        break;
                    }
                    if( currentPosition + bufferSize > size ){
                        bufferSize = size - currentPosition;
                    }
                    try {
                        this.volume.channelRaid0Export( this.object, this.channel, this.cacheBlockGroup.get( currentCacheBlockNumber.get() ), currentPosition, bufferSize, this.flyweightEntity);
                        currentPosition += bufferSize;
                        //唤醒文件线程
                        this.lockEntity.unlockBufferToFileLock();
                        /**
                         * 切换缓存块
                         */
                        this.currentCacheBlockNumber.addAndGet(this.jobNum);
                        if( this.currentCacheBlockNumber.get() > cacheBlockGroup.size() - 1 ){
                            this.currentCacheBlockNumber.getAndSet( jobCode );
                        }
                        if( this.cacheBlockGroup.get( this.currentCacheBlockNumber.get() ).getStatus() == CacheBlockStatus.full ){
                            try {
                                ((Semaphore) this.lockEntity.getLockObject()).acquire();
                            }
                            catch ( InterruptedException e ){
                                Thread.currentThread().interrupt();
                                e.printStackTrace();
                            }

                        }



//                while (true) {
//                    // 使用 compareAndSet 实现安全增量
//                    int currentCount = counter.get();
//                    if (currentCount < 2 && counter.compareAndSet(currentCount, currentCount + 1)) {
//                        if (counter.get() == 2) {
//                            if (isLast) {
//                            int temporaryCurrentPosition = 0;
//                            int totalSize = flyweightEntity.getTerminalStateRecordGroup().stream()
//                                    .mapToInt(stateRecord -> stateRecord.getValidByteEnd().intValue() - stateRecord.getValidByteStart().intValue())
//                                    .sum();
//                            byte[] temporaryBuffer = new byte[totalSize];
//                            for (TerminalStateRecord stateRecord : flyweightEntity.getTerminalStateRecordGroup()) {
//                                int length = stateRecord.getValidByteEnd().intValue() - stateRecord.getValidByteStart().intValue();
//                                ByteBuffer buffer = ByteBuffer.wrap(this.buffers.get(this.currentBufferCode.get()), stateRecord.getValidByteStart().intValue(), length);
//                                buffer.get(temporaryBuffer, temporaryCurrentPosition, length);
//                                temporaryCurrentPosition += length;
//                            }
//                            System.arraycopy( temporaryBuffer, 0, this.buffers.get(this.currentBufferCode.get()), 0, totalSize );
//                            flyweightEntity.setBufferToFileSize( totalSize );
//                        }
//
//                            counter.set(0);  // 重置 counter
//
//                            // 更新 buffer code 安全递增和重置
//                            if (lockEntity.getCurrentBufferCode().incrementAndGet() == 2) {
//                                lockEntity.getCurrentBufferCode().set(0);
//                            }
//
//                            lockEntity.unlockPipeStage();
//                            break;
//                        }
//                    }
//                    else {
//                        synchronized (lockEntity.getLockObject()) {
//                            try {
//                                lockEntity.getLockObject().wait();
//                                break;
//                            }
//                            catch (InterruptedException e) {
//                                Thread.currentThread().interrupt();  // 重设线程的中断状态
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                }




//                // 监工形态
//                boolean bNeedUnlock = true;
//                lockEntity.getMaoLock().lock();
//                try{
//                counter.incrementAndGet();
//
//                if( counter.incrementAndGet() == 2 ){
//                    counter.getAndSet( 0 );
//                    lockEntity.getCurrentBufferCode().incrementAndGet();
//                    if ( lockEntity.getCurrentBufferCode().get() == 2 ){
//                        lockEntity.getCurrentBufferCode().getAndSet( 0 );
//                    }
//
//                    Debug.traceSyn( "miao", Thread.currentThread().getName() );
//                    lockEntity.unlockPipeStage();
//                }
//                else {
//                    synchronized ( lockEntity.getLockObject() ){
//                        Debug.traceSyn( "shit" );
//                        try{
//                            lockEntity.getMaoLock().unlock();
//                            bNeedUnlock = false;
//                            lockEntity.getLockObject().wait();
//                        }
//                        catch ( InterruptedException e ) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }
//            finally {
//                if( bNeedUnlock ) {
//                    lockEntity.getMaoLock().unlock();
//                }
//            }


                        this.setWritingStatus();

                    }
                    catch ( IOException | SQLException e ) {
                        throw new VolumeJobCompromiseException( e );
                    }
                }
            }
//            finally {
//                parentProcess.getMajorStatusIO().unlock();
//            }
//        }
        Debug.trace("我是线程"+jobCode+"我已经完成任务");
    }

}
