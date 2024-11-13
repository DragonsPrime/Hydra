package com.pinecone.hydra.storage.volume.entity.local.physical.export;

import com.pinecone.hydra.storage.MiddleStorageObject;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlock;
import com.pinecone.hydra.storage.volume.entity.local.striped.StripExportFlyweightEntity;
import com.pinecone.hydra.storage.volume.entity.local.striped.StripLockEntity;
import com.pinecone.hydra.storage.volume.entity.local.striped.TerminalStateRecord;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public interface DirectChannelExport extends DirectExport{
    MiddleStorageObject export(DirectChannelExportEntity entity ) throws IOException;

    //MiddleStorageObject raid0Export(DirectChannelExportEntity entity, byte[] buffer, Number offset, Number endSize, StripExportFlyweightEntity flyweightEntity);
    MiddleStorageObject raid0Export(DirectChannelExportEntity entity, CacheBlock cacheBlock, Number offset, Number endSize, StripExportFlyweightEntity flyweightEntity);
}
