package com.pinecone.hydra.storage.volume.entity.local.simple.export;

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

public class TitanSimpleChannelExportEntity64 extends ArchExportEntity implements SimpleChannelExportEntity64{
    private FileChannel                 channel;
    private SimpleChannelExport64       simpleChannelExport64;
    public TitanSimpleChannelExportEntity64(VolumeManager volumeManager, ExportStorageObject exportStorageObject, FileChannel channel) {
        super(volumeManager, exportStorageObject);
        this.channel = channel;
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
    public MiddleStorageObject export() throws IOException {
        return this.simpleChannelExport64.export();
    }

    @Override
    public MiddleStorageObject raid0Export(CacheBlock cacheBlock, Number offset, Number endSize, byte[] buffer) throws IOException {
        return this.simpleChannelExport64.raid0Export( cacheBlock, offset, endSize, buffer );

    }
}
