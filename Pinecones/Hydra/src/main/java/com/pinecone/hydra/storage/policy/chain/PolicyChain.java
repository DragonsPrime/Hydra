package com.pinecone.hydra.storage.policy.chain;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.id.GUID;


public interface PolicyChain extends Pinenut {
    GUID execution( String filePath, String version );
}
