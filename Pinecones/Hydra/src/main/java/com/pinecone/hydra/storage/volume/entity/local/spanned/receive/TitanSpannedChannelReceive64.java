package com.pinecone.hydra.storage.volume.entity.local.spanned.receive;

import com.pinecone.hydra.storage.MiddleStorageObject;
import com.pinecone.hydra.storage.volume.VolumeTree;
import com.pinecone.hydra.storage.volume.entity.LogicVolume;
import com.pinecone.hydra.storage.volume.entity.ReceiveStorageObject;
import com.pinecone.hydra.storage.volume.entity.SpannedVolume;
import com.pinecone.hydra.storage.volume.entity.VolumeCapacity64;
import com.pinecone.hydra.storage.volume.entity.local.simple.recevice.SimpleChannelReceiverEntity;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.sql.SQLException;
import java.util.List;

public class TitanSpannedChannelReceive64 implements SpannedChannelReceive64{
    private SpannedVolume           spannedVolume;
    private FileChannel             channel;
    private VolumeTree              volumeTree;
    private ReceiveStorageObject    receiveStorageObject;
    private String                  destDirPath;

    public TitanSpannedChannelReceive64( SpannedChannelReceiveEntity entity ){
        this.volumeTree = entity.getVolumeTree();
        this.spannedVolume = entity.getSpannedVolume();
        this.channel = entity.getChannel();
        this.receiveStorageObject = entity.getReceiveStorageObject();
        this.destDirPath = entity.getDestDirPath();
    }
    @Override
    public MiddleStorageObject receive() throws IOException, SQLException {
        List<LogicVolume> volumes = this.spannedVolume.getChildren();
        for( LogicVolume volume : volumes ){
            if ( this.freeSpace( volume ) > receiveStorageObject.getSize().longValue() ){
                return volume.channelReceive(this.receiveStorageObject, this.destDirPath, this.channel);
            }
        }
        return null;
    }

    @Override
    public MiddleStorageObject receive( Number offset, Number endSize ) throws IOException, SQLException {
        List<LogicVolume> volumes = this.spannedVolume.getChildren();
        for( LogicVolume volume : volumes ){
            if ( this.freeSpace( volume ) > receiveStorageObject.getSize().longValue() ){
                return volume.channelReceive(this.receiveStorageObject, this.destDirPath, this.channel, offset, endSize);
            }
        }
        return null;
    }

    private long freeSpace( LogicVolume volume ){
        VolumeCapacity64 volumeCapacity = volume.getVolumeCapacity();
        return volumeCapacity.getDefinitionCapacity() - volumeCapacity.getUsedSize();
    }
}
