package com.pinecone.hydra.storage.volume.entity.local.striped.export;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.framework.util.sqlite.SQLiteExecutor;
import com.pinecone.framework.util.sqlite.SQLiteHost;
import com.pinecone.hydra.storage.MiddleStorageObject;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.ExportStorageObject;
import com.pinecone.hydra.storage.volume.entity.LogicVolume;
import com.pinecone.hydra.storage.volume.entity.PhysicalVolume;
import com.pinecone.hydra.storage.volume.entity.StripedVolume;
import com.pinecone.hydra.storage.volume.entity.local.striped.LocalStripedTaskThread;
import com.pinecone.hydra.storage.volume.entity.local.striped.TitanStripChannelExportJob;
import com.pinecone.hydra.storage.volume.kvfs.KenVolumeFileSystem;
import com.pinecone.hydra.storage.volume.kvfs.OnVolumeFileSystem;
import com.pinecone.hydra.storage.volume.runtime.MasterVolumeGram;
import com.pinecone.hydra.system.Hydrarum;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TitanStripedChannelExport64 implements StripedChannelExport64{
    private VolumeManager           volumeManager;
    private ExportStorageObject     exportStorageObject;
    private FileChannel             channel;
    private StripedVolume           stripedVolume;
    private OnVolumeFileSystem      kenVolumeFileSystem;

    public TitanStripedChannelExport64(StripedChannelExportEntity entity, StripedVolume stripedVolume){
        this.volumeManager = entity.getVolumeManager();
        this.exportStorageObject = entity.getExportStorageObject();
        this.channel = entity.getChannel();
        this.stripedVolume = stripedVolume;
        this.kenVolumeFileSystem = new KenVolumeFileSystem( this.volumeManager );
    }

    @Override
    public MiddleStorageObject export() throws IOException, SQLException {
        List<LogicVolume> volumes = this.stripedVolume.getChildren();
        Number stripSize = this.volumeManager.getConfig().getDefaultStripSize();
        int jobNum = volumes.size();
        GUID physicsVolumeGuid = this.kenVolumeFileSystem.getKVFSPhysicsVolume(this.stripedVolume.getGuid());
        PhysicalVolume physicalVolume = this.volumeManager.getPhysicalVolume(physicsVolumeGuid);
        String url = physicalVolume.getMountPoint().getMountPoint()+ "\\" +this.stripedVolume.getGuid()+".db";
        SQLiteExecutor sqLiteExecutor = new SQLiteExecutor( new SQLiteHost(url) );
        //创建两个缓冲区
        byte[] firstBuffer = new byte[jobNum * stripSize.intValue()];
        byte[] secondBuffer = new byte[jobNum * stripSize.intValue()];
        AtomicInteger firstCounter = new AtomicInteger(0);
        AtomicInteger secondCounter = new AtomicInteger(0);
        Hydrarum hydrarum = this.volumeManager.getHydrarum();
        MasterVolumeGram masterVolumeGram = new MasterVolumeGram( this.stripedVolume.getGuid().toString(), hydrarum );
        hydrarum.getTaskManager().add( masterVolumeGram );
        int index = 0;
        for( LogicVolume volume : volumes ){
            String sourceName = this.kenVolumeFileSystem.getKVFSFileStripSourceName(sqLiteExecutor, volume.getGuid(), this.exportStorageObject.getStorageObjectGuid());
            this.exportStorageObject.setSourceName( sourceName );
            TitanStripChannelExportJob exportJob = new TitanStripChannelExportJob( this.exportStorageObject, firstBuffer, secondBuffer, jobNum, index, this.volumeManager, volume, this.channel, firstCounter, secondCounter );
            LocalStripedTaskThread taskThread = new LocalStripedTaskThread( this.stripedVolume.getName()+index,masterVolumeGram,exportJob);
            masterVolumeGram.getTaskManager().add( taskThread );
            taskThread.start();
            index++;
        }
        return null;
    }
}
