package com.pinecone.hydra.storage.volume.entity;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.file.KOMFileSystem;
import com.pinecone.hydra.storage.file.entity.FileNode;
import com.pinecone.hydra.storage.volume.VolumeTree;
import com.pinecone.hydra.unit.udtt.entity.TreeNode;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.util.List;

public interface LogicVolume extends Volume, TreeNode {
    String getName();

    void setName( String name );

    List<LogicVolume> getChildren();

    void setChildren( List<LogicVolume> children );

    VolumeCapacity getVolumeCapacity();

    void setVolumeCapacity( VolumeCapacity volumeCapacity );

    void extendLogicalVolume( GUID physicalGuid );
    List< GUID > listPhysicalVolume();

    void channelExport( KOMFileSystem fileSystem, FileNode file ) throws IOException;
    void streamExport( KOMFileSystem fileSystem, FileNode file ) throws IOException;
    void channelReceive(KOMFileSystem fileSystem, FileNode file, FileChannel channel) throws IOException;
    void channelReceive( KOMFileSystem fileSystem, FileNode file, FileChannel channel, Number offset, Number endSize ) throws IOException;
    void streamReceive(KOMFileSystem fileSystem, FileNode file, InputStream inputStream) throws IOException;

    default MirroredVolume evinceMirroredVolume(){
        return null;
    }
    default SimpleVolume   evinceSimpleVolume(){
        return null;
    }
    default SpannedVolume  evinceSpannedVolume(){
        return null;
    }
    default StripedVolume  evinceStripeVolume(){
        return null;
    }
    void setVolumeTree( VolumeTree volumeTree );
}
