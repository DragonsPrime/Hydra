package com.pinecone.hydra.umc.msg;

import java.util.Map;

/**
 *  Pinecone Ursus For Java UMCProtocol [ Unified Message Control Protocol ]
 *  Author: Harold.E / JH.W (DragonKing)
 *  Copyright © 2008 - 2028 Bean Nuts Foundation All rights reserved.
 */
public interface UMCProtocol extends MsgProtocol {

    UMCProtocol applyMessageSource( Medium medium ) ;

    String getVersion();

    String getSignature();

    UMCHead getHead();

    default void setHead( String szKey, Object val ) {
        this.getHead().putExHeaderVal( szKey, val );
    }

    default Object getHead( String szKey ) {
        return this.getHead().getExHeaderVal( szKey );
    }

    void setHead  ( UMCHead head );

    default void setExHead( Map<String, Object > jo ) {
        this.getHead().setExtraHead( jo );
    }

    default void setExHead( Object jo ) {
        this.getHead().setExtraHead( jo );
    }

    void release();

}
