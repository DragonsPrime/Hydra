package com.pinecone.hydra.storage.volume.entity.local.striped;

import com.pinecone.framework.util.Debug;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

public class TitanStripChannelBufferToFileJob implements StripChannelBufferToFileJob {
    private byte[]          buffer;
    private FileChannel     channel;

    public TitanStripChannelBufferToFileJob( byte[] buffer, FileChannel channel ){
        this.buffer = buffer;
        this.channel = channel;
    }

    @Override
    public void execute() {
        ByteBuffer writeBuffer = ByteBuffer.wrap(buffer);
        try {
            channel.write(writeBuffer);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        Arrays.fill(buffer, (byte) 0);
        Debug.trace( "tofile done" );
    }
}
