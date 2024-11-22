package com.pinecone.hydra.storage.volume.entity.local.spanned.receive;

import com.pinecone.hydra.storage.KChannel;
import com.pinecone.hydra.storage.volume.entity.SpannedVolume;

import java.nio.channels.FileChannel;

public interface SpannedChannelReceiveEntity extends SpannedReceiveEntity{
    KChannel getChannel();
    void setChannel( KChannel channel );
    SpannedVolume getSpannedVolume();
}
