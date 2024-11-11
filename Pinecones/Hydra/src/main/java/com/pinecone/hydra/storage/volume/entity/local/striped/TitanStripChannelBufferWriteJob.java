package com.pinecone.hydra.storage.volume.entity.local.striped;

import com.pinecone.framework.util.Debug;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.ExportStorageObject;
import com.pinecone.hydra.storage.volume.entity.LogicVolume;
import com.pinecone.hydra.storage.volume.entity.local.striped.export.StripedChannelExport;
import com.pinecone.hydra.storage.volume.runtime.VolumeJobCompromiseException;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TitanStripChannelBufferWriteJob implements StripChannelBufferWriteJob {
    protected VolumeManager            volumeManager;
    protected List< byte[] >           buffers;

    protected ExportStorageObject      object;

    protected int                      jobNum;

    protected int                      jobCode;

    protected LogicVolume              volume;

    protected FileChannel              channel;
    protected AtomicInteger            currentBufferCode;

    protected AtomicInteger            counter;
    protected final Object             pipelineLock;
    protected List<Object>             lockGroup;

    protected StripLockEntity          lockEntity;

    protected BufferWriteStatus        status;
    ArrayList<TerminalStateRecord>     terminalStateRecordGroup;
    Number                             bufferToFileSize;

    public TitanStripChannelBufferWriteJob(StripedChannelExport stripedChannelExport, List<byte[]> buffers, int jobNum, int jobCode, StripLockEntity lockEntity, AtomicInteger counter, LogicVolume volume, ArrayList<TerminalStateRecord> terminalStateRecordGroup, Number bufferToFileSize  ){
        this.object                     = stripedChannelExport.getExportStorageObject();
        this.jobNum                     = jobNum;
        this.jobCode                    = jobCode;
        this.volumeManager              = stripedChannelExport.getVolumeManager();
        this.volume                     = volume;
        this.channel                    = stripedChannelExport.getFileChannel();
        this.buffers                    = buffers;
        this.currentBufferCode          = lockEntity.getCurrentBufferCode();
        this.pipelineLock               = lockEntity.getLockObject();
        this.lockGroup                  = lockEntity.getLockGroup();
        this.lockEntity                 = lockEntity;
        this.counter                    = counter;
        this.terminalStateRecordGroup   = terminalStateRecordGroup;
        this.bufferToFileSize           = bufferToFileSize;

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
        int num = 0;
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
                this.volume.channelRaid0Export( this.object, this.channel, this.buffers.get(this.currentBufferCode.get()), currentPosition, bufferSize, this.jobCode, this.jobNum, this.counter, this.lockEntity, terminalStateRecordGroup);
                currentPosition += bufferSize;



//                while (true) {
//                    // 使用 compareAndSet 实现安全增量
//                    int currentCount = counter.get();
//                    if (currentCount < 2 && counter.compareAndSet(currentCount, currentCount + 1)) {
//                        if (counter.get() == 2) {
////                            if (isLast) {
////                                int temporaryCurrentPosition = 0;
////                                terminalStateRecordGroup.sort(Comparator.comparing(TerminalStateRecord::getSequentialNumbering));
////                                int totalSize = terminalStateRecordGroup.stream()
////                                        .mapToInt(stateRecord -> stateRecord.getValidByteEnd().intValue() - stateRecord.getValidByteStart().intValue())
////                                        .sum();
////                                byte[] temporaryBuffer = new byte[totalSize];
////                                for (TerminalStateRecord stateRecord : terminalStateRecordGroup) {
////                                    int length = stateRecord.getValidByteEnd().intValue() - stateRecord.getValidByteStart().intValue();
////                                    ByteBuffer buffer = ByteBuffer.wrap(outputTarget, stateRecord.getValidByteStart().intValue(), length);
////                                    buffer.get(temporaryBuffer, temporaryCurrentPosition, length);
////                                    temporaryCurrentPosition += length;
////                                }
////                                outputTarget = temporaryBuffer;
////                            }
//
//                            counter.set(0);  // 重置 counter
//
//                            // 更新 buffer code 安全递增和重置
//                            if (lockEntity.getCurrentBufferCode().incrementAndGet() == 2) {
//                                lockEntity.getCurrentBufferCode().set(0);
//                            }
//
//                            lockEntity.unlockPipeStage();
//                            break;
//                        }
//                    }
//                    else {
//                        synchronized (lockEntity.getLockObject()) {
//                            try {
//                                lockEntity.getLockObject().wait();
//                                break;
//                            }
//                            catch (InterruptedException e) {
//                                Thread.currentThread().interrupt();  // 重设线程的中断状态
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                }




//                // 监工形态
//                boolean bNeedUnlock = true;
//                lockEntity.getMaoLock().lock();
//                try{
//                counter.incrementAndGet();
//
//                if( counter.get() == 2 ){
//                    counter.getAndSet( 0 );
//                    lockEntity.getCurrentBufferCode().incrementAndGet();
//                    if ( lockEntity.getCurrentBufferCode().get() == 2 ){
//                        lockEntity.getCurrentBufferCode().getAndSet( 0 );
//                    }
//
//                    Debug.traceSyn( "miao", Thread.currentThread().getName() );
//                    lockEntity.unlockPipeStage();
//                }
//                else {
//                    synchronized ( lockEntity.getLockObject() ){
//                        Debug.traceSyn( "shit" );
//                        try{
//                            lockEntity.getMaoLock().unlock();
//                            bNeedUnlock = false;
//                            lockEntity.getLockObject().wait();
//                        }
//                        catch ( InterruptedException e ) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }
//            finally {
//                if( bNeedUnlock ) {
//                    lockEntity.getMaoLock().unlock();
//                }
//            }


                this.setWritingStatus();

            }
            catch ( IOException | SQLException e ) {
                throw new VolumeJobCompromiseException( e );
            }
        }
        Debug.trace("我是线程"+jobCode+"我已经完成任务");
    }

}
