package com.pinecone.hydra.storage;

import java.nio.ByteBuffer;

import com.pinecone.framework.system.prototype.Pinenut;

public interface ChanfaceReader extends Pinenut {
    void afterRead( ByteBuffer out );

//    void afterRead( byte[] out );
}
