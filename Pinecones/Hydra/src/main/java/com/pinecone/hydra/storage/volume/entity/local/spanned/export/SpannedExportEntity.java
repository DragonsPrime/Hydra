package com.pinecone.hydra.storage.volume.entity.local.spanned.export;

import com.pinecone.hydra.storage.volume.entity.ExporterEntity;
import com.pinecone.hydra.storage.volume.entity.SpannedVolume;

public interface SpannedExportEntity extends ExporterEntity {
    SpannedVolume getSpannedVolume();
}
