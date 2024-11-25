package com.pinecone.hydra.storage.volume.entity.local.physical.export.stream;

import com.pinecone.hydra.storage.volume.entity.local.physical.export.DirectExportEntity;

import java.io.OutputStream;

public interface DirectStreamExportEntity extends DirectExportEntity {
    OutputStream getStream();
    void setStream( OutputStream stream );
}
