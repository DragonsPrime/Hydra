package com.pinecone.hydra.storage.volume.entity.local.striped;

import com.pinecone.framework.util.Debug;
import com.pinecone.hydra.storage.Chanface;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.runtime.MasterVolumeGram;
import com.pinecone.hydra.storage.volume.runtime.VolumeJobCompromiseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class TitanStripBufferOutJob implements StripBufferOutJob {
    protected VolumeManager             volumeManager;
    protected Chanface channel;
    protected int                       jobCount;

    protected StripBufferStatus         status;
    protected List< CacheBlock >        cacheBlocksGroup;
    protected AtomicInteger             currentPosition;
    protected LocalStripedTaskThread    parentThread;
    protected byte[]                    mBuffer;
    protected long                      totalSize;
    protected long                      exportSize;
    protected final Semaphore           mBlockerLatch;
    protected MasterVolumeGram          masterVolumeGram;

    public TitanStripBufferOutJob(MasterVolumeGram masterVolumeGram, VolumeManager volumeManager, Chanface channel, long totalSize, Semaphore blockerLatch){
        this.masterVolumeGram  = masterVolumeGram;
        this.volumeManager     = volumeManager;
        this.channel           = channel;
        this.jobCount          = masterVolumeGram.getJobCount();
        this.currentPosition   = new AtomicInteger(0);
        this.cacheBlocksGroup  = masterVolumeGram.getCacheGroup();
        this.mBuffer           = masterVolumeGram.getBuffer();
        this.totalSize         = totalSize;
        this.mBlockerLatch     = blockerLatch;
//        this.masterVolumeGram.applyBufferOutBlockerLatch( this.mBlockerLatch );
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
        this.status = BufferOutStatus.Writing;
    }

    protected void setSuspendedStatus() {
        this.status = BufferOutStatus.Suspended;
    }

    protected void setExitingStatus() {
        this.status = BufferOutStatus.Exiting;
    }

    @Override
    public void execute() throws VolumeJobCompromiseException {
        while( true ){

            if( this.exportSize >= this.totalSize ){
                this.setExitingStatus();
                this.masterVolumeGram.getMajorJobFuture().complete( true );
                return;
            }
            if( !this.isAllExiting() ){
                try{
                    Debug.trace("摸鱼罗");
                    this.setSuspendedStatus();
                    this.mBlockerLatch.acquire();
                }
                catch ( InterruptedException e ) {
                    Thread.currentThread().interrupt();
                    this.masterVolumeGram.getMajorJobFuture().completeExceptionally( e );
                    break;
                }
            }
            List<CacheBlock> writableCacheBlocks = this.getWritableCacheBlocks();
//            Debug.trace("准备干活");
            if (!writableCacheBlocks.isEmpty()){
                Debug.trace("执行写入");

                //ByteBuffer buffer = this.mergeArrays( writableCacheBlocks );
                //ByteBuffer writeBuffer = ByteBuffer.wrap(buffer, 0, buffer.length );
                try {
                    //this.channel.write(buffer);

                    int write = this.channel.write(this.mBuffer, writableCacheBlocks);
                    this.exportSize += write;
                }
                catch ( IOException e ) {
                    this.masterVolumeGram.getMajorJobFuture().completeExceptionally( e );
                    break;
                }
                //Arrays.fill(this.mBuffer, (byte) 0);
                this.updateCurrentPosition( writableCacheBlocks.size() );


                this.setSuspendedStatus();
                //唤醒所有缓存线程
                //this.lockEntity.unlockPipeStage();
                for ( int i = 0; i < jobCount; ++i ){
                    CacheBlock cacheBlock = this.cacheBlocksGroup.get(i);
                    MasterVolumeGram masterVolumeGram = (MasterVolumeGram) this.parentThread.parentExecutum();
                    LocalStripedTaskThread bufferWriteThread = masterVolumeGram.getChildThread(cacheBlock.getBufferWriteThreadId());
                    //当文件较小时，只有一个线程在执行写入且一次执行完就结束线程，可能会导致thread为null的情况
                    if( bufferWriteThread != null ){
                        StripBufferStatus jobStatus = bufferWriteThread.getJobStatus();
                        if( jobStatus == BufferWriteStatus.Suspended ){
                            bufferWriteThread.setJobStatus( BufferWriteStatus.Writing );
                            Semaphore jobLock = bufferWriteThread.getBlockerLatch();
                            Debug.trace("线程"+bufferWriteThread.getName()+"被唤醒");
                            jobLock.release();
                        }
                    }

                }
            }

        }

        this.masterVolumeGram.getMajorJobFuture().complete( false );
        //Debug.warnSyn( "wangwang" );
    }

    @Override
    public Semaphore getBlockerLatch() {
        return this.mBlockerLatch;
    }

    @Override
    public void setStatus(StripBufferStatus status) {
        this.status = status;
    }

//    private int getCacheLength(){
//        int rounds = 0;
//        int length = 0;
//        for( int i = this.currentPosition.get(); i < this.cacheBlocksGroup.size(); i++ ){
//            if( i == currentPosition.get() && rounds == 1 ){
//                break;
//            }
//
//            CacheBlock cacheBlock = cacheBlocksGroup.get(i);
//            if( cacheBlock.getStatus() != CacheBlockStatus.Full){
//                return length;
//            }
//            length++;
//            if( i == this.cacheBlocksGroup.size() - 1 ){
//                rounds++;
//                i = -1;
//            }
//        }
//        return length;
//    }
//
//    private ByteBuffer mergeArrays( List< CacheBlock > writableCacheBlocks ){
//        // 计算所有缓存块的总长度
//        int totalLength = 0;
//        for (CacheBlock cacheBlock : writableCacheBlocks) {
//            totalLength += cacheBlock.getValidByteEnd().intValue() - cacheBlock.getValidByteStart().intValue();
//        }
//
//        // 创建一个 ByteBuffer 来存储合并的数据
//        ByteBuffer mergedBuffer = ByteBuffer.allocate(totalLength);
//
//        // 将数据从 mBuffer 复制到 mergedBuffer
//        for (CacheBlock cacheBlock : writableCacheBlocks) {
//            int start = cacheBlock.getValidByteStart().intValue();
//            int end = cacheBlock.getValidByteEnd().intValue();
//            int bufferSize = end - start;
//
//            // 将 mBuffer 中的数据复制到 mergedBuffer
//            mergedBuffer.put(mBuffer, start, bufferSize);
//
//            // 将缓存块状态设置为 Free
//            cacheBlock.setStatus(CacheBlockStatus.Free);
//        }
//        this.exportSize += totalLength;
//        // 准备将 mergedBuffer 用于读取
//        mergedBuffer.flip();
//        return mergedBuffer;
//    }

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
        for( int i = 0; i < jobCount; ++i ){
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
}
