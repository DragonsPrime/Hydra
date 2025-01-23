package com.pinecone.framework.util.id;

import com.pinecone.framework.system.prototype.Pinenut;

public interface GuidAllocator extends Pinenut {

    GUID nextGUID();

}
