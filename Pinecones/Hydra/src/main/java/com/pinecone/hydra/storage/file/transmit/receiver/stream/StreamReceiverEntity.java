package com.pinecone.hydra.storage.file.transmit.receiver.stream;

import com.pinecone.hydra.storage.file.transmit.receiver.ReceiveEntity;

import java.io.IOException;
import java.io.InputStream;

public interface StreamReceiverEntity extends ReceiveEntity {
    InputStream getInputStream();
    void setInputStream( InputStream inputStream);
    void receive( ) throws IOException;

    @Override
    default StreamReceiverEntity evinceStreamReceiverEntity() {
        return this;
    }
}
