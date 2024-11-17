package com.pinecone.hydra.storage.volume.entity.local.physical.export;

import com.pinecone.hydra.storage.MiddleStorageObject;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.ArchExportEntity;
import com.pinecone.hydra.storage.volume.entity.ExportStorageObject;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlock;
import com.pinecone.hydra.storage.volume.entity.local.striped.StripExportFlyweightEntity;
import com.pinecone.hydra.storage.volume.entity.local.striped.StripLockEntity;
import com.pinecone.hydra.storage.volume.entity.local.striped.TerminalStateRecord;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class TitanDirectChannelExportEntity64 extends ArchExportEntity implements DirectChannelExportEntity64{
    private FileChannel             channel;
    private DirectChannelExport     channelExporter;

    public TitanDirectChannelExportEntity64(VolumeManager volumeManager, ExportStorageObject exportStorageObject, FileChannel channel) {
        super(volumeManager, exportStorageObject);
        this.channel = channel;
        this.channelExporter = new TitanDirectChannelExport64();

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
    public MiddleStorageObject export() throws IOException {
        return this.channelExporter.export( this );
    }

    @Override
    public MiddleStorageObject raid0Export(CacheBlock cacheBlock, Number offset, Number endSize, byte[] buffer) {
        return this.channelExporter.raid0Export( this, cacheBlock, offset, endSize, buffer );
    }

}
