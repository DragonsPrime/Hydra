package com.pinecone.hydra.umc.wolfmc.server;

import com.pinecone.hydra.umc.msg.Recipient;
import com.pinecone.hydra.umc.wolfmc.UlfMessageNode;
import com.pinecone.hydra.umc.wolfmc.WolfMCNode;
import com.pinecone.hydra.umct.UMCTExpressHandler;

public interface UlfServer extends UlfMessageNode, Recipient {
    WolfMCNode apply( UMCTExpressHandler handler );
}
