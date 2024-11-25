package com.pinecone.hydra.storage.volume.entity.local.striped.receive.stream;

import com.pinecone.hydra.storage.volume.entity.StripedVolume;
import com.pinecone.hydra.storage.volume.entity.local.striped.receive.StripedReceiverEntity;

import java.io.InputStream;

public interface StripedStreamReceiveEntity extends StripedReceiverEntity {
    InputStream getStream();
    void setStream( InputStream stream );
    StripedVolume getStripedVolume();
}
