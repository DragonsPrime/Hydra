package com.pinecone.hydra.umc.msg;

public interface TransferMessage extends UMCMessage {
    @Override
    default TransferMessage evinceTransferMessage() {
        return this;
    }

    Object      getBody() ;
}
