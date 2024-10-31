package com.pinecone.hydra.storage.volume.entity.local.spanned.export;

import com.pinecone.hydra.storage.MiddleStorageObject;
import com.pinecone.hydra.storage.volume.VolumeTree;
import com.pinecone.hydra.storage.volume.entity.ArchExportEntity;
import com.pinecone.hydra.storage.volume.entity.ExportStorageObject;
import com.pinecone.hydra.storage.volume.entity.SpannedVolume;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.sql.SQLException;

public class TitanSpannedChannelExportEntity64 extends ArchExportEntity implements SpannedChannelExportEntity64{
    private FileChannel                 channel;
    private SpannedChannelExport64      spannedChannelExport64;
    public TitanSpannedChannelExportEntity64(VolumeTree volumeTree, ExportStorageObject exportStorageObject, FileChannel channel, SpannedVolume spannedVolume) {
        super(volumeTree, exportStorageObject);
        this.channel = channel;
        this.spannedChannelExport64 = new TitanSpannedChannelExport64( this, spannedVolume );
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
        return this.spannedChannelExport64.export();
    }
}
