package com.pinecone.hydra.storage.volume.entity.local.striped.export;

import com.pinecone.hydra.storage.MiddleStorageObject;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.ExportStorageObject;
import com.pinecone.hydra.storage.volume.entity.StripedVolume;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.sql.SQLException;

public interface StripedChannelExport extends StripedExport{
    MiddleStorageObject export() throws IOException, SQLException;

    VolumeManager getVolumeManager();

    ExportStorageObject getExportStorageObject();

    FileChannel getFileChannel();

    StripedVolume getStripedVolume();
}
