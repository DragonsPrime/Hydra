package com.pinecone.hydra.storage.volume.entity.local.physical.export;

import com.pinecone.hydra.storage.MiddleStorageObject;
import com.pinecone.hydra.storage.volume.entity.local.striped.StripLockEntity;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.concurrent.atomic.AtomicInteger;

public interface DirectChannelExportEntity extends DirectExportEntity{
    FileChannel getChannel();

    void setChannel( FileChannel channel );

    MiddleStorageObject export() throws IOException;
    MiddleStorageObject raid0Export(byte[] buffer, Number offset, Number endSize, int jobCode, int jobNum, AtomicInteger counter, StripLockEntity lockEntity);
}
