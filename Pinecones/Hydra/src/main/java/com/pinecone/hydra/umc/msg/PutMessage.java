package com.pinecone.hydra.umc.msg;

public interface PutMessage extends UMCMessage {
    @Override
    default PutMessage evincePutMessage() {
        return this;
    }
}
