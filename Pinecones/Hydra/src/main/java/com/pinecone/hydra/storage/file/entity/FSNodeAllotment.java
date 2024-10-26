package com.pinecone.hydra.storage.file.entity;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.file.transmit.receiver.channel.ChannelReceiverEntity;

public interface FSNodeAllotment extends Pinenut {
     Folder              newFolder();
     Folder              newFolder( String name );

     FileNode            newFileNode();
     FileNode            newFileNode( String name, long definitionSize, boolean crc32Xor, boolean integrityCheckEnable, boolean disableCluster);
     FileNode            newFileNode( String name, long definitionSize );
     FileNode            newFileNode( String name, boolean crc32Xor, boolean integrityCheckEnable, boolean disableCluster);


     LocalFrame          newLocalFrame();
     LocalFrame          newLocalFrame( GUID fileGuid, int segId, String sourceName, String crc32, long size, long fileStartOffset );
     LocalFrame          newLocalFrame( GUID fileGuid, int segId, String sourceName );

     RemoteFrame         newRemoteFrame();
     RemoteFrame         newRemoteFrame( GUID fileGuid, int segId, String crc32, long size );
     RemoteFrame         newRemoteFrame( GUID fileGuid, int segId );
     Symbolic            newSymbolic();
     SymbolicMeta        newSymbolicMeta();
     ChannelReceiverEntity newChannelReceiverEntity();

     Strip  newStrip();
}
