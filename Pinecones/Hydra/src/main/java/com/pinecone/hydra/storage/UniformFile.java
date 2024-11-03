package com.pinecone.hydra.storage;

import com.pinecone.framework.system.prototype.Pinenut;

public interface UniformFile extends Pinenut {
    String getName();

    Number size();

    long getChecksum();
    void setChecksum(long checksum);

    int getParityCheck();
    void setParityCheck(int parityCheck);


}
