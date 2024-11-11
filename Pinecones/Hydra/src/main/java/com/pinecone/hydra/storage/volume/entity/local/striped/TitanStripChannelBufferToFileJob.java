package com.pinecone.hydra.storage.volume.entity.local.striped;

import com.pinecone.framework.util.Debug;
import com.pinecone.hydra.storage.volume.runtime.VolumeJobCompromiseException;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TitanStripChannelBufferToFileJob implements StripChannelBufferToFileJob {
    private List< byte[] >   bufferGroup;
    private AtomicInteger    currentBufferCode;
    private FileChannel      channel;
    private StripLockEntity  lockEntity;
    private Number           bufferSize;

    public TitanStripChannelBufferToFileJob( List< byte[] > bufferGroup, FileChannel channel, AtomicInteger currentBufferCode, StripLockEntity lockEntity, Number bufferSize ){
        this.bufferGroup       = bufferGroup;
        this.channel           = channel;
        this.currentBufferCode = currentBufferCode;
        this.lockEntity        = lockEntity;
        this.bufferSize        = bufferSize;
    }

    @Override
    public void execute() throws VolumeJobCompromiseException {
        while( true ){
            synchronized ( this.lockEntity.getLockObject() ){
                try{
                    Debug.trace("摸鱼罗");
                    this.lockEntity.getLockObject().wait();
                }
                catch ( InterruptedException e ) {
                    Thread.currentThread().interrupt();
                }
            }

            Debug.trace("开锁，执行写入");
            //todo 后面要实现跳出机制
            byte[] buffer = this.bufferGroup.get( this.currentBufferCode.get() );
            ByteBuffer writeBuffer = ByteBuffer.wrap( buffer, 0, this.bufferSize.intValue() );
            try {
                channel.write(writeBuffer);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
            Arrays.fill(buffer, (byte) 0);
        }

        //Debug.warnSyn( "wangwang" );
    }

}
