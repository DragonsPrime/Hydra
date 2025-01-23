package com.pinecone.ulf.util.guid.worker;

import com.pinecone.framework.system.prototype.Pinenut;

public interface WorkerIdAssigner extends Pinenut {
    long assignWorkerId();
}
