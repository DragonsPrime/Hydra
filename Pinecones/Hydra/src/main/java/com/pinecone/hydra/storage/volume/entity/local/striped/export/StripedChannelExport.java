package com.pinecone.hydra.storage.volume.entity.local.striped.export;

import com.pinecone.hydra.storage.MiddleStorageObject;

import java.io.IOException;
import java.sql.SQLException;

public interface StripedChannelExport extends StripedExport{
    MiddleStorageObject export() throws IOException, SQLException;
}
