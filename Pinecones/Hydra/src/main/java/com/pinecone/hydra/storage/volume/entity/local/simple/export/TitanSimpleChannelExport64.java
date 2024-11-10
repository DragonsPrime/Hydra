package com.pinecone.hydra.storage.volume.entity.local.simple.export;

import com.pinecone.hydra.storage.MiddleStorageObject;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.ExportStorageObject;
import com.pinecone.hydra.storage.volume.entity.local.physical.export.TitanDirectChannelExportEntity64;
import com.pinecone.hydra.storage.volume.entity.local.striped.StripLockEntity;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.concurrent.atomic.AtomicInteger;

public class TitanSimpleChannelExport64 implements SimpleChannelExport64{
    private VolumeManager           volumeManager;
    private ExportStorageObject     exportStorageObject;
    private FileChannel             channel;
    public TitanSimpleChannelExport64(SimpleChannelExportEntity entity){
        this.volumeManager       =  entity.getVolumeManager();
        this.exportStorageObject =  entity.getExportStorageObject();
        this.channel             =  entity.getChannel();
    }

    @Override
    public MiddleStorageObject export() throws IOException {
        TitanDirectChannelExportEntity64 titanDirectChannelExportEntity64 = new TitanDirectChannelExportEntity64(this.volumeManager, this.exportStorageObject,this.channel);
        return titanDirectChannelExportEntity64.export();
    }

    @Override
    public MiddleStorageObject raid0Export(byte[] buffer, Number offset, Number endSize, int jobCode, int jobNum, AtomicInteger counter, StripLockEntity lockEntity) throws IOException {
        TitanDirectChannelExportEntity64 titanDirectChannelExportEntity64 = new TitanDirectChannelExportEntity64(this.volumeManager, this.exportStorageObject,this.channel);
        return titanDirectChannelExportEntity64.raid0Export( buffer, offset, endSize, jobCode, jobNum, counter, lockEntity );
    }
}
