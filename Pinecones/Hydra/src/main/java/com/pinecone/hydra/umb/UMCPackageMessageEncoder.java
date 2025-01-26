package com.pinecone.hydra.umb;

import java.io.IOException;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.hydra.umc.msg.UMCMessage;

public interface UMCPackageMessageEncoder extends Pinenut {
    byte[] encode( UMCMessage message ) throws IOException;
}
