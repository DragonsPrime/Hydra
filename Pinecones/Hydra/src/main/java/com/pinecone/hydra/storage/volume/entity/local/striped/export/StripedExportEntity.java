package com.pinecone.hydra.storage.volume.entity.local.striped.export;

import com.pinecone.hydra.storage.volume.entity.ExporterEntity;
import com.pinecone.hydra.storage.volume.entity.StripedVolume;

public interface StripedExportEntity extends ExporterEntity {
    StripedVolume getStripedVolume();
}
