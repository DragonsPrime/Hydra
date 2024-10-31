package com.pinecone.hydra.storage.volume.entity.local.spanned.export;

import com.pinecone.hydra.storage.MiddleStorageObject;

import java.io.IOException;
import java.sql.SQLException;

public interface SpannedChannelExport extends SpannedExport{
    MiddleStorageObject export() throws IOException, SQLException;
}
