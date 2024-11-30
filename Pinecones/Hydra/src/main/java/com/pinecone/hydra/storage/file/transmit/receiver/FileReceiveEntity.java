package com.pinecone.hydra.storage.file.transmit.receiver;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.hydra.storage.Chanface;
import com.pinecone.hydra.storage.file.KOMFileSystem;
import com.pinecone.hydra.storage.file.entity.FileNode;
import com.pinecone.hydra.storage.volume.VolumeManager;

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

    Chanface getChannel();
    void setChannel( Chanface channel );

    VolumeManager getVolumeManager();
    void setVolumeManager( VolumeManager volumeManager );

    void receive() throws IOException, SQLException, InvocationTargetException, InstantiationException, IllegalAccessException;
    void receive( Number offset, Number endSize )throws IOException;
}
