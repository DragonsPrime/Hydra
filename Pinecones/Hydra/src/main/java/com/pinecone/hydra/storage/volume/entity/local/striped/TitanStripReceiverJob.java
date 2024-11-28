package com.pinecone.hydra.storage.volume.entity.local.striped;

import com.pinecone.framework.system.ProxyProvokeHandleException;
import com.pinecone.framework.util.Debug;
import com.pinecone.framework.util.rdb.MappedExecutor;
import com.pinecone.hydra.storage.KChannel;
import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.LogicVolume;
import com.pinecone.hydra.storage.volume.entity.ReceiveEntity;
import com.pinecone.hydra.storage.StorageReceiveIORequest;
import com.pinecone.hydra.storage.volume.entity.SimpleVolume;
import com.pinecone.hydra.storage.volume.entity.local.simple.recevice.TitanSimpleReceiveEntity64;
import com.pinecone.hydra.storage.volume.kvfs.KenVolumeFileSystem;
import com.pinecone.hydra.storage.volume.kvfs.OnVolumeFileSystem;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.sql.SQLException;
import java.util.concurrent.Semaphore;

public class TitanStripReceiverJob implements StripChannelReceiverJob{
    private LogicVolume                 volume;
    private int                         jobCount;
    private int                         jobCode;
    private VolumeManager               volumeManager;
    private StorageReceiveIORequest     object;
    private KChannel                    fileChannel;
    private OnVolumeFileSystem          kenVolumeFileSystem;
    private MappedExecutor              executor;
    private StorageIOResponse           storageIOResponse;
    private Number                      offset;
    private Number                      endSize;


    public TitanStripReceiverJob(ReceiveEntity entity, KChannel channel, int jobCount, int jobCode, LogicVolume volume, MappedExecutor executor, Number offset, Number ednSize ){
        this.volumeManager          = entity.getVolumeManager();
        this.object                 = entity.getReceiveStorageObject();
        this.fileChannel            = channel;
        this.jobCount               = jobCount;
        this.jobCode                = jobCode;
        this.volume                 = volume;
        this.kenVolumeFileSystem    = new KenVolumeFileSystem( this.volumeManager );
        this.executor               = executor;
        this.offset                 = offset;
        this.endSize                = ednSize;
    }

    @Override
    public void execute()  {
        //每次计算要保存的部分
        long size = this.endSize.longValue();
        long stripSize = this.volumeManager.getConfig().getDefaultStripSize().longValue();
        long currentPosition = jobCode * stripSize + this.offset.longValue();

        while( true ){

            long bufferSize = stripSize;
            if( currentPosition >= size ){
                break;
            }
            if( currentPosition + bufferSize > size ){
                bufferSize = size - currentPosition;
            }

            try {
//                this.storageIOResponse = this.volume.channelReceive(this.object, this.fileChannel, currentPosition, bufferSize);
                // todo 先默认底层是simpleVolume
                TitanSimpleReceiveEntity64 receiveEntity = new TitanSimpleReceiveEntity64( this.volumeManager, this.object, this.fileChannel, (SimpleVolume) volume);
                this.storageIOResponse = this.volume.receive( receiveEntity, currentPosition, bufferSize );
            } catch (IOException | SQLException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }

            currentPosition += bufferSize * jobCount;
        }
        try {
            if( this.storageIOResponse != null ){
                this.kenVolumeFileSystem.insertKVFSFileStripTable( executor, jobCode,  volume.getGuid(), this.object.getStorageObjectGuid(), this.storageIOResponse.getSourceName() );
            }
            //this.kenVolumeFileSystem.insertKVFSFileStripTable( executor, jobCode,  volume.getGuid(), this.object.getStorageObjectGuid(), this.storageIOResponse.getSourceName() );
        } catch (SQLException e) {
            throw new ProxyProvokeHandleException(e);
        }
    }

    @Override
    public void applyThread(LocalStripedTaskThread thread) {

    }

    @Override
    public StripBufferStatus getStatus() {
        return null;
    }

    @Override
    public Semaphore getBlockerLatch() {
        return null;
    }

    @Override
    public void setStatus(StripBufferStatus status) {

    }

}
