package com.pinecone.hydra.umc.msg;

public interface InformMessage extends UMCMessage {
    @Override
    default InformMessage evinceInformMessage() {
        return this;
    }
}
