package com.pinecone.hydra.storage.volume.entity.local.striped.export;

import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.volume.entity.StripedVolume;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.sql.SQLException;

public interface StripedChannelExportEntity extends StripedExportEntity{
    FileChannel getChannel();
    void setChannel( FileChannel channel );
    StorageIOResponse export( ) throws IOException, SQLException;
    StripedVolume getStripedVolume();
}
