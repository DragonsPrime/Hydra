package com.pinecone.hydra.storage.volume.entity.local.striped;

import com.pinecone.framework.util.Debug;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.runtime.MasterVolumeGram;
import com.pinecone.hydra.storage.volume.runtime.VolumeJobCompromiseException;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class TitanStripChannelBufferToFileJob implements StripChannelBufferToFileJob {
    protected VolumeManager             volumeManager;
    protected FileChannel               channel;
    protected StripLockEntity           lockEntity;
    protected int                       jobNum;
    protected StripBufferStatus         status;
    protected List< CacheBlock >        cacheBlocksGroup;
    protected AtomicInteger             currentPosition;
    protected LocalStripedTaskThread    parentThread;
    protected byte[]                    mBuffer;
    protected long                      totalSize;
    protected long                      exportSize;
    protected final Semaphore           pipelineLock;



    public TitanStripChannelBufferToFileJob( VolumeManager volumeManager, FileChannel channel, StripExportFlyweightEntity flyweightEntity, long totalSize){
        this.volumeManager     = volumeManager;
        this.channel           = channel;
        this.lockEntity        = flyweightEntity.getLockEntity();
        this.jobNum            = flyweightEntity.getJobNum();
        this.currentPosition   = new AtomicInteger(0);
        this.cacheBlocksGroup  = flyweightEntity.getCacheBlockGroup();
        this.mBuffer           = flyweightEntity.getBuffer();
        this.totalSize         = totalSize;
        pipelineLock           = (Semaphore) this.lockEntity.getLockObject();
    }

    @Override
    public void applyThread(LocalStripedTaskThread thread) {
        this.parentThread = thread;
    }

    @Override
    public StripBufferStatus getStatus() {
        return this.status;
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

            if( exportSize >= totalSize ){
                this.setExitingStatus();
                break;
            }
            if( !this.isAllExiting() ){
                try{
                    Debug.trace("摸鱼罗");
                    this.setSuspendedStatus();
                    ((Semaphore) this.lockEntity.getLockObject()).acquire();
                }
                catch ( InterruptedException e ) {
                    Thread.currentThread().interrupt();
                }
            }
            List<CacheBlock> writableCacheBlocks = this.getWritableCacheBlocks();
//            Debug.trace("准备干活");
            if (!writableCacheBlocks.isEmpty()){
                Debug.trace("执行写入");

                byte[] buffer = this.mergeArrays( writableCacheBlocks );
                ByteBuffer writeBuffer = ByteBuffer.wrap(buffer, 0, buffer.length ); //!!!!
                exportSize += buffer.length;


                try {
                    channel.write(writeBuffer);
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
                //Arrays.fill(this.mBuffer, (byte) 0);
                this.updateCurrentPosition( writableCacheBlocks.size() );


                this.setSuspendedStatus();
                //唤醒所有缓存线程
                //this.lockEntity.unlockPipeStage();
                for( int i = 0; i < jobNum; i++ ){
                    CacheBlock cacheBlock = this.cacheBlocksGroup.get(i);
                    MasterVolumeGram masterVolumeGram = (MasterVolumeGram) this.parentThread.parentExecutum();
                    LocalStripedTaskThread bufferWriteThread = masterVolumeGram.getChildThread(cacheBlock.getBufferWriteThreadId());
                    StripBufferStatus jobStatus = bufferWriteThread.getJobStatus();
                    if( jobStatus == BufferWriteStatus.Suspended ){
                        bufferWriteThread.setJobStatus( BufferWriteStatus.Writing );
                        Semaphore jobLock = bufferWriteThread.getJobLock();
                        Debug.trace("线程"+bufferWriteThread.getName()+"被唤醒");
                        jobLock.release();
                    }
                }
            }

        }

        //Debug.warnSyn( "wangwang" );
    }

    @Override
    public Semaphore getPipelineLock() {
        return this.pipelineLock;
    }

    @Override
    public void setStatus(StripBufferStatus status) {
        this.status = status;
    }

    private int getCacheLength(){
        int rounds = 0;
        int length = 0;
        for( int i = this.currentPosition.get(); i < this.cacheBlocksGroup.size(); i++ ){
            if( i == currentPosition.get() && rounds == 1 ){
                break;
            }

            CacheBlock cacheBlock = cacheBlocksGroup.get(i);
            if( cacheBlock.getStatus() != CacheBlockStatus.Full){
                return length;
            }
            length++;
            if( i == this.cacheBlocksGroup.size() - 1 ){
                rounds++;
                i = -1;
            }
        }
        return length;
    }

    //hold
    private byte[] mergeArrays( List< CacheBlock > writableCacheBlocks ){
        int totalLength = 0;
        for( CacheBlock cacheBlock : writableCacheBlocks ){
            totalLength += cacheBlock.getValidByteEnd().intValue() - cacheBlock.getValidByteStart().intValue();
        }
        long currentSize = 0;
        byte[] buffer = new byte[totalLength];
        for( CacheBlock cacheBlock : writableCacheBlocks ){
            int bufferSize = cacheBlock.getValidByteEnd().intValue() - cacheBlock.getValidByteStart().intValue();
            System.arraycopy( this.mBuffer, cacheBlock.getValidByteStart().intValue(),buffer,(int)currentSize, bufferSize  );
            cacheBlock.setStatus( CacheBlockStatus.Free );
            currentSize += bufferSize;

            //Debug.infoSyn(cacheBlock.getCacheBlockNumber());
        }
        return buffer;
    }

    private List< CacheBlock > getWritableCacheBlocks(){
        ArrayList<CacheBlock> cacheBlocks = new ArrayList<>();
        int rounds = 0;
        for( int i = this.currentPosition.get(); i < this.cacheBlocksGroup.size(); i++ ){
            if( i == currentPosition.get() && rounds == 1 ){
                break;
            }

            CacheBlock cacheBlock = cacheBlocksGroup.get(i);
            if( cacheBlock.getStatus() != CacheBlockStatus.Full){
                break;
            }
            cacheBlocks.add( cacheBlock );
            if( i == this.cacheBlocksGroup.size() - 1 ){
                rounds++;
                i = -1;
            }
        }
        return cacheBlocks;
    }

    private void updateCurrentPosition( int length ){
        for( int i= 0; i < length; i++ ){
            int incremented = this.currentPosition.incrementAndGet();
            if( incremented == cacheBlocksGroup.size() ){
                this.currentPosition.getAndSet( 0 );
            }
        }
    }

    private boolean isAllExiting(){
        for( int i = 0; i < jobNum; i++ ){
            CacheBlock cacheBlock = this.cacheBlocksGroup.get(i);
            MasterVolumeGram masterVolumeGram = (MasterVolumeGram) this.parentThread.parentExecutum();
            LocalStripedTaskThread bufferWriteThread = masterVolumeGram.getChildThread(cacheBlock.getBufferWriteThreadId());
            if( bufferWriteThread == null ){
                return false;
            }
            StripBufferStatus jobStatus = bufferWriteThread.getJobStatus();
            if( jobStatus != BufferWriteStatus.Exiting ){
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
