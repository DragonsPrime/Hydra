package com.pinecone.hydra.storage.volume.entity.local.striped.receive;

import com.pinecone.hydra.storage.MiddleStorageObject;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.LogicVolume;
import com.pinecone.hydra.storage.volume.entity.ReceiveStorageObject;
import com.pinecone.hydra.storage.volume.entity.StripedVolume;
import com.pinecone.hydra.storage.volume.runtime.MasterVolumeGram;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.sql.SQLException;
import java.util.List;

public class TitanStripedChannelReceiver64 implements StripedChannelReceiver64{
    private FileChannel                 fileChannel;
    private VolumeManager               volumeManager;
    private ReceiveStorageObject        receiveStorageObject;
    private String                      destDirPath;
    private StripedVolume               stripedVolume;

    public TitanStripedChannelReceiver64( StripedChannelReceiverEntity entity ){
        this.fileChannel   = entity.getChannel();
        this.volumeManager = entity.getVolumeManager();
        this.receiveStorageObject = entity.getReceiveStorageObject();
        this.destDirPath = entity.getDestDirPath();
        this.stripedVolume = entity.getStripedVolume();
    }

    @Override
    public MiddleStorageObject channelReceive() throws IOException, SQLException {
        //MasterVolumeGram masterVolumeGram = new MasterVolumeGram( this.stripedVolume.getGuid().toString(), this.volumeManager );
        List<LogicVolume> volumes = this.stripedVolume.getChildren();
        int index = 0;
        for( LogicVolume volume : volumes ){

        }
        return null;
    }

    @Override
    public MiddleStorageObject channelReceive(Number offset, Number endSize) throws IOException {
        return null;
    }
}
