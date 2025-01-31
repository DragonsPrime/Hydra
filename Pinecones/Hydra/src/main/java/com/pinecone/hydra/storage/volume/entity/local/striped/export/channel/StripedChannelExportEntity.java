package com.pinecone.hydra.storage.volume.entity.local.striped.export.channel;

import com.pinecone.hydra.storage.io.Chanface;
import com.pinecone.hydra.storage.volume.entity.ExporterEntity;
import com.pinecone.hydra.storage.volume.entity.StripedVolume;

public interface StripedChannelExportEntity extends ExporterEntity {
    Chanface getChannel();
    void setChannel( Chanface channel );
    StripedVolume getStripedVolume();
}
