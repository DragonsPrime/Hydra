package com.pinecone.hydra.storage.volume.entity;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.MiddleStorageObject;
import com.pinecone.hydra.storage.file.KOMFileSystem;
import com.pinecone.hydra.storage.file.entity.FileNode;
import com.pinecone.hydra.storage.volume.VolumeTree;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.sql.SQLException;

public interface PhysicalVolume extends Volume{
    MountPoint getMountPoint();
    void setMountPoint( MountPoint mountPoint );

    MiddleStorageObject channelReceive(VolumeTree volumeTree, ReceiveStorageObject receiveStorageObject, FileChannel channel, String destDirPath ) throws IOException, SQLException;
    MiddleStorageObject channelReceive(VolumeTree volumeTree, ReceiveStorageObject receiveStorageObject, FileChannel channel, String destDirPath, Number offset, Number endSize ) throws IOException;
    MiddleStorageObject channelExport( VolumeTree volumeTree, ExportStorageObject exportStorageObject, FileChannel channel ) throws IOException;

}
