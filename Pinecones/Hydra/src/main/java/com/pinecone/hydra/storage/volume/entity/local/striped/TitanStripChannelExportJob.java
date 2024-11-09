package com.pinecone.hydra.storage.volume.entity.local.striped;

import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.ExportStorageObject;
import com.pinecone.hydra.storage.volume.entity.LogicVolume;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

public class TitanStripChannelExportJob implements StripChannelExportJob {
    private VolumeManager           volumeManager;
    private byte[]                  firstBuffer;
    private byte[]                  secondBuffer;
    private ExportStorageObject     object;
    private int                     jobNum;
    private int                     jobCode;
    private LogicVolume             volume;
    private FileChannel             channel;
    private AtomicInteger           firstCounter;
    private AtomicInteger           secondCounter;

    public TitanStripChannelExportJob(ExportStorageObject object, byte[] firstBuffer, byte[] secondBuffer, int jobNum, int jobCode, VolumeManager volumeManager, LogicVolume volume, FileChannel channel, AtomicInteger firstCounter, AtomicInteger secondCounter){
        this.object = object;
        this.firstBuffer = firstBuffer;
        this.secondBuffer = secondBuffer;
        this. jobNum = jobNum;
        this.jobCode = jobCode;
        this.volumeManager = volumeManager;
        this.volume = volume;
        this.channel = channel;
        this.firstCounter = firstCounter;
        this.secondCounter = secondCounter;
    }
    @Override
    public void execute()  {
        long size = this.object.getSize().longValue();
        long stripSize = this.volumeManager.getConfig().getDefaultStripSize().longValue();
        long currentPosition = 0;
        int bufferStartPosition = (int) (jobCode * stripSize);
        int bufferEndPosition = (int) (bufferStartPosition + stripSize);
        while (true){
            long bufferSize = stripSize;
            if( currentPosition >= size ){
                break;
            }
            if( currentPosition + bufferSize > size ){
                bufferSize = size - currentPosition;
            }
            if( check( this.firstBuffer, bufferStartPosition, bufferEndPosition ) ){
                try {
                    this.volume.channelRaid0Export( this.object, this.channel, this.firstBuffer, currentPosition, bufferSize, this.jobCode, this.jobNum, this.firstCounter );
                } catch (IOException | SQLException e) {
                    throw new RuntimeException(e);
                }

                currentPosition += bufferSize;
            } else if( check( this.secondBuffer, bufferStartPosition, bufferEndPosition ) ){
                try {
                    this.volume.channelRaid0Export( this.object, this.channel, this.secondBuffer, currentPosition, bufferSize, this.jobCode, this.jobNum, this.secondCounter );
                } catch (IOException | SQLException e) {
                    throw new RuntimeException(e);
                }

                currentPosition += bufferSize;
            }
        }
    }

    private boolean check( byte[] buffer, int startPosition, int endPosition ){
        for( int i = startPosition; i < endPosition; i++ ){
            if ( buffer[ i ] != 0 ){
                return false;
            }
        }
        return true;
    }
}
