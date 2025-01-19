package com.pinecone.hydra.storage.volume.entity.local.simple.export;

import com.pinecone.hydra.storage.volume.entity.ExporterEntity;
import com.pinecone.hydra.storage.volume.entity.SimpleVolume;

public interface SimpleExportEntity extends ExporterEntity {
    SimpleVolume getSimpleVolume();
}
