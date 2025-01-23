package com.pinecone.hydra.umc.msg;

import java.util.Map;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.hydra.umc.msg.extra.ExtraHeadCoder;

public interface UMCHead extends Pinenut {
    ExtraHeadCoder getExtraHeadCoder();

    String          getSignature();

    int             getSignatureLength();

    UMCMethod       getMethod();

    int             getExtraHeadLength();

    long            getBodyLength();

    long            getKeepAlive();

    long            getSessionId();

    Status          getStatus();

    ExtraEncode     getExtraEncode();

    long            getControlBits();

    long            getIdentityId();

    byte[]          getExtraHeadBytes();

    Map<String, Object > evalMapExtraHead() ;

    Map<String, Object > getMapExtraHead() ;

    Object getExtraHead();

    Object getExHeaderVal( String key );

    void putExHeaderVal( String key, Object val ) throws IllegalArgumentException;


    void setKeepAlive     ( long nKeepAlive        );

    void setControlBits   ( long controlBits       );

    void setSessionId     ( long sessionId         );

    void setIdentityId    ( long identityId        );

    void setStatus        ( Status status          );


    void release();

    default AbstractUMCHead inface() {
        return (AbstractUMCHead) this;
    }
}
