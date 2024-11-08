package com.pinecone.hydra.storage.volume.entity.local.striped.export;

import com.pinecone.hydra.storage.MiddleStorageObject;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.sql.SQLException;

public interface StripedChannelExportEntity extends StripedExportEntity{
    FileChannel getChannel();
    void setChannel( FileChannel channel );
    MiddleStorageObject export( ) throws IOException, SQLException;
}
