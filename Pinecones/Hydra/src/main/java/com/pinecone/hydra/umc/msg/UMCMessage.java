package com.pinecone.hydra.umc.msg;

public interface UMCMessage extends Message {
    UMCHead     getHead();

    default UMCMethod   getMethod(){
        return this.getHead().getMethod();
    }

    default Object getExHead() {
        return this.getHead().getExtraHead();
    }

    default InformMessage evinceInformMessage() {
        return null;
    }

    default TransferMessage evinceTransferMessage() {
        return null;
    }
}
