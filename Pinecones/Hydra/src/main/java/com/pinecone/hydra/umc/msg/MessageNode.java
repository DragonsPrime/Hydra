package com.pinecone.hydra.umc.msg;

import com.pinecone.framework.system.executum.Processum;
import com.pinecone.hydra.system.Hydrarum;
import com.pinecone.hydra.umc.msg.extra.ExtraHeadCoder;
import com.pinecone.hydra.umc.msg.handler.ErrorMessageAudit;

public interface MessageNode extends Processum {
    Hydrarum             getSystem();

    long                 getMessageNodeId();

    ExtraHeadCoder       getExtraHeadCoder();

    ErrorMessageAudit    getErrorMessageAudit();

    void                 setErrorMessageAudit( ErrorMessageAudit audit );
}
