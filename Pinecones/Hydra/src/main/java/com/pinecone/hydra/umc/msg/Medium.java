package com.pinecone.hydra.umc.msg;

import com.pinecone.framework.system.prototype.Pinenut;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface Medium extends Pinenut {
    Object getNativeMessageSource();

    OutputStream getOutputStream();

    InputStream getInputStream();

    default byte[] receive( int nLength ) throws IOException {
        return this.getInputStream().readNBytes( nLength );
    }

    default void send   ( byte[] bytes, int off, int n ) throws IOException {
        this.getOutputStream().write( bytes, off, n );
    }

    default void send   ( byte[] bytes ) throws IOException {
        this.getOutputStream().write( bytes, 0, bytes.length );
    }

    String sourceName();

    void release();

    MessageNodus getMessageNode();
}
