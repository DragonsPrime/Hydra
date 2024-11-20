package com.pinecone.hydra.storage;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class FileChannelInputChannel implements InputChannel{
    private FileChannel             channel;
    private ByteBuffer              buffer;
    @Override
    public void read( ReadChannelRecalled recalled ) throws IOException {
        buffer.rewind();
        channel.position( channel.size() );
        channel.write( buffer );
    }
}
