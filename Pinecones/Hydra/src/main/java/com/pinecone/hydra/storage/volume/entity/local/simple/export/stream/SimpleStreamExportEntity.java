package com.pinecone.hydra.storage.volume.entity.local.simple.export.stream;

import com.pinecone.hydra.storage.volume.entity.local.simple.export.SimpleExporterEntity;

import java.io.OutputStream;

public interface SimpleStreamExportEntity extends SimpleExporterEntity {
    OutputStream getStream();
    void setStream( OutputStream stream );


}
