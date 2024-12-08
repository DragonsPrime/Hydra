package com.pinecone.hydra.umct.appoint;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.hydra.umc.msg.UMCMessage;
import com.pinecone.hydra.umc.msg.UMCReceiver;
import com.pinecone.hydra.umc.msg.UMCTransmit;


public interface AsynMsgHandler extends Pinenut {
    default void onSuccessfulMsgReceived( UMCTransmit transmit, UMCReceiver receiver, UMCMessage msg ) throws Exception {
        this.onSuccessfulMsgReceived( msg );
    }

    void onSuccessfulMsgReceived( UMCMessage msg ) throws Exception ;

    default void onErrorMsgReceived( UMCTransmit transmit, UMCReceiver receiver, UMCMessage msg ) throws Exception {
        this.onErrorMsgReceived( msg );
    }

    void onErrorMsgReceived( UMCMessage msg ) throws Exception ;

    default void onError( Object data, Throwable cause ) {

    }
}
