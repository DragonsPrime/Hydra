package com.pinecone.hydra.storage.volume.entity.local.spanned.export;

import com.pinecone.hydra.storage.StorageIOResponse;

import java.io.IOException;
import java.sql.SQLException;

public interface SpannedChannelExport extends SpannedExport{
    StorageIOResponse export() throws IOException, SQLException;
}
