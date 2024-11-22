package com.pinecone.hydra.storage.file.transmit.receiver;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.hydra.storage.file.KOMFileSystem;
import com.pinecone.hydra.storage.file.entity.FileNode;
import com.pinecone.hydra.storage.file.transmit.receiver.channel.ChannelReceiverEntity;
import com.pinecone.hydra.storage.file.transmit.receiver.stream.StreamReceiverEntity;
import com.pinecone.hydra.storage.volume.entity.LogicVolume;

import java.io.IOException;
import java.sql.SQLException;

public interface ReceiveEntity extends Pinenut {
    KOMFileSystem getFileSystem();
    void setFileSystem( KOMFileSystem fileSystem );

    String getDestDirPath();
    void setDestDirPath( String destDirPath );

    FileNode getFile();
    void setFile( FileNode file );

    default ChannelReceiverEntity evinceChannelReceiverEntity(){
        return null;
    }
    default StreamReceiverEntity evinceStreamReceiverEntity(){
        return null;
    }

    void receive(LogicVolume volume ) throws IOException, SQLException;
    void receive( Number offset, Number endSize )throws IOException;


}
