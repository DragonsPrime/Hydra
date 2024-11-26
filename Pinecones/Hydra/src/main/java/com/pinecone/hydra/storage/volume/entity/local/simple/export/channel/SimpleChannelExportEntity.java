package com.pinecone.hydra.storage.volume.entity.local.simple.export.channel;

import com.pinecone.hydra.storage.KChannel;
import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.volume.entity.local.simple.export.SimpleExporterEntity;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlock;

import java.io.IOException;
import java.nio.channels.FileChannel;

public interface SimpleChannelExportEntity extends SimpleExporterEntity {
    KChannel getChannel();
    void setChannel( KChannel channel );
}
