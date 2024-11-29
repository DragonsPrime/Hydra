package com.pinecone.hydra.storage.volume.entity.local.spanned.receive;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.framework.util.sqlite.SQLiteExecutor;
import com.pinecone.framework.util.sqlite.SQLiteHost;
import com.pinecone.hydra.storage.KChannel;
import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.StorageReceiveIORequest;
import com.pinecone.hydra.storage.volume.UnifiedTransmitConstructor;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.LogicVolume;
import com.pinecone.hydra.storage.volume.entity.PhysicalVolume;
import com.pinecone.hydra.storage.volume.entity.ReceiveEntity;
import com.pinecone.hydra.storage.volume.entity.SimpleVolume;
import com.pinecone.hydra.storage.volume.entity.SpannedVolume;
import com.pinecone.hydra.storage.volume.entity.Volume;
import com.pinecone.hydra.storage.volume.entity.VolumeCapacity64;
import com.pinecone.hydra.storage.volume.entity.local.simple.recevice.TitanSimpleReceiveEntity64;
import com.pinecone.hydra.storage.volume.kvfs.KenVolumeFileSystem;
import com.pinecone.hydra.storage.volume.kvfs.OnVolumeFileSystem;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

public class TitanSpannedReceive64 implements SpannedReceive64{
    protected SpannedVolume                 spannedVolume;

    protected KChannel                      channel;

    protected VolumeManager                 volumeManager;

    protected StorageReceiveIORequest       storageReceiveIORequest;

    protected OnVolumeFileSystem            kenVolumeFileSystem;

    public TitanSpannedReceive64( SpannedReceiveEntity64 entity ){
        this.spannedVolume           = entity.getSpannedVolume();
        this.channel                 = entity.getKChannel();
        this.volumeManager           = entity.getVolumeManager();
        this.storageReceiveIORequest = entity.getReceiveStorageObject();
        this.kenVolumeFileSystem     = new KenVolumeFileSystem( this.volumeManager );
    }
    @Override
    public StorageIOResponse receive() throws IOException, SQLException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return this.receiveInternal( null, null );
    }

    @Override
    public StorageIOResponse receive(Number offset, Number endSize) throws IOException, SQLException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return this.receiveInternal( offset, endSize );
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

    private StorageIOResponse receiveInternal(Number offset, Number endSize) throws IOException, SQLException, InvocationTargetException, InstantiationException, IllegalAccessException {
        List<LogicVolume> volumes = this.spannedVolume.getChildren();
        UnifiedTransmitConstructor constructor = new UnifiedTransmitConstructor();
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
                    //TitanSimpleReceiveEntity64 receiveEntity = new TitanSimpleReceiveEntity64( this.volumeManager, this.storageReceiveIORequest, this.channel, (SimpleVolume) volume);

                    ReceiveEntity receiveEntity = constructor.getReceiveEntity(volume.getClass(), this.volumeManager, this.storageReceiveIORequest, this.channel, volume);
                    return offset == null && endSize == null
                            ? volume.receive( receiveEntity )
                            : volume.receive( receiveEntity, offset, endSize );
                }
            }
        } else {
            //TitanSimpleReceiveEntity64 receiveEntity = new TitanSimpleReceiveEntity64( this.volumeManager, this.storageReceiveIORequest, this.channel, (SimpleVolume) targetVolume);
            ReceiveEntity receiveEntity = constructor.getReceiveEntity(targetVolume.getClass(), this.volumeManager, this.storageReceiveIORequest, this.channel, targetVolume);
            return offset == null && endSize == null
                    ? targetVolume.receive( receiveEntity )
                    : targetVolume.receive(receiveEntity, offset, endSize);
        }

        return null;
    }
}
