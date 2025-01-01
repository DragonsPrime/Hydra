package com.pinecone.hydra.umct.appoint;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.hydra.umc.msg.UMCMessage;

public interface AsynReturnHandler extends Pinenut {

    void onSuccessfulReturn( Object ret ) throws Exception ;

    void onErrorMsgReceived( UMCMessage msg ) throws Exception ;

    default void onError( Object data, Throwable cause ) {

    }
}
