package com.pinecone.hydra.storage.volume.entity.local.simple.export;

import com.pinecone.hydra.storage.MiddleStorageObject;
import com.pinecone.hydra.storage.volume.entity.local.striped.StripLockEntity;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public interface SimpleChannelExport extends SimpleExporter{

    MiddleStorageObject export() throws IOException;
    MiddleStorageObject raid0Export(byte[] buffer, Number offset, Number endSize, int jobCode, int jobNum, AtomicInteger counter, StripLockEntity lockEntity) throws IOException;
}
