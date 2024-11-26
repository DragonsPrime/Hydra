package com.pinecone.hydra.storage.volume.entity.local.spanned.export.channel;

import com.pinecone.hydra.storage.KChannel;
import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.volume.entity.local.spanned.export.SpannedExportEntity;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.sql.SQLException;

public interface SpannedChannelExportEntity extends SpannedExportEntity {
    KChannel getChannel();
    void setChannel( KChannel channel );
}
