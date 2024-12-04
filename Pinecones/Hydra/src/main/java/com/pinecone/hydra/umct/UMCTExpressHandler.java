package com.pinecone.hydra.umct;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.Debug;
import com.pinecone.hydra.umc.msg.Medium;
import com.pinecone.hydra.umc.msg.UMCMessage;
import com.pinecone.hydra.umc.msg.UMCReceiver;
import com.pinecone.hydra.umc.msg.UMCTransmit;

public interface UMCTExpressHandler extends Pinenut {
    default void onSuccessfulMsgReceived ( Medium medium, UMCTransmit transmit, UMCReceiver receiver, UMCMessage msg, Object[] args ) throws Exception {
        Debug.warn( "Unimplemented method `onSuccessfulMsgReceived`.", msg );
    }

    default void onSuccessfulMsgReceived ( UMCConnection connection, Object[] args ) throws Exception {
        Debug.warn( "Unimplemented method `onSuccessfulMsgReceived`.", connection.getMessage() );
    }

    default void onErrorMsgReceived      ( Medium medium, UMCTransmit transmit, UMCReceiver receiver, UMCMessage msg, Object[] args ) throws Exception {
        Debug.warn( "Unimplemented method `onErrorMsgReceived`.", msg );
    }

    default void onError                 ( Object data, Throwable cause ) {
        Debug.warn( "Unimplemented method `onError`." );
    }
}
