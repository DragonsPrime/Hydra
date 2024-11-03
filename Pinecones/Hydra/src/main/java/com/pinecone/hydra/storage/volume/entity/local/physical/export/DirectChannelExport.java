package com.pinecone.hydra.storage.volume.entity.local.physical.export;

import com.pinecone.hydra.storage.MiddleStorageObject;

import java.io.IOException;

public interface DirectChannelExport extends DirectExport{
    MiddleStorageObject export(DirectChannelExportEntity entity ) throws IOException;
}
