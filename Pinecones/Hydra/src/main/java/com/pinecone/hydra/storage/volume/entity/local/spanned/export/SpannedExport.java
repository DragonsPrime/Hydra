package com.pinecone.hydra.storage.volume.entity.local.spanned.export;

import com.pinecone.hydra.storage.io.Chanface;
import com.pinecone.hydra.storage.RandomAccessChanface;
import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.volume.entity.Exporter;

import java.io.IOException;
import java.sql.SQLException;

public interface SpannedExport extends Exporter {
    StorageIOResponse export(Chanface chanface) throws IOException, SQLException;

    StorageIOResponse export(RandomAccessChanface randomAccessChanface) throws IOException, SQLException;
}
