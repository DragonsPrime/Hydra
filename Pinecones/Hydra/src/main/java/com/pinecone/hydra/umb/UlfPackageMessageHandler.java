package com.pinecone.hydra.umb;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.hydra.umc.msg.Medium;
import com.pinecone.hydra.umc.msg.UMCMessage;
import com.pinecone.hydra.umc.msg.UMCReceiver;
import com.pinecone.hydra.umc.msg.UMCTransmit;

public interface UlfPackageMessageHandler extends Pinenut {
    default void onSuccessfulMsgReceived ( byte[] body, Object[] args ) throws Exception {

    }

    default void onErrorMsgReceived      ( byte[] body, Object[] args ) throws Exception {

    }

    default void onError                 ( Object data, Throwable cause ) {

    }
}
