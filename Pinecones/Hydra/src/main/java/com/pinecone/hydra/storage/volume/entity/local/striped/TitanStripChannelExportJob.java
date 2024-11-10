package com.pinecone.hydra.storage.volume.entity.local.striped;

import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.ExportStorageObject;
import com.pinecone.hydra.storage.volume.entity.LogicVolume;
import com.pinecone.hydra.storage.volume.entity.local.striped.export.StripedChannelExport;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TitanStripChannelExportJob implements StripChannelExportJob {
    private VolumeManager           volumeManager;
    private List< byte[] >          buffers;
    private ExportStorageObject     object;
    private int                     jobNum;
    private int                     jobCode;
    private LogicVolume             volume;
    private FileChannel             channel;
    private AtomicInteger           currentBufferCode;
    private AtomicInteger           counter;
    private Object                  lockObject;
    private List<Object>            lockGroup;
    private StripLockEntity         lockEntity;


    public TitanStripChannelExportJob(StripedChannelExport stripedChannelExport, List<byte[]> buffers, int jobNum, int jobCode, StripLockEntity lockEntity, AtomicInteger counter, LogicVolume volume){
        this.object             =   stripedChannelExport.getExportStorageObject();
        this. jobNum            =   jobNum;
        this.jobCode            =   jobCode;
        this.volumeManager      =   stripedChannelExport.getVolumeManager();
        this.volume             =   volume;
        this.channel            =   stripedChannelExport.getFileChannel();
        this.buffers            =   buffers;
        this.currentBufferCode  =   lockEntity.getCurrentBufferCode();
        this.lockObject         =   lockEntity.getLockObject();
        this.lockGroup          =   lockEntity.getLockGroup();
        this.lockEntity         =   lockEntity;
        this.counter            =   counter;
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
            try {
                this.volume.channelRaid0Export( this.object, this.channel, this.buffers.get(this.currentBufferCode.get()), currentPosition, bufferSize, this.jobCode, this.jobNum, this.counter, this.lockEntity );
                currentPosition += bufferSize;
                synchronized ( this.lockObject ){
                    this.lockObject.wait();
                }
            } catch (IOException | SQLException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
