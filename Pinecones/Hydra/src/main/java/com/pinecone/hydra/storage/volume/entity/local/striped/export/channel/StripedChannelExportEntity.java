package com.pinecone.hydra.storage.volume.entity.local.striped.export.channel;

import com.pinecone.hydra.storage.Chanface;
import com.pinecone.hydra.storage.volume.entity.StripedVolume;
import com.pinecone.hydra.storage.volume.entity.local.striped.export.StripedExportEntity;

public interface StripedChannelExportEntity extends StripedExportEntity {
    Chanface getChannel();
    void setChannel( Chanface channel );
    StripedVolume getStripedVolume();
}
