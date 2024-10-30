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


    void channelExport(KOMFileSystem fileSystem, FileNode file ) throws IOException;
    void streamExport( KOMFileSystem fileSystem, FileNode file ) throws IOException;
    void channelReceive(KOMFileSystem fileSystem, FileNode file, FileChannel channel) throws IOException;
    void channelReceive( KOMFileSystem fileSystem, FileNode file, FileChannel channel, Number offset, Number endSize ) throws IOException;
    void channelReceive(KOMFileSystem fileSystem, FileNode file, FileChannel channel, GUID frameGuid, int threadNum, int threadId) throws IOException;
    void streamReceive(KOMFileSystem fileSystem, FileNode file, InputStream inputStream) throws IOException;




    MiddleStorageObject channelReceive(VolumeTree volumeTree, ReceiveStorageObject receiveStorageObject, FileChannel channel, String destDirPath ) throws IOException, SQLException;
    MiddleStorageObject channelReceive(VolumeTree volumeTree, ReceiveStorageObject receiveStorageObject, FileChannel channel, String destDirPath, Number offset, Number endSize ) throws IOException;
    MiddleStorageObject channelExport( VolumeTree volumeTree, ExportStorageObject exportStorageObject, FileChannel channel ) throws IOException;

}
