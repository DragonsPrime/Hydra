package com.pinecone.hydra.umb.broadcast;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.hydra.umb.UMBServiceException;
import com.pinecone.hydra.umb.UlfPackageMessageHandler;

public interface BroadcastConsumer extends Pinenut {

    void close();

    void start( UlfPackageMessageHandler handler ) throws UMBServiceException;
}
