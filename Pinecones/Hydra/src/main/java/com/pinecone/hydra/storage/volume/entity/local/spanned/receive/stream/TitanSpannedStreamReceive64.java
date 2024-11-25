package com.pinecone.hydra.storage.volume.entity.local.spanned.receive.stream;

import com.pinecone.framework.util.Debug;
import com.pinecone.framework.util.id.GUID;
import com.pinecone.framework.util.sqlite.SQLiteExecutor;
import com.pinecone.framework.util.sqlite.SQLiteHost;
import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.StorageReceiveIORequest;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.LogicVolume;
import com.pinecone.hydra.storage.volume.entity.PhysicalVolume;
import com.pinecone.hydra.storage.volume.entity.SimpleVolume;
import com.pinecone.hydra.storage.volume.entity.SpannedVolume;
import com.pinecone.hydra.storage.volume.entity.Volume;
import com.pinecone.hydra.storage.volume.entity.VolumeCapacity64;
import com.pinecone.hydra.storage.volume.entity.local.physical.receive.stream.TitanDirectStreamReceiveEntity64;
import com.pinecone.hydra.storage.volume.entity.local.simple.recevice.stream.TitanSimpleStreamReceiveEntity64;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlock;
import com.pinecone.hydra.storage.volume.kvfs.KenVolumeFileSystem;
import com.pinecone.hydra.storage.volume.kvfs.OnVolumeFileSystem;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;

public class TitanSpannedStreamReceive64 implements SpannedStreamReceive64{
    protected SpannedVolume             spannedVolume;

    protected InputStream               stream;

    protected VolumeManager             volumeManager;

    protected StorageReceiveIORequest   storageReceiveIORequest;

    protected OnVolumeFileSystem        kenVolumeFileSystem;

    public TitanSpannedStreamReceive64( SpannedStreamReceiveEntity64 entity ){
        this.volumeManager           = entity.getVolumeManager();
        this.stream                  = entity.getStream();
        this.spannedVolume           = entity.getSpannedVolume();
        this.storageReceiveIORequest = entity.getReceiveStorageObject();
        this.kenVolumeFileSystem = new KenVolumeFileSystem( this.volumeManager );
    }

    @Override
    public StorageIOResponse streamReceive() throws IOException, SQLException {
        return this.receiveInternal(null, null);
    }

    @Override
    public StorageIOResponse streamReceive(Number offset, Number endSize) throws IOException, SQLException {
        return this.receiveInternal(offset, endSize);
    }

    @Override
    public StorageIOResponse streamReceive(CacheBlock cacheBlock, byte[] buffer) throws IOException, SQLException {
        return null;
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
                    // todo 先默认底层只能套simple卷，后续要增加适配器操作
                    TitanSimpleStreamReceiveEntity64 titanSimpleStreamReceiveEntity64 = new TitanSimpleStreamReceiveEntity64( this.volumeManager, this.storageReceiveIORequest, this.stream, (SimpleVolume) volume );
                    this.kenVolumeFileSystem.insertKVFSCollisionTable(sqLiteExecutor, idx, storageReceiveIORequest.getStorageObjectGuid(), volume.getGuid());
                    return offset == null && endSize == null
                            ? volume.receive( titanSimpleStreamReceiveEntity64 )
                            : volume.receive( titanSimpleStreamReceiveEntity64, offset, endSize );
                }
            }
        } else {
            TitanSimpleStreamReceiveEntity64 titanSimpleStreamReceiveEntity64 = new TitanSimpleStreamReceiveEntity64( this.volumeManager, this.storageReceiveIORequest, this.stream, (SimpleVolume) targetVolume );
            return offset == null && endSize == null
                    ? targetVolume.receive( titanSimpleStreamReceiveEntity64 )
                    : targetVolume.receive( titanSimpleStreamReceiveEntity64, offset, endSize );
        }

        return null;
    }

    private SQLiteExecutor getSQLiteExecutor( PhysicalVolume physicalVolume ) throws SQLException {
        String mountPoint = physicalVolume.getMountPoint().getMountPoint();
        String url = mountPoint + "/" + this.spannedVolume.getGuid()+".db";
        return new SQLiteExecutor( new SQLiteHost(url) );
    }

    private long freeSpace( Volume volume ){
        VolumeCapacity64 volumeCapacity = volume.getVolumeCapacity();
        return volumeCapacity.getDefinitionCapacity() - volumeCapacity.getUsedSize();
    }
}
