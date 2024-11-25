package com.pinecone.hydra.storage.volume.entity.local.simple.recevice.stream;

import com.pinecone.hydra.storage.KChannel;
import com.pinecone.hydra.storage.volume.entity.SimpleVolume;
import com.pinecone.hydra.storage.volume.entity.local.simple.recevice.SimpleReceiverEntity;

import java.io.InputStream;

public interface SimpleStreamReceiveEntity extends SimpleReceiverEntity {
    InputStream getStream();
    void setStream( InputStream stream );

    SimpleVolume getSimpleVolume();
}
