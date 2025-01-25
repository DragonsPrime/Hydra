package com.pinecone.hydra.umc.msg;

import java.util.Map;

import com.pinecone.hydra.umc.msg.extra.ExtraHeadCoder;

public interface UMCHead extends EMCHead {
    ExtraHeadCoder getExtraHeadCoder();

    UMCMethod       getMethod();

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



    void setStatus        ( Status status          );

    void setKeepAlive     ( long nKeepAlive        );

    void setControlBits   ( long controlBits       );

    void setIdentityId    ( long identityId        );

    void setSessionId     ( long sessionId         );




    void release();

    default AbstractUMCHead inface() {
        return (AbstractUMCHead) this;
    }
}
