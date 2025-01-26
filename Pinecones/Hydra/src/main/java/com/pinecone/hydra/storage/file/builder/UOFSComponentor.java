package com.pinecone.hydra.storage.file.builder;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.hydra.storage.file.KOMFileSystem;

public interface UOFSComponentor extends Pinenut {
    void apply( KOMFileSystem fs );

    Feature getFeature();

}
