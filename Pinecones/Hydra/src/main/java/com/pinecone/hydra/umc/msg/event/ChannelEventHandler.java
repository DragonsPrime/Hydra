package com.pinecone.hydra.umc.msg.event;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.hydra.umc.msg.RecipientChannelControlBlock;

public interface ChannelEventHandler extends Pinenut {
    void afterEventTriggered( RecipientChannelControlBlock block );
}
