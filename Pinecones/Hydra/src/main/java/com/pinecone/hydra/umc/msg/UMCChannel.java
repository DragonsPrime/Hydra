package com.pinecone.hydra.umc.msg;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.hydra.umc.wolfmc.UlfChannelStatus;

import java.io.IOException;
import java.net.SocketAddress;

public interface UMCChannel extends Pinenut {
    Thread         getAffiliateThread();

    SocketAddress  getAddress();

    void           reconnect() throws IOException;

    Object         getNativeHandle();

    ChannelStatus  getChannelStatus();

    void           setChannelStatus( UlfChannelStatus status );

    MessageNode    getParentMessageNode();

    Object         getChannelID() ;

    long           getIdentityID();

    void           release();

    void           close();

    boolean        isShutdown();
}
