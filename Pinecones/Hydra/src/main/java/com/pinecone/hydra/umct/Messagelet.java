package com.pinecone.hydra.umct;

import com.pinecone.hydra.umc.msg.UMCMessage;
import com.pinecone.hydra.umc.msg.UMCReceiver;
import com.pinecone.hydra.umc.msg.UMCTransmit;
import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.hydra.system.Hydrarum;

import java.util.Map;

public interface Messagelet extends Pinenut {
    Hydrarum getSystem();

    UMCMessage getReceivedMessage();

    UMCTransmit getTransmit();

    UMCReceiver getReceiver();

    MessageDeliver getMessageDeliver();

    UMCConnection getMessagePackage();

    ArchMessagram getMessagelet();

    Map<String, Object > getConfig();

    Object     getLetLocal();

    void dispatch();

    void terminate();

    String serviceName();
}
