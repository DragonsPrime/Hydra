package com.pinecone.hydra.storage.volume.entity.local.physical.export.channel;

import com.pinecone.hydra.storage.KChannel;
import com.pinecone.hydra.storage.volume.entity.local.physical.export.DirectExportEntity;

public interface DirectChannelExportEntity extends DirectExportEntity {
    KChannel getChannel();

    void setChannel( KChannel channel );


}
