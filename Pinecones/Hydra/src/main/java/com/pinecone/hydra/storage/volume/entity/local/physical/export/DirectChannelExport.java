package com.pinecone.hydra.storage.volume.entity.local.physical.export;

import com.pinecone.hydra.storage.MiddleStorageObject;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public interface DirectChannelExport extends DirectExport{
    MiddleStorageObject export(DirectChannelExportEntity entity ) throws IOException;
    MiddleStorageObject raid0Export(DirectChannelExportEntity entity, byte[] buffer, Number offset, Number endSize, int jobCode, int jobNum, AtomicInteger counter);
}
