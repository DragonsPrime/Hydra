package com.pinecone.hydra.storage.volume.entity.local.simple.export;

import com.pinecone.hydra.storage.MiddleStorageObject;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlock;
import com.pinecone.hydra.storage.volume.entity.local.striped.StripExportFlyweightEntity;
import com.pinecone.hydra.storage.volume.entity.local.striped.StripLockEntity;
import com.pinecone.hydra.storage.volume.entity.local.striped.TerminalStateRecord;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public interface SimpleChannelExport extends SimpleExporter{

    MiddleStorageObject export() throws IOException;
    MiddleStorageObject raid0Export(CacheBlock cacheBlock, Number offset, Number endSize, StripExportFlyweightEntity flyweightEntity ) throws IOException;
}
