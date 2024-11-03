package com.pinecone.hydra.umc.msg;

public interface PostMessage extends UMCMessage {
    @Override
    default PostMessage evincePostMessage() {
        return this;
    }

    Object      getBody() ;
}
