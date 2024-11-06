package com.pinecone.hydra.storage.volume.entity.local.striped;

import com.pinecone.hydra.storage.MiddleStorageObject;
import com.pinecone.hydra.storage.file.transmit.receiver.channel.ChannelReceiverEntity;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.LogicVolume;
import com.pinecone.hydra.storage.volume.entity.ReceiveEntity;
import com.pinecone.hydra.storage.volume.entity.ReceiveStorageObject;

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.sql.SQLException;

public class TitanStripChannelReceiverJob implements StripChannelReceiverJob{
    private LogicVolume             volume;
    private int                     jobNum;
    private int                     jobCode;
    private VolumeManager           volumeManager;
    private ReceiveStorageObject    object;
    private String                  destDirPath;
    private FileChannel             fileChannel;

    public  TitanStripChannelReceiverJob(ReceiveEntity entity, FileChannel channel, int jobNum, int jobCode, LogicVolume volume){
        this.volumeManager      =   entity.getVolumeManager();
        this.object             =    entity.getReceiveStorageObject();
        this.destDirPath        =   entity.getDestDirPath();
        this.fileChannel        =   channel;
        this.jobNum             =   jobNum;
        this.jobCode            =   jobCode;
        this.volume             =   volume;
    }

    @Override
    public void execute() throws SQLException, IOException {
        //每次计算要保存的部分
        long size = this.object.getSize().longValue();
        int sequenceNumber = jobCode;
        long stripSize = this.volumeManager.getConfig().getDefaultStripSize().longValue();
        long currentPosition = jobCode * stripSize;

        while( true ){
            long bufferSize = stripSize;
            if( currentPosition > size ){
                break;
            }
            if( currentPosition + bufferSize > size ){
                bufferSize = size - currentPosition;
            }

            MiddleStorageObject middleStorageObject = this.volume.channelReceive(this.object, this.destDirPath, this.fileChannel, currentPosition, bufferSize);

            currentPosition += bufferSize;
        }
    }
}
