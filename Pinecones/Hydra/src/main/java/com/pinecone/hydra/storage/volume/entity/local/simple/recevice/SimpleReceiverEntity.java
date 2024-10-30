package com.pinecone.hydra.storage.volume.entity.local.simple.recevice;

import com.pinecone.hydra.storage.MiddleStorageObject;
import com.pinecone.hydra.storage.volume.VolumeFile;
import com.pinecone.hydra.storage.volume.VolumeTree;
import com.pinecone.hydra.storage.volume.entity.ReceiveEntity;

import java.io.IOException;
import java.sql.SQLException;

public interface SimpleReceiverEntity extends ReceiveEntity {
    MiddleStorageObject receive() throws IOException, SQLException;
    MiddleStorageObject receive( Number offset, Number endSize ) throws IOException;
}
