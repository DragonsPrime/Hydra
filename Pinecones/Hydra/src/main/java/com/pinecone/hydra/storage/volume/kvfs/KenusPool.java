package com.pinecone.hydra.storage.volume.kvfs;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.rdb.MappedExecutor;

public interface KenusPool extends Pinenut {
    MappedExecutor allot(String name);

}
