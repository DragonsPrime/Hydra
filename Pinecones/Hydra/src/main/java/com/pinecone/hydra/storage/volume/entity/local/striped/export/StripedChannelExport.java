package com.pinecone.hydra.storage.volume.entity.local.striped.export;

import com.pinecone.hydra.storage.KChannel;
import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.StorageExportIORequest;
import com.pinecone.hydra.storage.volume.entity.StripedVolume;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.sql.SQLException;

public interface StripedChannelExport extends StripedExport{
    StorageIOResponse export() throws IOException, SQLException;

    StorageIOResponse export( Number offset, Number endSize ) throws IOException, SQLException;

    VolumeManager getVolumeManager();

    StorageExportIORequest getStorageIORequest();

    KChannel getFileChannel();

    StripedVolume getStripedVolume();
}
