package com.pinecone.hydra.storage.volume.entity.local.striped.export;

import com.pinecone.hydra.storage.MiddleStorageObject;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.ArchExportEntity;
import com.pinecone.hydra.storage.volume.entity.ExportStorageObject;
import com.pinecone.hydra.storage.volume.entity.StripedVolume;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.sql.SQLException;

public class TitanStripedChannelExportEntity64 extends ArchExportEntity implements StripedChannelExportEntity64{
    private FileChannel                 channel;
    private StripedChannelExport64      stripedChannelExport64;
    private StripedVolume               stripedVolume;
    public TitanStripedChannelExportEntity64(VolumeManager volumeManager, ExportStorageObject exportStorageObject, FileChannel channel, StripedVolume stripedVolume) {
        super(volumeManager, exportStorageObject);
        this.channel = channel;
        this.stripedVolume = stripedVolume;
        this.stripedChannelExport64 = new TitanStripedChannelExport64( this, this.stripedVolume );
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
    public MiddleStorageObject export() throws IOException, SQLException {
        return this.stripedChannelExport64.export();
    }
}
