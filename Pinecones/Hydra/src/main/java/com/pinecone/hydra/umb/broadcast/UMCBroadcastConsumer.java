package com.pinecone.hydra.umb.broadcast;

import com.pinecone.hydra.umb.UMBServiceException;
import com.pinecone.hydra.umct.UMCTExpressHandler;

public interface UMCBroadcastConsumer extends BroadcastConsumer {

    void start( UMCTExpressHandler handler ) throws UMBServiceException ;

}
