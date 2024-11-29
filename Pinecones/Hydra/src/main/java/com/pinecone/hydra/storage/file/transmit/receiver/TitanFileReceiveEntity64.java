package com.pinecone.hydra.storage.file.transmit.receiver;

import com.pinecone.hydra.storage.KChannel;
import com.pinecone.hydra.storage.file.KOMFileSystem;
import com.pinecone.hydra.storage.file.entity.FileNode;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.LogicVolume;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

public class TitanFileReceiveEntity64 extends ArchFileReceiveEntity  implements FileReceiveEntity64{
    protected FileReceive fileReceive;

    public TitanFileReceiveEntity64(KOMFileSystem fileSystem, String destDirPath, FileNode file, KChannel channel, VolumeManager volumeManager) {
        super(fileSystem, destDirPath, file, channel, volumeManager);
        this.fileReceive = new TitanFileReceive64( this );
    }


    @Override
    public void receive(LogicVolume volume) throws IOException, SQLException, InvocationTargetException, InstantiationException, IllegalAccessException {
        this.fileSystem.affirmFileNode( this.destDirPath );
        this.fileReceive.receive( volume );
    }

    @Override
    public void receive(LogicVolume volume, Number offset, Number endSize) throws IOException {
        this.fileSystem.affirmFileNode( this.destDirPath );
        this.fileReceive.receive( volume, offset, endSize );
    }


}
