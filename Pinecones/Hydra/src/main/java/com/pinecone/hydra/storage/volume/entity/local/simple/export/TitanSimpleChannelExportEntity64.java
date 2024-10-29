package com.pinecone.hydra.storage.volume.entity.local.simple.export;

import com.pinecone.hydra.storage.volume.VolumeTree;
import com.pinecone.hydra.storage.volume.entity.ArchExportEntity;
import com.pinecone.hydra.storage.volume.entity.ExportStorageObject;

import java.io.IOException;
import java.nio.channels.FileChannel;

public class TitanSimpleChannelExportEntity64 extends ArchExportEntity implements SimpleChannelExportEntity64{
    private FileChannel                 channel;
    private SimpleChannelExport64       simpleChannelExport64;
    public TitanSimpleChannelExportEntity64(VolumeTree volumeTree, ExportStorageObject exportStorageObject) {
        super(volumeTree, exportStorageObject);
        this.simpleChannelExport64 = new TitanSimpleChannelExport64( this );
    }


    @Override
    public FileChannel getChannel() {
        return this.channel;
    }

    @Override
    public void setChannel(FileChannel channel) {
        this.channel = channel;
    }

    @Override
    public void export() throws IOException {
        this.simpleChannelExport64.export();
    }
}
