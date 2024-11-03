package com.pinecone.hydra.storage.volume.entity.local.simple.export;

import com.pinecone.hydra.storage.MiddleStorageObject;

import java.io.IOException;

public interface SimpleChannelExport extends SimpleExporter{

    MiddleStorageObject export() throws IOException;
}
