package com.pinecone.hydra.umc.msg;

import java.util.concurrent.atomic.AtomicInteger;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.hydra.umc.msg.extra.ExtraHeadCoder;
import com.pinecone.hydra.umc.msg.handler.ErrorMessageAudit;

public interface MessageNodus extends Pinenut {
    AtomicInteger LocalNodeIdAllocator = new AtomicInteger( 0 );

    static int nextLocalId() {
        return MessageNodus.LocalNodeIdAllocator.getAndIncrement();
    }

    long                 getMessageNodeId();

    ExtraHeadCoder       getExtraHeadCoder();

    ErrorMessageAudit    getErrorMessageAudit();

    void                 setErrorMessageAudit( ErrorMessageAudit audit );
}
