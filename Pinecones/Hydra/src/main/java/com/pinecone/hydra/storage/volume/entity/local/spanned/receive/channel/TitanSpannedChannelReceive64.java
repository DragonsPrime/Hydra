package com.pinecone.hydra.storage.volume.entity.local.spanned.receive.channel;

import com.pinecone.framework.util.Debug;
import com.pinecone.framework.util.id.GUID;
import com.pinecone.framework.util.sqlite.SQLiteExecutor;
import com.pinecone.framework.util.sqlite.SQLiteHost;
import com.pinecone.hydra.storage.KChannel;
import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.LogicVolume;
import com.pinecone.hydra.storage.volume.entity.PhysicalVolume;
import com.pinecone.hydra.storage.StorageReceiveIORequest;
import com.pinecone.hydra.storage.volume.entity.SpannedVolume;
import com.pinecone.hydra.storage.volume.entity.Volume;
import com.pinecone.hydra.storage.volume.entity.VolumeCapacity64;
import com.pinecone.hydra.storage.volume.kvfs.KenVolumeFileSystem;
import com.pinecone.hydra.storage.volume.kvfs.OnVolumeFileSystem;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class TitanSpannedChannelReceive64 implements SpannedChannelReceive64{
    private SpannedVolume           spannedVolume;
    private KChannel                channel;
    private VolumeManager           volumeManager;
    private StorageReceiveIORequest storageReceiveIORequest;
    private OnVolumeFileSystem      kenVolumeFileSystem;

    public TitanSpannedChannelReceive64( SpannedChannelReceiveEntity entity ){
        this.volumeManager = entity.getVolumeManager();
        this.spannedVolume = entity.getSpannedVolume();
        this.channel = entity.getChannel();
        this.storageReceiveIORequest = entity.getReceiveStorageObject();
        this.kenVolumeFileSystem = new KenVolumeFileSystem( this.volumeManager );
    }
    @Override
    public StorageIOResponse receive() throws IOException, SQLException {
        return this.receiveInternal(null, null);
    }

    @Override
    public StorageIOResponse receive(Number offset, Number endSize) throws IOException, SQLException {
        return this.receiveInternal(offset, endSize);
    }

    private long freeSpace( Volume volume ){
        VolumeCapacity64 volumeCapacity = volume.getVolumeCapacity();
        return volumeCapacity.getDefinitionCapacity() - volumeCapacity.getUsedSize();
    }

    private SQLiteExecutor getSQLiteExecutor( PhysicalVolume physicalVolume ) throws SQLException {
        String mountPoint = physicalVolume.getMountPoint().getMountPoint();
        String url = mountPoint + "/" + this.spannedVolume.getGuid()+".db";
        return new SQLiteExecutor( new SQLiteHost(url) );
    }

    private StorageIOResponse receiveInternal(Number offset, Number endSize) throws IOException, SQLException {
        List<LogicVolume> volumes = this.spannedVolume.getChildren();
        GUID physicsGuid = this.kenVolumeFileSystem.getKVFSPhysicsVolume( this.spannedVolume.getGuid() );
        PhysicalVolume physicalVolume = this.volumeManager.getPhysicalVolume(physicsGuid);
        SQLiteExecutor sqLiteExecutor = this.getSQLiteExecutor(physicalVolume);
        int idx = this.kenVolumeFileSystem.KVFSHash(this.storageReceiveIORequest.getStorageObjectGuid(), volumes.size());
        //Debug.trace("存储的GUID是："+storageReceiveIORequest.getStorageObjectGuid());
        GUID volumeGuid = this.kenVolumeFileSystem.getKVFSIndexTableTargetGuid(sqLiteExecutor, idx);
        //Debug.trace( volumeGuid );
        LogicVolume targetVolume = this.volumeManager.get(volumeGuid);


        if (this.freeSpace(targetVolume) < storageReceiveIORequest.getSize().longValue()) {

            for (LogicVolume volume : volumes) {
                if (this.freeSpace(volume) > storageReceiveIORequest.getSize().longValue()) {
                    this.kenVolumeFileSystem.insertKVFSCollisionTable(sqLiteExecutor, idx, storageReceiveIORequest.getStorageObjectGuid(), volume.getGuid());
                    return offset == null && endSize == null
                            ? volume.channelReceive(this.storageReceiveIORequest, this.channel)
                            : volume.channelReceive(this.storageReceiveIORequest,  this.channel, offset, endSize);
                }
            }
        } else {
            return offset == null && endSize == null
                    ? targetVolume.channelReceive(this.storageReceiveIORequest,  this.channel)
                    : targetVolume.channelReceive(this.storageReceiveIORequest,  this.channel, offset, endSize);
        }

        return null;
    }
}
