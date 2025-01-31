package com.pinecone.hydra.storage;

import com.pinecone.framework.system.prototype.Pinenut;

public interface UFile extends Pinenut {
    String getName();

    Number size();

    //String getPath();

}
