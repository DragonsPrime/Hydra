package com.pinecone.hydra.umc.msg.handler;

import com.pinecone.hydra.umc.msg.MessageNodus;
import com.pinecone.hydra.umc.msg.UMCMessage;

public class GenericErrorMessageAudit implements ErrorMessageAudit {
    protected MessageNodus mMessageNode;

    public GenericErrorMessageAudit( MessageNodus node ) {
        this.mMessageNode = node;
    }

    @Override
    public boolean isErrorMessage( UMCMessage message ) {
        return message.getHead().getStatus().getValue() >= 500;
    }
}
