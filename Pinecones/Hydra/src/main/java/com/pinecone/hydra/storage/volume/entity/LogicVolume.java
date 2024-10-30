package com.pinecone.hydra.storage.volume.entity;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.MiddleStorageObject;
import com.pinecone.hydra.storage.file.KOMFileSystem;
import com.pinecone.hydra.storage.file.entity.FileNode;
import com.pinecone.hydra.storage.volume.VolumeTree;
import com.pinecone.hydra.unit.udtt.entity.TreeNode;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.sql.SQLException;
import java.util.List;

public interface LogicVolume extends Volume, TreeNode {
    String getName();

    void setName( String name );

    List<LogicVolume> getChildren();

    void setChildren( List<LogicVolume> children );

    VolumeCapacity64 getVolumeCapacity();

    void setVolumeCapacity( VolumeCapacity64 volumeCapacity );

    void extendLogicalVolume( GUID physicalGuid );
    List< GUID > listPhysicalVolume();

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


    MiddleStorageObject channelReceive( ReceiveStorageObject receiveStorageObject, String destDirPath, FileChannel channel ) throws IOException, SQLException;
    MiddleStorageObject channelReceive( ReceiveStorageObject receiveStorageObject, String destDirPath, FileChannel channel, Number offset, Number endSize ) throws IOException, SQLException;
    MiddleStorageObject channelExport( ExportStorageObject exportStorageObject, FileChannel channel ) throws IOException;
}
