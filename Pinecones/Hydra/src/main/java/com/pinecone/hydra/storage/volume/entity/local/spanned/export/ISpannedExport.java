package com.pinecone.hydra.storage.volume.entity.local.spanned.export;

import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.volume.entity.Exporter;

import java.io.IOException;
import java.sql.SQLException;

public interface ISpannedExport extends Exporter {
    StorageIOResponse export() throws IOException, SQLException;
}
