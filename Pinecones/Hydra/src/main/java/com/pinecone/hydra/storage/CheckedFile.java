package com.pinecone.hydra.storage;

public interface CheckedFile extends UFile {

    long getChecksum();

    void setChecksum(long checksum);

    int getParityCheck();

    void setParityCheck(int parityCheck);


}
