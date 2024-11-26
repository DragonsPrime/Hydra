package com.pinecone.hydra.storage.volume.entity.local.spanned.export.stream;

import com.pinecone.hydra.storage.volume.entity.local.spanned.export.SpannedExportEntity;

import java.io.OutputStream;

public interface SpannedStreamExportEntity extends SpannedExportEntity {
    OutputStream  getStream();
    void setStream( OutputStream stream );
}
