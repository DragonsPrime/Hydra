package com.pinecone.hydra.storage.volume.entity.local.spanned.export;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.framework.util.sqlite.SQLiteExecutor;
import com.pinecone.framework.util.sqlite.SQLiteHost;
import com.pinecone.hydra.storage.KChannel;
import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.StorageExportIORequest;
import com.pinecone.hydra.storage.volume.entity.LogicVolume;
import com.pinecone.hydra.storage.volume.entity.PhysicalVolume;
import com.pinecone.hydra.storage.volume.entity.SimpleVolume;
import com.pinecone.hydra.storage.volume.entity.SpannedVolume;
import com.pinecone.hydra.storage.volume.kvfs.KenVolumeFileSystem;
import com.pinecone.hydra.storage.volume.kvfs.OnVolumeFileSystem;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.sql.SQLException;
import java.util.List;

public class TitanSpannedChannelExport64 implements SpannedChannelExport64{
    private VolumeManager           volumeManager;
    private StorageExportIORequest storageExportIORequest;
    private KChannel               channel;
    private SpannedVolume           spannedVolume;
    private OnVolumeFileSystem      kenVolumeFileSystem;

    public TitanSpannedChannelExport64(SpannedChannelExportEntity entity , SpannedVolume spannedVolume){
        this.volumeManager =  entity.getVolumeManager();
        this.storageExportIORequest =  entity.getStorageIORequest();
        this.channel             =  entity.getChannel();
        this.spannedVolume       =  spannedVolume;
        this.kenVolumeFileSystem =  new KenVolumeFileSystem( this.volumeManager );
    }

    @Override
    public StorageIOResponse export() throws IOException, SQLException {
        //先查找冲突表中是否存在该文件
        List<LogicVolume> volumes = this.spannedVolume.getChildren();
        GUID physicsVolumeGuid = this.kenVolumeFileSystem.getKVFSPhysicsVolume(this.spannedVolume.getGuid());
        PhysicalVolume physicalVolume = this.volumeManager.getPhysicalVolume(physicsVolumeGuid);
        SQLiteExecutor sqLiteExecutor = this.getSQLiteExecutor(physicalVolume);
        GUID targetGuid = this.kenVolumeFileSystem.getKVFSCollisionTableTargetGuid(sqLiteExecutor, this.storageExportIORequest.getStorageObjectGuid());
        if ( targetGuid == null ){
            int idx = this.kenVolumeFileSystem.KVFSHash(this.storageExportIORequest.getStorageObjectGuid(), volumes.size());
            GUID tableTargetGuid = this.kenVolumeFileSystem.getKVFSIndexTableTargetGuid(sqLiteExecutor, idx);
            String source = this.getSource(tableTargetGuid, this.storageExportIORequest.getStorageObjectGuid());
            this.storageExportIORequest.setSourceName( source );
            SimpleVolume simpleVolume = (SimpleVolume)this.volumeManager.get(tableTargetGuid);
            List<GUID> guids = simpleVolume.listPhysicalVolume();
            PhysicalVolume volume = this.volumeManager.getPhysicalVolume(guids.get(0));
            return  volume.channelExport( this.volumeManager, this.storageExportIORequest, channel );
        }
        else {
            SimpleVolume simpleVolume = (SimpleVolume)this.volumeManager.get(targetGuid);
            List<GUID> guids = simpleVolume.listPhysicalVolume();
            PhysicalVolume volume = this.volumeManager.getPhysicalVolume(guids.get(0));
            return volume.channelExport( this.volumeManager, storageExportIORequest, channel );
        }

    }

    private SQLiteExecutor getSQLiteExecutor(PhysicalVolume physicalVolume ) throws SQLException {
        String mountPoint = physicalVolume.getMountPoint().getMountPoint();
        String url = mountPoint + "/" + this.spannedVolume.getGuid()+".db";
        return new SQLiteExecutor( new SQLiteHost(url) );
    }

    private String getSource( GUID volumeGuid, GUID storageObjectGuid ) throws SQLException {
        GUID physicsVolumeGuid = this.kenVolumeFileSystem.getKVFSPhysicsVolume( volumeGuid );
        PhysicalVolume physicalVolume = this.volumeManager.getPhysicalVolume( physicsVolumeGuid );
        String mountPoint = physicalVolume.getMountPoint().getMountPoint();
        String url = mountPoint + "/" + volumeGuid+".db";
        SQLiteExecutor sqLiteExecutor = new SQLiteExecutor(new SQLiteHost(url));
        return this.kenVolumeFileSystem.getKVFSTableSourceName(storageObjectGuid, sqLiteExecutor);
    }


}
