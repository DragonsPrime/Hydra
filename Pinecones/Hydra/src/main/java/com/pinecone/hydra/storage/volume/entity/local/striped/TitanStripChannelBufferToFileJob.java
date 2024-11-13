package com.pinecone.hydra.storage.volume.entity.local.striped;

import com.pinecone.framework.util.Debug;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.runtime.VolumeJobCompromiseException;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class TitanStripChannelBufferToFileJob implements StripChannelBufferToFileJob {
    private VolumeManager       volumeManager;
    private FileChannel         channel;
    private StripLockEntity     lockEntity;
    private int                 jobNum;
    private BufferToFileStatus  status;
    private List< CacheBlock >  cacheBlocksGroup;
    private AtomicInteger       currentPosition;

    public TitanStripChannelBufferToFileJob( VolumeManager volumeManager, FileChannel channel, StripExportFlyweightEntity flyweightEntity, List< CacheBlock > cacheBlockGroup ){
        this.volumeManager     = volumeManager;
        this.channel           = channel;
        this.lockEntity        = flyweightEntity.getLockEntity();
        this.jobNum            = flyweightEntity.getJobNum();
        this.currentPosition.getAndSet( 0 );
        this.cacheBlocksGroup  = cacheBlockGroup;
    }

    protected void setWritingStatus() {
        this.status = BufferToFileStatus.Writing;
    }
    protected void setSuspendedStatus() {
        this.status = BufferToFileStatus.Suspended;
    }

    protected void setExitingStatus() {
        this.status = BufferToFileStatus.Exiting;
    }
    @Override
    public void execute() throws VolumeJobCompromiseException {
        while( true ){
            try{
                Debug.trace("摸鱼罗");
                this.setSuspendedStatus();
                ((Semaphore) this.lockEntity.getLockObject()).acquire();
            }
            catch ( InterruptedException e ) {
                Thread.currentThread().interrupt();
            }
            while ( this.checkNextStep() ){
                this.setWritingStatus();
                for( int i = 0; i < jobNum; i++  ){
                    CacheBlock cacheBlock = this.cacheBlocksGroup.get(currentPosition.get());
                    byte[] buffer = cacheBlock.getCache();
                    ByteBuffer writeBuffer = ByteBuffer.wrap( buffer, cacheBlock.getValidByteStart().intValue(), cacheBlock.getValidByteEnd().intValue() );
                    try {
                        channel.write(writeBuffer);
                    }
                    catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    Arrays.fill(buffer, (byte) 0);
                    currentPosition.incrementAndGet();
                }
                this.setSuspendedStatus();
            }
            //唤醒所有缓存线程
            this.lockEntity.unlockPipeStage();
        }

        //Debug.warnSyn( "wangwang" );
    }
    private boolean checkNextStep(){
        for( int i = 0; i < jobNum; i++ ){
            CacheBlock cacheBlock = cacheBlocksGroup.get(i);
            if( cacheBlock.getStatus() != CacheBlockStatus.full ){
                return false;
            }
        }
        return true;
    }

    private void lock(){
//        synchronized ( this.lockEntity.getLockObject() ){
            try{
                Debug.trace("摸鱼罗");
                this.setSuspendedStatus();
                ((Semaphore) this.lockEntity.getLockObject()).acquire();
            }
            catch ( InterruptedException e ) {
                Thread.currentThread().interrupt();
            }
//        }
    }
}
