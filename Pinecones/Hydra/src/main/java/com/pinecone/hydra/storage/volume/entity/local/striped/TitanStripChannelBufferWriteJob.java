package com.pinecone.hydra.storage.volume.entity.local.striped;

import com.pinecone.framework.util.Debug;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.ExportStorageObject;
import com.pinecone.hydra.storage.volume.entity.LogicVolume;
import com.pinecone.hydra.storage.volume.entity.local.striped.export.StripedChannelExport;
import com.pinecone.hydra.storage.volume.runtime.VolumeJobCompromiseException;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TitanStripChannelBufferWriteJob implements StripChannelBufferWriteJob {
    protected VolumeManager           volumeManager;
    protected List< byte[] >          buffers;
    protected ExportStorageObject     object;
    protected int                     jobNum;
    protected int                     jobCode;
    protected LogicVolume             volume;
    protected FileChannel             channel;
    protected AtomicInteger           currentBufferCode;
    protected AtomicInteger           counter;
    protected final Object            pipelineLock;
    protected List<Object>            lockGroup;
    protected StripLockEntity         lockEntity;
    protected BufferWriteStatus       status;

    public TitanStripChannelBufferWriteJob(StripedChannelExport stripedChannelExport, List<byte[]> buffers, int jobNum, int jobCode, StripLockEntity lockEntity, AtomicInteger counter, LogicVolume volume ){
        this.object             = stripedChannelExport.getExportStorageObject();
        this.jobNum             = jobNum;
        this.jobCode            = jobCode;
        this.volumeManager      = stripedChannelExport.getVolumeManager();
        this.volume             = volume;
        this.channel            = stripedChannelExport.getFileChannel();
        this.buffers            = buffers;
        this.currentBufferCode  = lockEntity.getCurrentBufferCode();
        this.pipelineLock       = lockEntity.getLockObject();
        this.lockGroup          = lockEntity.getLockGroup();
        this.lockEntity         = lockEntity;
        this.counter            = counter;

        this.setWritingStatus();
    }

    @Override
    public BufferWriteStatus getStatus() {
        return this.status;
    }

    protected void setWritingStatus() {
        this.status = BufferWriteStatus.Writing;
    }

    protected void setSuspendedStatus() {
        this.status = BufferWriteStatus.Suspended;
    }

    protected void setMasteringStatus() {
        this.status = BufferWriteStatus.Mastering;
    }

    protected void setExitingStatus() {
        this.status = BufferWriteStatus.Exiting;
    }


    @Override
    public void execute() throws VolumeJobCompromiseException {
        long size = this.object.getSize().longValue();
        long stripSize = this.volumeManager.getConfig().getDefaultStripSize().longValue();
        long currentPosition    = 0;
        int bufferStartPosition = (int) (jobCode * stripSize);
        int bufferEndPosition   = (int) (bufferStartPosition + stripSize);

        while ( true ){
            long bufferSize = stripSize;
            if( currentPosition >= size ){
                this.setExitingStatus();
                break;
            }
            if( currentPosition + bufferSize > size ){
                bufferSize = size - currentPosition;
            }
            try {
                this.volume.channelRaid0Export( this.object, this.channel, this.buffers.get(this.currentBufferCode.get()), currentPosition, bufferSize, this.jobCode, this.jobNum, this.counter, this.lockEntity );
                currentPosition += bufferSize;


                Debug.traceSyn( Thread.currentThread().getName(),"I am boss" );
                // 监工形态
                // TODO
                Debug.infoSyn( "sss", counter.get(), counter.hashCode() );
//                synchronized ( this.counter ) {
//
//                    if( this.counter.get() == 2 ){
//                        this.setMasteringStatus();
//                        this.counter.getAndSet( 0 );
//                        this.lockEntity.getCurrentBufferCode().incrementAndGet();
//                        if ( this.lockEntity.getCurrentBufferCode().get() == 2 ){
//                            this.lockEntity.getCurrentBufferCode().getAndSet( 0 );
//                        }
//
//                        Debug.traceSyn( "miao", Thread.currentThread().getName() );
//                        this.lockEntity.unlockPipeStage();
//                    }
//                    else {
//                        synchronized ( this.pipelineLock ){
//                            this.setSuspendedStatus();
//                            Debug.traceSyn( "shit" );
//                            this.pipelineLock.wait();
//                        }
//                    }
//                }

                this.setWritingStatus();

                Debug.traceSyn(  Thread.currentThread().getName() );
            }
            catch ( IOException | SQLException e ) {
                throw new VolumeJobCompromiseException( e );
            }
//            catch ( InterruptedException ie ) {
//                Thread.currentThread().interrupt();
//            }
        }

        Debug.trace( Thread.currentThread().getName(),"miaomiao" );
    }

}
