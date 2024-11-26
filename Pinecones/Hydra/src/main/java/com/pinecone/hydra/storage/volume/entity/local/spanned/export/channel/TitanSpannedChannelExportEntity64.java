package com.pinecone.hydra.storage.volume.entity.local.spanned.export.channel;

import com.pinecone.hydra.storage.KChannel;
import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.ArchExportEntity;
import com.pinecone.hydra.storage.StorageExportIORequest;
import com.pinecone.hydra.storage.volume.entity.SpannedVolume;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlock;

import java.io.IOException;
import java.sql.SQLException;

public class TitanSpannedChannelExportEntity64 extends ArchExportEntity implements SpannedChannelExportEntity64{
    private KChannel                    channel;
    private SpannedChannelExport64      spannedChannelExport64;
    public TitanSpannedChannelExportEntity64(VolumeManager volumeManager, StorageExportIORequest storageExportIORequest, KChannel channel, SpannedVolume spannedVolume) {
        super(volumeManager, storageExportIORequest);
        this.channel = channel;
        this.spannedChannelExport64 = new TitanSpannedChannelExport64( this, spannedVolume );
    }

    @Override
    public KChannel getChannel() {
        return this.channel;
    }

    @Override
    public void setChannel(KChannel channel) {
        this.channel = channel;
    }

    @Override
    public StorageIOResponse export() throws IOException, SQLException {
        return this.spannedChannelExport64.export();
    }

    @Override
    public StorageIOResponse export(CacheBlock cacheBlock, Number offset, Number endSize, byte[] buffer) throws IOException {
        return null;
    }
}
