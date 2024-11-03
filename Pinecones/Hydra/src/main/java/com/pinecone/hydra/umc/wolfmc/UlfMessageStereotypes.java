package com.pinecone.hydra.umc.wolfmc;

import com.pinecone.hydra.umc.msg.MessageStereotypes;

public class UlfMessageStereotypes implements MessageStereotypes {
    public static final MessageStereotypes Default = new UlfMessageStereotypes();

    protected Class<? > putType        = UlfPutMessage.class;
    protected Class<? > postBytesType  = UlfBytesPostMessage.class;
    protected Class<? > postStreamType = UlfStreamPostMessage.class;

    @Override
    public Class<? > putType() {
        return this.putType;
    }

    @Override
    public Class<? > postBytesType() {
        return this.postBytesType;
    }

    @Override
    public Class<? > postStreamType() {
        return this.postStreamType;
    }
}
