package com.pinecone.hydra.umc.wolfmc;

import com.pinecone.hydra.umc.msg.MessageStereotypes;

public class UlfMessageStereotypes implements MessageStereotypes {
    public static final MessageStereotypes Default = new UlfMessageStereotypes();

    protected Class<? > putType        = UlfInformMessage.class;
    protected Class<? > postBytesType  = UlfBytesTransferMessage.class;
    protected Class<? > postStreamType = UlfStreamTransferMessage.class;

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
