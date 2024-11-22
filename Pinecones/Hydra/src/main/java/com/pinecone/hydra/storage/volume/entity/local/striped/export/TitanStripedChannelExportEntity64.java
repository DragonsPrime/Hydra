package com.pinecone.hydra.storage.volume.entity.local.striped.export;

import com.pinecone.hydra.storage.KChannel;
import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.ArchExportEntity;
import com.pinecone.hydra.storage.StorageExportIORequest;
import com.pinecone.hydra.storage.volume.entity.StripedVolume;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlock;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.sql.SQLException;

public class TitanStripedChannelExportEntity64 extends ArchExportEntity implements StripedChannelExportEntity64{
    private KChannel                 channel;
    private StripedChannelExport64      stripedChannelExport64;
    private StripedVolume               stripedVolume;
    public TitanStripedChannelExportEntity64(VolumeManager volumeManager, StorageExportIORequest storageExportIORequest, KChannel channel, StripedVolume stripedVolume) {
        super(volumeManager, storageExportIORequest);
        this.channel = channel;
        this.stripedVolume = stripedVolume;
        this.stripedChannelExport64 = new TitanStripedChannelExport64( this );
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
        return this.stripedChannelExport64.export();
    }

    @Override
    public StorageIOResponse export(CacheBlock cacheBlock, Number offset, Number endSize, byte[] buffer) throws IOException {
        return null;
    }


    @Override
    public StripedVolume getStripedVolume() {
        return this.stripedVolume;
    }
}
