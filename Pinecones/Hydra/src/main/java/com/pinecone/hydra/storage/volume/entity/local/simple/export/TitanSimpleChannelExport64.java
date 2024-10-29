package com.pinecone.hydra.storage.volume.entity.local.simple.export;

import com.pinecone.hydra.storage.MiddleStorageObject;
import com.pinecone.hydra.storage.volume.VolumeTree;
import com.pinecone.hydra.storage.volume.entity.ExportStorageObject;
import com.pinecone.hydra.storage.volume.entity.local.physical.export.TitanDirectChannelExportEntity64;

import java.io.IOException;
import java.nio.channels.FileChannel;

public class TitanSimpleChannelExport64 implements SimpleChannelExport64{
    private VolumeTree              volumeTree;
    private ExportStorageObject     exportStorageObject;
    private FileChannel             channel;
    public TitanSimpleChannelExport64(SimpleChannelExportEntity entity){
        this.volumeTree          =  entity.getVolumeTree();
        this.exportStorageObject =  entity.getExportStorageObject();
        this.channel             =  entity.getChannel();
    }

    @Override
    public MiddleStorageObject export() throws IOException {
        TitanDirectChannelExportEntity64 titanDirectChannelExportEntity64 = new TitanDirectChannelExportEntity64(this.volumeTree, this.exportStorageObject,this.channel);
        return titanDirectChannelExportEntity64.export();
    }
}
