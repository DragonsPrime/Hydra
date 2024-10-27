package com.pinecone.hydra.storage.file;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.system.ko.KernelObjectConfig;

public interface FileSystemConfig extends KernelObjectConfig {
    String getVersionSignature();

    Number getFrameSize();

    GUID getLocalhostGUID();


    // TODO 删掉
    long stripSize1 = 1024 * 1024;
}
