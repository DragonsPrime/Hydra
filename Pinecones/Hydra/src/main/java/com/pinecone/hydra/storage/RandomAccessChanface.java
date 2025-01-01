package com.pinecone.hydra.storage;

import java.io.IOException;

public interface RandomAccessChanface extends Chanface{
    void mark(int readlimit);
    void reset() throws IOException;
}
