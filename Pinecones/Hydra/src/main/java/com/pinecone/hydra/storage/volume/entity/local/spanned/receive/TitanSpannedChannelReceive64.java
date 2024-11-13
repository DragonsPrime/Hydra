package com.pinecone.hydra.storage.volume.entity.local.spanned.receive;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.framework.util.sqlite.SQLiteExecutor;
import com.pinecone.framework.util.sqlite.SQLiteHost;
import com.pinecone.hydra.storage.MiddleStorageObject;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.LogicVolume;
import com.pinecone.hydra.storage.volume.entity.PhysicalVolume;
import com.pinecone.hydra.storage.volume.entity.ReceiveStorageObject;
import com.pinecone.hydra.storage.volume.entity.SpannedVolume;
import com.pinecone.hydra.storage.volume.entity.Volume;
import com.pinecone.hydra.storage.volume.entity.VolumeCapacity64;
import com.pinecone.hydra.storage.volume.kvfs.KenVolumeFileSystem;
import com.pinecone.hydra.storage.volume.kvfs.OnVolumeFileSystem;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.sql.SQLException;
import java.util.List;

public class TitanSpannedChannelReceive64 implements SpannedChannelReceive64{
    private SpannedVolume           spannedVolume;
    private FileChannel             channel;
    private VolumeManager           volumeManager;
    private ReceiveStorageObject    receiveStorageObject;
    private OnVolumeFileSystem      kenVolumeFileSystem;

    public TitanSpannedChannelReceive64( SpannedChannelReceiveEntity entity ){
        this.volumeManager = entity.getVolumeManager();
        this.spannedVolume = entity.getSpannedVolume();
        this.channel = entity.getChannel();
        this.receiveStorageObject = entity.getReceiveStorageObject();
        this.kenVolumeFileSystem = new KenVolumeFileSystem( this.volumeManager );
    }
    @Override
    public MiddleStorageObject receive() throws IOException, SQLException {
        return this.receiveInternal(null, null);
    }

    @Override
    public MiddleStorageObject receive(Number offset, Number endSize) throws IOException, SQLException {
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

    private MiddleStorageObject receiveInternal(Number offset, Number endSize) throws IOException, SQLException {
        List<LogicVolume> volumes = this.spannedVolume.getChildren();
        GUID physicsGuid = this.kenVolumeFileSystem.getKVFSPhysicsVolume( this.spannedVolume.getGuid() );
        PhysicalVolume physicalVolume = this.volumeManager.getPhysicalVolume(physicsGuid);
        SQLiteExecutor sqLiteExecutor = this.getSQLiteExecutor(physicalVolume);
        int idx = this.kenVolumeFileSystem.KVFSHash(this.receiveStorageObject.getStorageObjectGuid(), volumes.size());
        GUID volumeGuid = this.kenVolumeFileSystem.getKVFSIndexTableTargetGuid(sqLiteExecutor, idx);
        LogicVolume targetVolume = this.volumeManager.get(volumeGuid);


        if (this.freeSpace(targetVolume) < receiveStorageObject.getSize().longValue()) {

            for (LogicVolume volume : volumes) {
                if (this.freeSpace(volume) > receiveStorageObject.getSize().longValue()) {
                    this.kenVolumeFileSystem.insertKVFSCollisionTable(sqLiteExecutor, idx, receiveStorageObject.getStorageObjectGuid(), volume.getGuid());
                    return offset == null && endSize == null
                            ? volume.channelReceive(this.receiveStorageObject, this.channel)
                            : volume.channelReceive(this.receiveStorageObject,  this.channel, offset, endSize);
                }
            }
        } else {
            return offset == null && endSize == null
                    ? targetVolume.channelReceive(this.receiveStorageObject,  this.channel)
                    : targetVolume.channelReceive(this.receiveStorageObject,  this.channel, offset, endSize);
        }

        return null;
    }
}
