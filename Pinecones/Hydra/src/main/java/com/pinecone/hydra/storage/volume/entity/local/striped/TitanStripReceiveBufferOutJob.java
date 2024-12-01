package com.pinecone.hydra.storage.volume.entity.local.striped;

import com.pinecone.framework.util.Debug;
import com.pinecone.framework.util.rdb.MappedExecutor;
import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.StorageReceiveIORequest;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.LogicVolume;
import com.pinecone.hydra.storage.volume.entity.SimpleVolume;
import com.pinecone.hydra.storage.volume.entity.local.simple.recevice.stream.TitanSimpleStreamReceiveEntity64;
import com.pinecone.hydra.storage.volume.kvfs.KenVolumeFileSystem;
import com.pinecone.hydra.storage.volume.kvfs.OnVolumeFileSystem;
import com.pinecone.hydra.storage.volume.runtime.MasterVolumeGram;
import com.pinecone.hydra.storage.volume.runtime.VolumeJobCompromiseException;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Semaphore;

public class TitanStripReceiveBufferOutJob implements StripReceiveBufferOutJob{
    protected MasterVolumeGram          masterVolumeGram;

    protected byte[]                    buffer;

    protected StripBufferStatus         status;

    protected InputStream               stream;

    protected final Semaphore           blockerLatch;

    protected List< CacheBlock >        cacheBlocksGroup;

    protected LocalStripedTaskThread    parentThread;

    protected VolumeManager             volumeManager;

    protected long                      totalSize;

    protected long                      exportSize;


    protected StorageReceiveIORequest   request;

    protected OnVolumeFileSystem        kenVolumeFileSystem;

    protected MappedExecutor            executor;


    public TitanStripReceiveBufferOutJob(MasterVolumeGram masterVolumeGram, VolumeManager volumeManager, InputStream stream, StorageReceiveIORequest request, MappedExecutor executor ){
        this.masterVolumeGram       = masterVolumeGram;
        this.stream                 = stream;
        this.totalSize              = request.getSize().longValue();
        this.volumeManager          = volumeManager;
        this.blockerLatch           = new Semaphore(0);
        this.masterVolumeGram.applyBufferOutBlockerLatch( this.blockerLatch );
        this.exportSize             = 0;
        this.cacheBlocksGroup       = this.masterVolumeGram.getCacheGroup();
        this.status                 = ReceiveBufferOutStatus.Suspended;
        this.request                = request;
        this.buffer                 = masterVolumeGram.getBuffer();
        this.kenVolumeFileSystem    = new KenVolumeFileSystem( this.volumeManager );
        this.executor               = executor;
    }
    @Override
    public void execute() throws VolumeJobCompromiseException {
        while( true ){
            try {
                Debug.trace("我摸鱼了");
                this.blockerLatch.acquire();
                if( exportSize >= totalSize ){
                    this.status = ReceiveBufferOutStatus.Exiting;
                    for( CacheBlock cacheBlock : cacheBlocksGroup ){
                        LocalStripedTaskThread bufferInThread = this.masterVolumeGram.getChildThread(cacheBlock.getBufferWriteThreadId());
                        bufferInThread.setJobStatus( ReceiveBufferInStatus.Exiting );
                        bufferInThread.getBlockerLatch().release();
                    }
                    break;
                }
                Debug.trace("开始上班");
                this.status = ReceiveBufferOutStatus.Writing;
                CacheBlock currentCacheBlock = this.cacheBlocksGroup.get(this.masterVolumeGram.getCurrentBufferInJobCode());
                int start = currentCacheBlock.getValidByteStart().intValue();
                int end   = currentCacheBlock.getValidByteEnd().intValue();
                // todo应该使用适配器，现在默认底层是simpleVolume
                TitanSimpleStreamReceiveEntity64 entity = new TitanSimpleStreamReceiveEntity64( this.volumeManager,this.request, this.stream, (SimpleVolume) currentCacheBlock.getVolume() );
                StorageIOResponse response = currentCacheBlock.getVolume().receive(entity, currentCacheBlock, this.buffer);

                this.status = ReceiveBufferOutStatus.Suspended;
                if( !this.isExist() ){
                    LogicVolume currentVolume = this.cacheBlocksGroup.get(this.masterVolumeGram.getCurrentBufferInJobCode()).getVolume();

                    this.kenVolumeFileSystem.insertStripMetaTable( this.executor, this.masterVolumeGram.getCurrentBufferInJobCode(), currentVolume.getGuid(), this.request.getStorageObjectGuid(), response.getSourceName() );

                }

                this.exportSize += ( end - start );
                this.masterVolumeGram.setCurrentBufferInJobCode( this.masterVolumeGram.getCurrentBufferInJobCode() + 1 );
                if( this.masterVolumeGram.getCurrentBufferInJobCode() >= this.masterVolumeGram.getJobCount() ){
                    this.masterVolumeGram.setCurrentBufferInJobCode( 0 );
                }
                //唤醒所有线程
                for( CacheBlock cacheBlock : cacheBlocksGroup ){
                    LocalStripedTaskThread bufferInThread = this.masterVolumeGram.getChildThread(cacheBlock.getBufferWriteThreadId());
                    if( bufferInThread.getJobStatus() == ReceiveBufferInStatus.Suspended ){
                        bufferInThread.getBlockerLatch().release();
                    }
                }
            } catch (SQLException | IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }


        }

    }

    @Override
    public void applyThread(LocalStripedTaskThread thread) {
        this.parentThread = thread;
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

    boolean isExist(  ) throws SQLException {
        LogicVolume currentVolume = this.cacheBlocksGroup.get(this.masterVolumeGram.getCurrentBufferInJobCode()).getVolume();
        return this.kenVolumeFileSystem.isExistStripMetaTable(this.executor, currentVolume.getGuid(), this.request.getStorageObjectGuid());
    }
}
