package com.pinecone.hydra.storage.volume.entity.local.spanned.export.stream;

import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.volume.entity.local.spanned.export.SpannedExport;

import java.io.IOException;
import java.sql.SQLException;

public interface SpannedStreamExport extends SpannedExport {
    StorageIOResponse export() throws IOException, SQLException;
}
