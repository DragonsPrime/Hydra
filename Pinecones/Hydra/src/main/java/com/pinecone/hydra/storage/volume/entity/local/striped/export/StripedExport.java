package com.pinecone.hydra.storage.volume.entity.local.striped.export;

import com.pinecone.hydra.storage.io.Chanface;
import com.pinecone.hydra.storage.RandomAccessChanface;
import com.pinecone.hydra.storage.StorageExportIORequest;
import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.Exporter;
import com.pinecone.hydra.storage.volume.entity.StripedVolume;

import java.io.IOException;
import java.sql.SQLException;

public interface StripedExport extends Exporter {
    StorageIOResponse export(Chanface chanface) throws IOException, SQLException;

    StorageIOResponse export( Chanface chanface,Number offset, Number endSize ) throws IOException, SQLException;

    StorageIOResponse export(RandomAccessChanface randomAccessChanface) throws IOException, SQLException;

    StorageIOResponse export( RandomAccessChanface randomAccessChanface,Number offset, Number endSize ) throws IOException, SQLException;

    VolumeManager getVolumeManager();

    StorageExportIORequest getStorageIORequest();

    Chanface getFileChannel();

    StripedVolume getStripedVolume();
}
