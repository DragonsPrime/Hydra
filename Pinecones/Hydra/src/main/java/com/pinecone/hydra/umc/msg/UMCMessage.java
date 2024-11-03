package com.pinecone.hydra.umc.msg;

public interface UMCMessage extends Message {
    UMCHead     getHead();

    default UMCMethod   getMethod(){
        return this.getHead().getMethod();
    }

    default Object getExHead() {
        return this.getHead().getExtraHead();
    }

    default PutMessage evincePutMessage() {
        return null;
    }

    default PostMessage evincePostMessage() {
        return null;
    }
}
