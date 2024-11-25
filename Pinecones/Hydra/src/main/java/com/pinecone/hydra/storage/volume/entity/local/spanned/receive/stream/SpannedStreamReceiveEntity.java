package com.pinecone.hydra.storage.volume.entity.local.spanned.receive.stream;

import com.pinecone.hydra.storage.volume.entity.SpannedVolume;
import com.pinecone.hydra.storage.volume.entity.local.spanned.receive.SpannedReceiveEntity;

import java.io.InputStream;

public interface SpannedStreamReceiveEntity extends SpannedReceiveEntity {
    InputStream getStream();
    void setStream( InputStream stream );
    SpannedVolume getSpannedVolume();
}
