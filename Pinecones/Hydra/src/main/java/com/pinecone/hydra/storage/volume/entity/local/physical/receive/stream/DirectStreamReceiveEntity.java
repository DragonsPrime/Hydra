package com.pinecone.hydra.storage.volume.entity.local.physical.receive.stream;

import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.volume.entity.ReceiveEntity;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlock;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

public interface DirectStreamReceiveEntity extends ReceiveEntity {
    InputStream getStream();
    void setStream( InputStream stream );

    String getDestDirPath();
    void setDestDirPath( String destDirPath );


}
