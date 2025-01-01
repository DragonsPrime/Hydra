package com.pinecone.hydra.umct;

import com.pinecone.hydra.umc.msg.UMCMessage;

public interface UMCTExpress extends MessageExpress, UMCTExpressHandler {
    UMCMessage processResponse( UMCMessage request, UMCMessage response );
}
