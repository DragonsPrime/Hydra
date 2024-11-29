package com.pinecone.hydra.storage.file.transmit.receiver;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.hydra.storage.KChannel;
import com.pinecone.hydra.storage.file.KOMFileSystem;
import com.pinecone.hydra.storage.file.entity.FileNode;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.LogicVolume;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

public interface FileReceiveEntity extends Pinenut {
    KOMFileSystem getFileSystem();
    void setFileSystem( KOMFileSystem fileSystem );

    String getDestDirPath();
    void setDestDirPath( String destDirPath );

    FileNode getFile();
    void setFile( FileNode file );

    KChannel getChannel();
    void setChannel( KChannel channel );

    VolumeManager getVolumeManager();
    void setVolumeManager( VolumeManager volumeManager );

    void receive( LogicVolume volume ) throws IOException, SQLException, InvocationTargetException, InstantiationException, IllegalAccessException;
    void receive( LogicVolume volume, Number offset, Number endSize )throws IOException;
}
