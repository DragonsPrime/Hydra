package com.pinecone.hydra.storage;

import com.pinecone.hydra.storage.io.Chanface;

import java.io.IOException;

public interface RandomAccessChanface extends Chanface {
    void mark(int readlimit);
    void reset() throws IOException;
}
