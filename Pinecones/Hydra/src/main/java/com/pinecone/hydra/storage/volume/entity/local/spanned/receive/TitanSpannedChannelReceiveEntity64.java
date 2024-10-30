package com.pinecone.hydra.storage.volume.entity.local.spanned.receive;

import com.pinecone.hydra.storage.MiddleStorageObject;
import com.pinecone.hydra.storage.volume.VolumeTree;
import com.pinecone.hydra.storage.volume.entity.ArchReceiveEntity;
import com.pinecone.hydra.storage.volume.entity.ReceiveStorageObject;
import com.pinecone.hydra.storage.volume.entity.SpannedVolume;

import java.io.IOException;
import java.nio.channels.FileChannel;

public class TitanSpannedChannelReceiveEntity64 extends ArchReceiveEntity implements SpannedChannelReceiveEntity64{
    private FileChannel             channel;
    private SpannedVolume           spannedVolume;
    private SpannedChannelReceive64 spannedChannelReceive64;


    public TitanSpannedChannelReceiveEntity64(VolumeTree volumeTree, ReceiveStorageObject receiveStorageObject, String destDirPath, FileChannel channel, SpannedVolume spannedVolume) {
        super(volumeTree, receiveStorageObject, destDirPath);
        this.channel = channel;
        this.spannedVolume = spannedVolume;

    }

    @Override
    public FileChannel getChannel() {
        return this.channel;
    }

    @Override
    public void setChannel(FileChannel channel) {
        this.channel = channel;
    }

    @Override
    public SpannedVolume getSpannedVolume() {
        return this.spannedVolume;
    }

    @Override
    public MiddleStorageObject receive() throws IOException {
        return null;
    }

    @Override
    public MiddleStorageObject receive(Number offset, Number endSize) throws IOException {
        return null;
    }
}
