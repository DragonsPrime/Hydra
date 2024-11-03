package com.pinecone.hydra.storage.volume.entity.local.spanned.receive;

import com.pinecone.hydra.storage.volume.entity.SpannedVolume;

import java.nio.channels.FileChannel;

public interface SpannedChannelReceiveEntity extends SpannedReceiveEntity{
    FileChannel getChannel();
    void setChannel( FileChannel channel );
    SpannedVolume getSpannedVolume();
}
