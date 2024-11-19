package com.pinecone.hydra.storage.volume.entity.local.striped;

import com.pinecone.framework.system.ProxyProvokeHandleException;
import com.pinecone.framework.util.rdb.MappedExecutor;
import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.LogicVolume;
import com.pinecone.hydra.storage.volume.entity.ReceiveEntity;
import com.pinecone.hydra.storage.StorageReceiveIORequest;
import com.pinecone.hydra.storage.volume.kvfs.KenVolumeFileSystem;
import com.pinecone.hydra.storage.volume.kvfs.OnVolumeFileSystem;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.sql.SQLException;
import java.util.concurrent.Semaphore;

public class TitanStripReceiverJob implements StripChannelReceiverJob{
    private LogicVolume             volume;
    private int                     jobCount;
    private int                     jobCode;
    private VolumeManager           volumeManager;
    private StorageReceiveIORequest object;
    private String                  destDirPath;
    private FileChannel             fileChannel;
    private OnVolumeFileSystem      kenVolumeFileSystem;
    private MappedExecutor          executor;
    private StorageIOResponse storageIOResponse;


    public TitanStripReceiverJob(ReceiveEntity entity, FileChannel channel, int jobCount, int jobCode, LogicVolume volume, MappedExecutor executor ){
        this.volumeManager          = entity.getVolumeManager();
        this.object                 = entity.getReceiveStorageObject();
        this.fileChannel            = channel;
        this.jobCount                 = jobCount;
        this.jobCode                = jobCode;
        this.volume                 = volume;
        this.kenVolumeFileSystem    = new KenVolumeFileSystem( this.volumeManager );
        this.executor               = executor;
    }

    @Override
    public void execute()  {
        //每次计算要保存的部分
        long size = this.object.getSize().longValue();
        int sequenceNumber = jobCode;
        long stripSize = this.volumeManager.getConfig().getDefaultStripSize().longValue();
        long currentPosition = jobCode * stripSize;

        while( true ){
            long bufferSize = stripSize;
            if( currentPosition >= size ){
                break;
            }
            if( currentPosition + bufferSize > size ){
                bufferSize = size - currentPosition;
            }

            try {
                this.storageIOResponse = this.volume.channelReceive(this.object, this.fileChannel, currentPosition, bufferSize);

            } catch (IOException | SQLException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }

            currentPosition += bufferSize * jobCount;
        }
        try {
            this.kenVolumeFileSystem.insertKVFSFileStripTable( executor, jobCode,  volume.getGuid(), this.object.getStorageObjectGuid(), storageIOResponse.getSourceName() );
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
