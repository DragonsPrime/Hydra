package com.pinecone.hydra.storage.volume.entity.local.spanned.receive;

import com.pinecone.hydra.storage.MiddleStorageObject;
import com.pinecone.hydra.storage.volume.VolumeTree;
import com.pinecone.hydra.storage.volume.entity.ReceiveStorageObject;
import com.pinecone.hydra.storage.volume.entity.SpannedVolume;
import com.pinecone.hydra.storage.volume.entity.local.simple.recevice.SimpleChannelReceiverEntity;

import java.io.IOException;
import java.nio.channels.FileChannel;

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
    public MiddleStorageObject receive(SimpleChannelReceiverEntity entity) throws IOException {

        return null;
    }

    @Override
    public MiddleStorageObject receive(SimpleChannelReceiverEntity entity, Number offset, Number endSize) throws IOException {
        return null;
    }
}
