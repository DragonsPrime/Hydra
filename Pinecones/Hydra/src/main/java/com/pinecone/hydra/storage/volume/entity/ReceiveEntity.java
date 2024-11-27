package com.pinecone.hydra.storage.volume.entity;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.hydra.storage.KChannel;
import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.StorageReceiveIORequest;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlock;

import java.io.IOException;
import java.sql.SQLException;

public interface ReceiveEntity extends Pinenut {
    VolumeManager getVolumeManager();
    void setVolumeManager(VolumeManager volumeManager);

    StorageReceiveIORequest getReceiveStorageObject();
    void setReceiveStorageObject( StorageReceiveIORequest storageReceiveIORequest);

    KChannel getKChannel();
    void setKChannel( KChannel channel);

    StorageIOResponse receive() throws IOException, SQLException;

    StorageIOResponse receive(Number offset, Number endSize ) throws IOException, SQLException;

    StorageIOResponse receive(CacheBlock cacheBlock, byte[] buffer ) throws IOException, SQLException;

}
