package com.pinecone.hydra.storage.volume.entity.local.spanned.export;

import com.pinecone.hydra.storage.KChannel;
import com.pinecone.hydra.storage.StorageIOResponse;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.sql.SQLException;

public interface SpannedChannelExportEntity extends SpannedExportEntity{
    KChannel getChannel();
    void setChannel( KChannel channel );
}
