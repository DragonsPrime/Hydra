package com.pinecone.hydra.storage.volume.entity.local.simple.export;

import com.pinecone.hydra.storage.MiddleStorageObject;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlock;
import com.pinecone.hydra.storage.volume.entity.local.striped.StripExportFlyweightEntity;
import com.pinecone.hydra.storage.volume.entity.local.striped.StripLockEntity;
import com.pinecone.hydra.storage.volume.entity.local.striped.TerminalStateRecord;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public interface SimpleChannelExportEntity extends SimpleExporterEntity{
    FileChannel getChannel();
    void setChannel( FileChannel channel );
    MiddleStorageObject export( ) throws IOException;
    MiddleStorageObject raid0Export(CacheBlock cacheBlock, Number offset, Number endSize, byte[] buffer) throws IOException;
}
