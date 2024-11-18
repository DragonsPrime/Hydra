package com.pinecone.hydra.storage.volume.entity.local.spanned.export;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.framework.util.sqlite.SQLiteExecutor;
import com.pinecone.framework.util.sqlite.SQLiteHost;
import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.StorageIORequest;
import com.pinecone.hydra.storage.volume.entity.LogicVolume;
import com.pinecone.hydra.storage.volume.entity.PhysicalVolume;
import com.pinecone.hydra.storage.volume.entity.SpannedVolume;
import com.pinecone.hydra.storage.volume.kvfs.KenVolumeFileSystem;
import com.pinecone.hydra.storage.volume.kvfs.OnVolumeFileSystem;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.sql.SQLException;
import java.util.List;

public class TitanSpannedChannelExport64 implements SpannedChannelExport64{
    private VolumeManager           volumeManager;
    private StorageIORequest storageIORequest;
    private FileChannel             channel;
    private SpannedVolume           spannedVolume;
    private OnVolumeFileSystem      kenVolumeFileSystem;

    public TitanSpannedChannelExport64(SpannedChannelExportEntity entity , SpannedVolume spannedVolume){
        this.volumeManager =  entity.getVolumeManager();
        this.storageIORequest =  entity.getStorageIORequest();
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
        GUID targetGuid = this.kenVolumeFileSystem.getKVFSCollisionTableTargetGuid(sqLiteExecutor, storageIORequest.getStorageObjectGuid());
        if ( targetGuid == null ){
            int idx = this.kenVolumeFileSystem.KVFSHash(storageIORequest.getStorageObjectGuid(), volumes.size());
            GUID tableTargetGuid = this.kenVolumeFileSystem.getKVFSIndexTableTargetGuid(sqLiteExecutor, idx);
            PhysicalVolume volume = this.volumeManager.getPhysicalVolume(tableTargetGuid);
            return  volume.channelExport( this.volumeManager, storageIORequest, channel );
        }
        else {
            PhysicalVolume volume = this.volumeManager.getPhysicalVolume(targetGuid);
            return volume.channelExport( this.volumeManager, storageIORequest, channel );
        }

    }

    private SQLiteExecutor getSQLiteExecutor(PhysicalVolume physicalVolume ) throws SQLException {
        String mountPoint = physicalVolume.getMountPoint().getMountPoint();
        String url = mountPoint + "/" + this.spannedVolume.getGuid()+".db";
        return new SQLiteExecutor( new SQLiteHost(url) );
    }
}
